package com.skyun.recon.util.database.ibatis;

import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;
import com.skyun.app.util.config.AppConfig;


public class SqlMapInstance {
	
	private static SqlMapClient sqlMap=null;
	private static Logger logger = LoggerFactory.getLogger(SqlMapClient.class);

	static {
		try {
			FileReader reader = new FileReader(AppConfig.getProperty("config.configfile.path")+"/"+"SqlMapConfig.xml");
			
			Properties per=new Properties();
			per.load(new FileInputStream( AppConfig.currentDir + "/conf/server.properties"));
			
			String pdecode=AppConfig.getProperty("config.pic.db.password");
			String udecode=AppConfig.getProperty("config.pic.db.username");
			
			per.setProperty("config.pic.db.password",pdecode );
			per.setProperty("config.pic.db.username",udecode );
			
			sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader, per) ;
			reader.close();
			
		} catch (Exception e) {
			logger.info("DB Connect error _________________ check DB env");			
			logger.info("DB url           :"+AppConfig.getProperty("config.pic.db.url"));
			logger.info("DB password      :"+AppConfig.getProperty("config.pic.db.password"));
			logger.info("DB username      :"+AppConfig.getProperty("config.pic.db.username"));
			logger.info("exception        :"+e.getMessage());
			System.exit(-1);
		}
	}

	
	public static SqlMapClient getSqlMapInstance()  {
		return sqlMap;
	}	
}
