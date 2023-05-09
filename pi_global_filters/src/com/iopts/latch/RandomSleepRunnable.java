package com.iopts.latch;
 
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.skyun.recon.util.database.ibatis.SqlMapInstance;

public class RandomSleepRunnable implements Runnable {
	
	private  CountDownLatch lacth;
	
	private static SqlMapClient sqlMap = SqlMapInstance.getSqlMapInstance();
	private int id = 0;
	private static Logger logger = LoggerFactory.getLogger(RandomSleepRunnable.class);
	

	public RandomSleepRunnable(int id,CountDownLatch l) {
		this.id = id;
		this.lacth=l;
		
	}

	@Override
	public void run() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
            if (this.lacth == null)
                return;
           this.lacth.countDown();
           
           logger.info("Thread end "+this.id);
		}
					
	}
}

