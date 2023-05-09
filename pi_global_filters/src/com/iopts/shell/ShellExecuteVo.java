package com.iopts.shell;

public class ShellExecuteVo {
	private String account;
	private StringBuffer Shell;
	private StringBuffer stdout = new StringBuffer();
	private StringBuffer stderr = new StringBuffer();;
	private long timeout;
	private long exitcode;
	private String shellid;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public StringBuffer getShell() {
		return Shell;
	}

	public void setShell(StringBuffer shell) {
		Shell = shell;
	}

	public StringBuffer getStdout() {
		return stdout;
	}

	public void setStdout(StringBuffer stdout) {
		this.stdout.append(stdout);
	}

	public StringBuffer getStderr() {
		return stderr;
	}

	public void setStderr(StringBuffer stderr) {
		this.stderr.append(stderr);
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public long getExitcode() {
		return exitcode;
	}

	public void setExitcode(long exitcode) {
		this.exitcode = exitcode;
	}

	public String getShellid() {
		return shellid;
	}

	public void setShellid(String shellid) {
		this.shellid = shellid;
	}

}
