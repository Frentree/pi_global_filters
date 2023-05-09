package com.iopts.shell;

public interface ProcessExecutorHandler {
	public void onStandardOutput(String line);
	public void onStandardError(String line);
}
