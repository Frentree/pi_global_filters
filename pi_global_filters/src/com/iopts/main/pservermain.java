 package com.iopts.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iopts.scheduler.globalFilters;
import com.skyun.app.util.config.AppConfig;

public class pservermain {
	private static String CONF_PATH = null;
	private static String LOGJ_PATH = null;
	private static String PID = null;
	public static String currentDir = null;
	
	private static String customer_id = "";

	public static void main(String[] args) {
		currentDir = System.getProperty("user.dir");
		File f = new File(currentDir);
		currentDir = f.getParent().toString();

		LOGJ_PATH = currentDir + "/conf/logbackFilters.xml";
		System.setProperty("logback.configurationFile", LOGJ_PATH);
		Logger logger = LoggerFactory.getLogger(pservermain.class);
		AppConfig.setPID(getPID() + "");
		wrtiePID(AppConfig.getPID());
		

		customer_id = AppConfig.getProperty("config.customer");

		logger.info(">> Process ID :" + AppConfig.getPID());
		logger.info(">> Home Dir :" + AppConfig.currentDir);
		logger.info(">> System Version  2023-03-23__________________ ");
		logger.info(">> System Version  2023-03-23 (Mod)__________________ ");
		logger.info(">> System Version  2023-03-23 Patch list");
		logger.info(">> 		1> global filters insert");
		logger.info(">> 		2> customer ID :: " + customer_id);


		new globalFilters();
		
		
	}

	public static long getPID() {
		String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
		return Long.parseLong(processName.split("@")[0]);
	}

	public static void wrtiePID(String pid) {
		BufferedWriter out = null;
		try {
			
			out = new BufferedWriter(new FileWriter(AppConfig.currentDir + "/psid"));
			out.write(pid);
			
		} catch (IOException e) {
			System.err.println(e); 
			System.exit(1);
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
