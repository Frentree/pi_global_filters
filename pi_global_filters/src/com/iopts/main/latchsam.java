package com.iopts.main;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iopts.latch.RandomSleepRunnable;
import com.skyun.app.util.config.AppConfig;

public class latchsam {

	private static Logger logger = LoggerFactory.getLogger(latchsam.class);
	
	public static void main(String[] args) {
		
		
		logger.info(">> Process ID :" + AppConfig.getPID());
		logger.info(">> Home Dir :" + AppConfig.currentDir);


		while (true) {
			
			logger.info("test");
			service();

			try {
				Thread.sleep((10 * 1000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private static void service() {
		int THREADS = 100;
		CountDownLatch lacth = new CountDownLatch(THREADS);
		
		logger.info("Service ......................................"+ lacth.getCount());

		for (int i = 0; i < THREADS; ++i) {
			new Thread(new RandomSleepRunnable(i, lacth)).start();
		}

		try {
			// lacth.await();
			lacth.await(30000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("All threads terminated." + lacth.getCount());

	}

}
