package com.iopts.shell;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.OS;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;

import com.skyun.app.util.config.AppConfig;
import com.skyun.app.util.config.OSInfo;


/*
 * 			ShellExecuteVo retobj = (ShellExecuteVo) new ShellExecuteInfo((ShellExecuteVo) obj).getObject();
 */
public class ShellExecute {
	public ShellExecuteVo shell;
	public static final Long WATCHDOG_EXIST_VALUE = -999L;
	private long startTime = 0l;
	private static long endTime = 0l;
	private String uuid = "";

	public ShellExecute() {
		
	}

	public ShellExecute(ShellExecuteVo arg) {
		this.shell = arg;
		if (this.shell.getShellid() == null || this.shell.getShellid().equals("null")) {
			this.uuid = UUID.randomUUID().toString();
		} else {
			this.uuid = UUID.randomUUID().toString();
		}
	}

	public static Future<Long> runProcess(final CommandLine commandline, final ProcessExecutorHandler handler, final long watchdogTimeout, ShellExecuteVo shell) throws IOException {

		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<Long> result = executor.submit(new ProcessCallable(watchdogTimeout, handler, commandline));
		executor.shutdown();

		return result;
	}

	private static class ProcessCallable implements Callable<Long> {

		private long watchdogTimeout=0;
		private ProcessExecutorHandler handler;
		private CommandLine commandline;

		private ProcessCallable(long watchdogTimeout, ProcessExecutorHandler handler, CommandLine commandline) {
			this.watchdogTimeout = watchdogTimeout;
			this.handler = handler;
			this.commandline = commandline;
		}

		public Long call() throws Exception {
			Executor executor = new DefaultExecutor();
			executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
			ExecuteWatchdog watchDog = new ExecuteWatchdog(watchdogTimeout);
			executor.setWatchdog(watchDog);
			executor.setStreamHandler(new PumpStreamHandler(new MyLogOutputStream(handler, true), new MyLogOutputStream(handler, false)));

			Long exitValue;

			try {
				exitValue = new Long(executor.execute(commandline));
			} catch (ExecuteException e) {
				exitValue = new Long(e.getExitValue());
				endTime = System.currentTimeMillis();
				
			} finally {
				endTime = System.currentTimeMillis();
			}

			if (watchDog.killedProcess()) {
				exitValue = WATCHDOG_EXIST_VALUE;
				endTime = System.currentTimeMillis();
			}

			return exitValue;
		}
	}

	private static class MyLogOutputStream extends LogOutputStream {

		private ProcessExecutorHandler handler;
		private boolean forewordToStandardOutput;

		private MyLogOutputStream(ProcessExecutorHandler handler, boolean forewordToStandardOutput) {
			this.handler = handler;
			this.forewordToStandardOutput = forewordToStandardOutput;
		}

		@Override
		protected void processLine(String line, int level) {
			if (forewordToStandardOutput) {
				handler.onStandardOutput(line);
			} else {
				handler.onStandardError(line);
			}
		}
	}

	public Object getObject() {

		makeTempFile();
		startTime = System.currentTimeMillis();

		CommandLine cl = null;

		if (OSInfo.getOs().toString().equals("WINDOWS")) {
			cl = CommandLine.parse(resolveScriptForOS(this.uuid));
		} else {
			if (this.shell.getAccount().equals("root")) {
				cl = CommandLine.parse(resolveScriptForOS(this.uuid));
			} else {
				cl = CommandLine.parse("su - " + this.shell.getAccount() + " -c '" + resolveScriptForOS(this.uuid + "'"));
			}
		}
		

		try {
			Future<Long> exitValue = runProcess(cl, new ProcessExecutorHandler() {
				public void onStandardOutput(String line) {
					shell.setStdout(new StringBuffer(line + "\n"));
				}

				public void onStandardError(String line) {
					shell.setStderr(new StringBuffer(line + "\n"));
				}
			}, this.shell.getTimeout(), shell);

			try {
				this.shell.setExitcode(exitValue.get());
			} catch (InterruptedException e) {
				this.shell.setExitcode(-997);
				this.shell.setStderr(new StringBuffer(e.getLocalizedMessage()));

			} catch (ExecutionException e) {
				this.shell.setExitcode(-998);
				this.shell.setStderr(new StringBuffer(e.getLocalizedMessage()));
			}

		} catch (IOException e) {
			this.shell.setStderr(new StringBuffer("Shell Error ErrorMesg [" + e.getMessage() + "]"));
			this.shell.setExitcode(-9999);
		} finally {			
			File f = new File(resolveScriptForOS(this.uuid));
			if (!f.delete()) {
				System.out.println("Delete Error __________________");
			}
			
		}

		return this.shell;
	}

	public void makeTempFile() {
		String fname = resolveScriptForOS(this.uuid);

		try {

			File file = new File(fname);
			FileWriter fw = new FileWriter(file);
			fw.write(this.shell.getShell().toString());
			fw.close();

			if (!OSInfo.getOs().toString().equals("WINDOWS")) {
				setChmod(fname);
			}

		} catch (IOException e) {
			System.out.println(fname + "  File Create Error __________________" + e.getMessage());
		}

	}

	public void setChmod(String shellname) {

		try {
			Class<?> fspClass = Class.forName("java.util.prefs.FileSystemPreferences");
			Method chmodMethod = fspClass.getDeclaredMethod("chmod", String.class, Integer.TYPE);
			chmodMethod.setAccessible(true);
			chmodMethod.invoke(null, shellname, 0777);
		} catch (Throwable ex) {
			System.out.println("File Permission error______________");
		}

	}

	public String resolveScriptForOS(String shellname) {

		if (OS.isFamilyWindows()) {
			shellname = AppConfig.currentDir + "/shell/" + shellname + ".bat";
		} else if (OS.isFamilyUnix()) {
			shellname = AppConfig.currentDir + "/shell/" + shellname + ".sh";
		} else if (OS.isFamilyOpenVms()) {
			shellname = AppConfig.currentDir + "/shell/" + shellname + ".dcl";
		} else {
			System.out.println("Unknown OS _____________ default make");
			shellname = AppConfig.currentDir + "/shell/" + shellname + ".sh";
		}

		return shellname;
	}
}
