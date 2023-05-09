package com.iopts.scheduler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.iopts.skyun.recon.vo.groupall.globalFiltersCo;
import com.opencsv.CSVWriter;
import com.skyun.app.util.config.AppConfig;
import com.skyun.app.util.config.IoptsCurl;
import com.skyun.recon.util.database.ibatis.SqlMapInstance;
import com.skyun.recon.util.database.ibatis.tr.DBInsertTable;
import com.skyun.recon.util.database.ibatis.vo.globalFiltersVo;

public class globalFilters {
	private static Logger logger = LoggerFactory.getLogger(globalFilters.class);
	private DBInsertTable tr = null;
	private static SqlMapClient sqlMap = null;
	private static String customer_id = "";
	private int ap_number;
	
	public globalFilters() {
		this.sqlMap = SqlMapInstance.getSqlMapInstance();
		this.customer_id = AppConfig.getProperty("config.customer");
		this.ap_number = AppConfig.getPropertyInt("config.recon.ap.number");
		
		try {
			
			String str_ap_count = AppConfig.getProperty("config.recon.ap.count");
			int ap_count = ("".equals(str_ap_count)) ? 1 : Integer.parseInt(str_ap_count);
			
			for(int i=0; i<ap_count; i++) {
				executeRun(i);
			}
			/*executeRun(ap_number);*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void executeRun(int ap_no) {
		
		List<globalFiltersVo> reconFiltersList = new ArrayList<>();
		String user = (ap_no == 0) ? AppConfig.getProperty("config.recon.user") : AppConfig.getProperty("config.recon.user_"+(ap_no+1));
		String pass = (ap_no == 0) ? AppConfig.getProperty("config.recon.pawwsord") : AppConfig.getProperty("config.recon.pawwsord_"+(ap_no+1));
		String ip = (ap_no == 0) ? AppConfig.getProperty("config.recon.ip") : AppConfig.getProperty("config.recon.ip_"+(ap_no+1)) ;
		String port = AppConfig.getProperty("config.recon.port");
		String api_ver = AppConfig.getProperty("config.recon.api.version");

		this.tr = new DBInsertTable();
		
		try {
			
			String curlurl = String.format("-k -X GET -u %s:%s https://%s:%s/%s/filters", user, pass, ip, port, api_ver);
			logger.info("globalFilter Check curlurl [" + curlurl + "]");
			
			String[] array = curlurl.split(" ");
			String json_string = new IoptsCurl().opt(array).exec(null);
			
			json_string = new IoptsCurl().opt(array).exec(null);

			if (json_string == null || json_string.length() < 1 || json_string.contains("Resource not found.")) {
				logger.error("Data Null Check IP or ID: " + curlurl);
			}else {
				JSONArray temp1 = new JSONArray(json_string);
				
				for (int i = 0; i < temp1.length(); i++) {
					Gson gson = new Gson();
					globalFiltersCo g = gson.fromJson(temp1.get(i).toString(), globalFiltersCo.class);
					globaFiltersAll(g, ap_no);
				}
			}
			
		} catch (ParseException e1) {
			logger.info("ParseException");
			e1.printStackTrace();
		} catch (SQLException e) {
			logger.info("SQLException");
			e.printStackTrace();
		} catch (Exception e) {
			logger.info("Exception");
			e.printStackTrace();
		}
	}

	private void globaFiltersAll(globalFiltersCo g, int ap_no) throws Exception {
		List<globalFiltersVo> reconFiltersList = new ArrayList<>();
		
		
		try {
			globalFiltersVo v = new globalFiltersVo();
			v.setValue(g);
			v.setAp_no(ap_no);
			logger.info("reconFiltersList >>> "  + v.toString()) ;
			
			tr.setDBInsertTable("insert.setGlobalFilters", v);
			
			if (g == null || g.getApply_to() == null) {
				logger.info("GlobalFilters Data is null ____");
			}
		} catch (Exception e) {
			logger.info("Exception");
			e.printStackTrace();
		}
		
	}

}
