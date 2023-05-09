package com.iopts.scheduler.queue;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.skyun.app.util.config.AppConfig;

public class QueueStaticPool {

	static HashMap<String,String> exception_hash = null;

	static {
		exception_hash=new HashMap<>();
	}

	public static HashMap<String, String> getException_hash() {
		return exception_hash;
	}

	public static void setException_hash(HashMap<String, String> h) {
		exception_hash.clear();
		exception_hash = h;
	}
	

}
