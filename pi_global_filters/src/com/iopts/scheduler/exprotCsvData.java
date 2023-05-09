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

import com.ibatis.sqlmap.client.SqlMapClient;
import com.opencsv.CSVWriter;
import com.skyun.app.util.config.AppConfig;
import com.skyun.app.util.config.IoptsCurl;
import com.skyun.recon.util.database.ibatis.SqlMapInstance;
import com.skyun.recon.util.database.ibatis.tr.DBInsertTable;
import com.skyun.recon.util.database.ibatis.vo.exportCsvVo;
import com.skyun.recon.util.database.ibatis.vo.targetScanDataVo;
import com.skyun.recon.util.database.ibatis.vo.targetVo;

public class exprotCsvData {
	private static Logger logger = LoggerFactory.getLogger(exprotCsvData.class);
	private DBInsertTable tr = null;
	private static SqlMapClient sqlMap = null;
	private static String customer_id = "";
	private long chkTimeStamp = 1667228400;
	Timestamp ts;
	
	public exprotCsvData() {
		this.sqlMap = SqlMapInstance.getSqlMapInstance();
		this.customer_id = AppConfig.getProperty("config.customer");
		
		try {
			//executeRun();
			licenseRun();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void executeRun() throws ParseException {
		Map<String, Object> resultMap =  new HashMap<String, Object>();
		
		List<Map<String, Object>> resultList = new ArrayList<>();
		List<Map<String, Object>> dataTypeResultList = new ArrayList<>();
		List<Map<String, Object>> dataTypeList = new ArrayList<>();
		Map<String, Object> dataTypeResultMap =  new HashMap<String, Object>();

		List<String> targetList = new ArrayList<String>();
		List<Map<String, Object>> targetResultList = new ArrayList<>();
		Map<String, Object> taregtMap =  new HashMap<String, Object>();
		
		
		this.tr = new DBInsertTable();
		
		String user = AppConfig.getProperty("config.recon.user");
		String pass = AppConfig.getProperty("config.recon.pawwsord");
		String ip = AppConfig.getProperty("config.recon.ip");
		String port = AppConfig.getProperty("config.recon.port");
		String api_ver = AppConfig.getProperty("config.recon.api.version");
		
		String str_date = AppConfig.getProperty("config.start_date");
		String end_date = AppConfig.getProperty("config.end_date");
		
		logger.info("searchDate >>>> " + str_date + "~" + end_date) ;
		
		Map<String, Object> getMap = new HashMap<>();
		getMap.put("str_date", str_date);
		getMap.put("end_date", end_date);
		
		if(str_date.length() != 14 && end_date.length() != 14) {
			logger.info("시작 일 혹은 종료 일을 다시 확인해주세요.");
			return;
		}
		
		List<exportCsvVo> dbSearchDate = new ArrayList<>();
		try {
			if(end_date.equals("0")) {
				dbSearchDate = sqlMap.openSession().queryForList("query.getSearchDate", getMap);
			}else{
				dbSearchDate = sqlMap.openSession().queryForList("query.getSearchDate2", getMap);
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		String target_id = "";
		String host_name = "";
		String search_status = "";
		String path = "";
		// Recon Target 조회
		
		String schedule_str_dt = str_date.substring(2, 8);
		String schedule_ed_dt = end_date.substring(2, 8);
		
		// 라이선스는 Complete 인 대상한에서 추가됨
		String schedule_curlurl = String.format("-k -X GET -u %s:%s https://%s:%s/%s/schedules?completed=true&cancelled=true&stopped=true&failed=true&deactivated=true&interrupted=true&start_date=%s&limit=5000000&end_date=%s", user, pass, ip, port, api_ver, schedule_str_dt, schedule_ed_dt);
		// String schedule_curlurl = String.format("-k -X GET -u %s:%s https://%s:%s/%s/schedules?completed=true&start_date=%s&limit=5000000&end_date=%s", user, pass, ip, port, api_ver, schedule_str_dt, schedule_ed_dt);
		logger.info("Schedule Check curlurl [" + schedule_curlurl + "]");
		String[] schedule_array = schedule_curlurl.split(" ");
		
		String schedule_json_string = new IoptsCurl().opt(schedule_array).exec(null);
		if (schedule_json_string == null || schedule_json_string.length() < 1 || schedule_json_string.contains("Resource not found.")) {
			logger.error("Schedule Data Null Check IP or ID: " + schedule_curlurl);
		} else {
			try {
				JSONArray scheduleJsonArray = new JSONArray(schedule_json_string);
				
				logger.info("scheduleJsonArray >>>> " + scheduleJsonArray);
				
				for(int i=0 ; i< scheduleJsonArray.length() ; i++) {
					taregtMap =  new HashMap<String, Object>();
					
					JSONObject scheduleJsonObject = scheduleJsonArray.getJSONObject(i);
					JSONArray targetArray = scheduleJsonObject.getJSONArray("targets");

					target_id = targetArray.getJSONObject(0).getString("id");
					host_name = targetArray.getJSONObject(0).getString("name");
				/*	search_status = scheduleJsonObject.getString("status");*/
					
					boolean target_chk = true;
					
					for(int j=0 ; j < targetList.size() ; j++) {
						String targetChk = targetList.get(j);
						if(target_id.equals(targetChk)) {
							target_chk = false;
							break;
						}
					}
					if(target_chk) {
						targetList.add(target_id);
					}
				}
				
				for(int i=0 ; i < targetList.size() ; i++) {
					
					target_id = targetList.get(i);
					String isolated_curlurl = String.format("-k -X GET -u %s:%s https://%s:%s/%s/targets/%s/isolated", user, pass, ip, port, api_ver, target_id);
					logger.info("isolated Check curlurl [" + isolated_curlurl + "]");
					String[] isolated_array = isolated_curlurl.split(" ");
					
					String isolated_json_string = new IoptsCurl().opt(isolated_array).exec(null);
					if (isolated_json_string == null || isolated_json_string.length() < 1 || isolated_json_string.contains("Resource not found.")) {
						logger.error("Consolidated Data Null Check IP or ID: " + isolated_curlurl);
						logger.info("==============="+target_id+"_is not scann("+i+1+"/"+targetList.size()+")==============");
					} else {
						logger.info("isolatedJsonArray >> " + isolated_json_string);

						logger.info("==============="+target_id+"_start("+i+1+"/"+targetList.size()+")==============");
						JSONArray isolatedJsonArray = new JSONArray(isolated_json_string);
						
						// 검색한 TimeStamp 기준 날짜 구하기
						for(int j=0 ; j< isolatedJsonArray.length() ; j++) {
							
							String timeDate = isolatedJsonArray.get(j).toString();
							int timeStamp = 0;
							
							// 경우에 따라 timeStamp- '' 출력되는 경우가 있음.
							if(timeDate.contains("-")) {
								String[] timeArray = timeDate.split("-");
								timeStamp = Integer.parseInt(timeArray[0]);
							}else {
								timeStamp = Integer.parseInt(timeDate);
							}
							
							int st_date = 0;
							if(!str_date.equals("0")) st_date = dbSearchDate.get(0).getEnd_date();
							int ed_date = dbSearchDate.get(0).getStart_date();
							
							if(ed_date <= timeStamp && timeStamp <= st_date ) {
								// Map 초기화 
								resultMap =  new HashMap<String, Object>();
								resultMap.put("target_id", target_id); 
								
								// TimeStamp를 활용하여 데이터 출력
								String rawisolated_curlurl = String.format("-k -X GET -u %s:%s https://%s:%s/%s/targets/%s/rawisolated/%s", user, pass, ip, port, api_ver, target_id, timeStamp);
								logger.info("rawisolated_date Check curlurl [" + rawisolated_curlurl + "]");
								String[] rawisolated_date_array = rawisolated_curlurl.split(" ");
								
								StringBuffer sb = new StringBuffer();
								String rawisolated_json_string = new IoptsCurl().opt(rawisolated_date_array).header("Accept: application/json").exec(null);
								if (rawisolated_json_string == null || rawisolated_json_string.length() < 1 || rawisolated_json_string.contains("Resource not found.")) {
									logger.error("Rawisolated Null Check IP or ID: " + rawisolated_curlurl);
								} else {
									JSONObject RawisolatedJsonArray = new JSONObject(rawisolated_json_string);
									// 이름, 사용량, 시작 시간, 종료 시간, Recon 그불, 경로, 상태
									
									//com.sun.jmx.snmp.Timestamp stDt =  new com.sun.jmx.snmp.Timestamp(Integer.parseInt(RawisolatedJsonArray.get("start").toString()));
									//com.sun.jmx.snmp.Timestamp edDt =  new com.sun.jmx.snmp.Timestamp(Integer.parseInt(RawisolatedJsonArray.get("end").toString()));
									
									/*String stDT = RawisolatedJsonArray.getString("start");
									String edDT = RawisolatedJsonArray.getString("end");*/
									
									SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
								
									//logger.info("stDT >>>> " + sdf.format(stDt));
									//logger.info("edDT >>>> " + sdf.format(edDt));
									
									host_name = RawisolatedJsonArray.getString("target");
									
											
									
									
									
									/*logger.info("RawisolatedJsonArray >>>>> " + RawisolatedJsonArray);*/
								}
							}
						}
					}
					
				}
				
				/*for(int i=0 ; i< scheduleJsonArray.length() ; i++) {
					
					target_id = targetJsonObject.get("id").toString();
					host_name = targetJsonObject.get("name").toString();
					search_status = targetJsonObject.get("search_status").toString();
					
					logger.info("targetJsonObject" + i + ">>>>>> " + targetJsonObject.get("id") + ">>>>> " + targetJsonObject.get("name"));
					
					logger.info("==============="+host_name+"_start("+i+1+"/"+scheduleJsonArray.length()+")==============");
					
					// 각 대상별 검색한 일의 TimeStamp 
					String consolidated_curlurl = String.format("-k -X GET -u %s:%s https://%s:%s/%s/targets/%s/consolidated", user, pass, ip, port, api_ver, target_id);
					logger.info("consolidated Check curlurl [" + consolidated_curlurl + "]");
					String[] consolidated_array = consolidated_curlurl.split(" ");
					
					String consolidated_json_string = new IoptsCurl().opt(consolidated_array).exec(null);
					if (consolidated_json_string == null || consolidated_json_string.length() < 1 || consolidated_json_string.contains("Resource not found.")) {
						logger.error("Consolidated Data Null Check IP or ID: " + consolidated_curlurl);
					} else {
						logger.info("consolidatedJsonArray >> " + consolidated_json_string);
						JSONArray consolidatedJsonArray = new JSONArray(consolidated_json_string);
						
						// 검색한 TimeStamp 기준 날짜 구하기
						for(int j=0 ; j< consolidatedJsonArray.length() ; j++) {
							
							String timeDate = consolidatedJsonArray.get(j).toString();
							
							int timeStamp = 0;
							if(timeDate.contains("-")) {
								String[] timeArray = timeDate.split("-");
								timeStamp = Integer.parseInt(timeArray[0]);
							}else {
								timeStamp = Integer.parseInt(timeDate);
							}
							
							int st_date = 0;
							if(!str_date.equals("0")) st_date = dbSearchDate.get(0).getEnd_date();
							int ed_date = dbSearchDate.get(0).getStart_date();
							
							if(ed_date <= timeStamp && timeStamp <= st_date ) {
								// Map 초기화 
								resultMap =  new HashMap<String, Object>();
								
								// 대상 데이터 추가
								resultMap.put("target_id", target_id); 
								resultMap.put("host_name", host_name); 
								
								// TimeStamp를 활용하여 데이터 출력
								String consolidated_date_curlurl = String.format("-k -X GET -u %s:%s https://%s:%s/%s/targets/%s/consolidated/%s", user, pass, ip, port, api_ver, target_id, timeDate);
								logger.info("consolidated_date Check curlurl [" + consolidated_date_curlurl + "]");
								String[] consolidated_date_array = consolidated_date_curlurl.split(" ");
								
								StringBuffer sb = new StringBuffer();
								String consolidated_date_json_string = new IoptsCurl().opt(consolidated_date_array).header("Accept: application/json").exec(null);
								if (consolidated_date_json_string == null || consolidated_date_json_string.length() < 1 || consolidated_date_json_string.contains("Resource not found.")) {
									logger.error("Consolidated Data2 Null Check IP or ID: " + consolidated_date_curlurl);
								} else {
									
									JSONObject consolidatedDateJsonArray = new JSONObject(consolidated_date_json_string);
									
									JSONObject summaryObject = consolidatedDateJsonArray.getJSONObject("summary");
									String stateunconfirmed_type_chk = summaryObject.get("stateunconfirmed").getClass().getName();
									
									JSONObject stateunconfirmed = null;
									if(stateunconfirmed_type_chk.contains("JSONObject")) { // 검출 데이터가 있을 경우에만 JSONObject 생성 없을 NULL 유지
										stateunconfirmed = summaryObject.getJSONObject("stateunconfirmed");
									}
								
									if(targetJsonObject != null) {
										JSONObject matches = targetJsonObject.getJSONObject("matches");
										int match = 0;
										int prohibited = 0;
										int match = matches.getInt("match");
										int prohibited = matches.getInt("prohibited");
										resultMap.put("detection", (match+prohibited)+""); // 검출 결과
									}else {
										resultMap.put("detection", summaryObject.getString("matches")); // 검출 결과
									}
									
									String locations = summaryObject.getString("locations"); // 검출위치개수
									String false_locations = "0";
									
									if(stateunconfirmed != null ) {
										false_locations =  stateunconfirmed.getString("locations"); // 검출위치개수 (오탐제외)
									}
									
									resultMap.put("locations", locations); 
									resultMap.put("false_locations", false_locations); 
									
									dataTypeResultList = new ArrayList<>();
									dataTypeList = new ArrayList<>();
									// dataTyped, search 타입이 데이터의 양의 따라 바뀌어 조건 추가
									// 검출 타입
									
									boolean dataType_status = summaryObject.has("datatype");
									if(dataType_status) { // 검출 데이터가 없을 경우 해당 데이터가 생성되지 않아 비교 구문 추가
										if(summaryObject.get("datatype").getClass().getName().contains("JSONArray")) { 
											JSONArray dataTypeArray = summaryObject.getJSONArray("datatype");
											for(int w=0 ; w<dataTypeArray.length() ; w++) {
												dataTypeResultMap = new HashMap<String, Object>();
												JSONObject D_resultObject = dataTypeArray.getJSONObject(w);
												if(D_resultObject.length() > 1) {
													dataTypeResultMap.put("matches", D_resultObject.getString("matches"));
													dataTypeResultMap.put("label", D_resultObject.getString("label"));
													dataTypeResultList.add(dataTypeResultMap);
												}
											} 
											
										}else if(summaryObject.get("datatype").getClass().getName().contains("JSONObject")) {
											JSONObject dataTypeArray = summaryObject.getJSONObject("datatype");
											if(dataTypeArray.length() > 1) {
												dataTypeResultMap.put("matches", dataTypeArray.getString("matches"));
												dataTypeResultMap.put("label", dataTypeArray.getString("label"));
												dataTypeResultList.add(dataTypeResultMap);
											}
										}
									}else { // 데이터가 없는 경우
										dataTypeResultMap.put("matches","0");
										dataTypeResultMap.put("label", "0");
										dataTypeResultList.add(dataTypeResultMap);
									}
									resultMap.put("dataTypeResultList", dataTypeResultList);
									
									
									String uri  = "";
									String endDate = "";
									String inaccess = "";
									String sTDate = "";
									String endLocationCNT = "";
									String scannedbytes = "";
									String scannedbyte = "";
									String scanStatus = "Not Searched";
									
									// 검출 대상
									if(summaryObject.get("search").getClass().getName().contains("JSONArray")) { 
										JSONArray searchArray = summaryObject.getJSONArray("search");
										uri = searchArray.getJSONObject(0).getString("uri");
										
										boolean stopped = searchArray.getJSONObject(0).toString().contains("stopped");
										boolean failed = searchArray.getJSONObject(0).toString().contains("failed");
										
										if(failed) {
											scanStatus = "Failed";
										}else if(!failed && stopped) {
											scanStatus = "Stopped";
										}else if(!failed && !stopped) {
											scanStatus = "Completed";
										}else {
											scanStatus = "unknown";
										}
										
										for(int a=0; a < searchArray.length() ; a++) {
											
											uri  = "";
											endDate = "";
											inaccess = "";
											sTDate = "";
											endLocationCNT = "";
											scannedbytes = "";
											scannedbyte = "";
											
											inaccess = searchArray.getJSONObject(0).getString("inaccess");
											
											if(scannedbytes.equals("") && searchArray.getJSONObject(0).has("starttime")) {
												sTDate = searchArray.getJSONObject(0).getString("starttime");
											}
											if(scannedbytes.equals("") && searchArray.getJSONObject(0).has("endtime")) {
												endDate = searchArray.getJSONObject(0).getString("endtime");
											}
											if(scannedbytes.equals("") && searchArray.getJSONObject(0).has("scannedlocations")) {
												endLocationCNT = searchArray.getJSONObject(0).getString("scannedlocations");
											}
											if(scannedbytes.equals("") && searchArray.getJSONObject(0).has("scannedbytes")) {
												scannedbytes = searchArray.getJSONObject(0).getString("scannedbytes");
											}
										}
										
									}else if(summaryObject.get("search").getClass().getName().contains("JSONObject")) {
										JSONObject searchArray = summaryObject.getJSONObject("search");
										uri = "";
										uri = searchArray.getString("uri");
										
										boolean stopped = searchArray.has("stopped");
										boolean failed = searchArray.has("failed");
										
										if(failed) {
											scanStatus = "Failed";
										}else if(!failed && stopped) {
											scanStatus = "Stopped";
										}else if(!failed && !stopped) {
											scanStatus = "Completed";
										}else {
											scanStatus = "unknown";
										}
										
										inaccess = searchArray.getString("inaccess");
										
										if(searchArray.has("endtime")) {
											endDate = searchArray.getString("endtime");
										}
										if(searchArray.has("starttime")) {
											sTDate = searchArray.getString("starttime");
											
										}
										if(searchArray.has("scannedbytes")) {
											scannedbytes = searchArray.getString("scannedbytes");
										}
										if(searchArray.has("scannedlocations")) {
											endLocationCNT = searchArray.getString("scannedlocations");
										}
									}
									
									scannedbyte = scannedbytes;
									
									if(endDate == null || endDate.equals("") || endDate.length() == 0) {
										endDate = "0000-00-00 00:00:00z";
									}
									
									
									if(scannedbytes != null && !scannedbytes.equals("") && scannedbytes.length() > 0) {
										scannedbytes = numberFormat(scannedbytes);
									}else {
										scannedbytes = "0 bytes";
									}
									
									resultMap.put("uri", uri); 
									resultMap.put("endDate", endDate.substring(0, endDate.length()-1)); 
									resultMap.put("sTDate", sTDate.substring(0, sTDate.length()-1)); 
									resultMap.put("endLocationCNT", endLocationCNT); 
									resultMap.put("inaccess", inaccess); 
									resultMap.put("scannedbytes", scannedbytes); 
									resultMap.put("scannedbyte", scannedbyte); 
									resultMap.put("scanStatus", scanStatus); 
									resultList.add(resultMap);
								}
							}
						}
					}
				}*/
				
			} catch (Exception e) {
				logger.error("Export csv api Export Error :::  " + e);
				e.printStackTrace();
			}
			
		}
		exportExcel(resultList);
	}
	
	private void exportExcel(List<Map<String, Object>> resultList) throws ParseException {
		
		/*logger.info("resultList >>> " + resultList);*/
		
		if(resultList == null) {
			logger.error("Export Data IS Null");
		}
		
		List<String[]> data = new ArrayList<>();
		data.add(new String[] {"host", "시작시간", "종료시간", "검색량", "byte", "검색상태"});
		
		String host = "";
		String st_dt = "";
		String ed_dt = "";
		String size = "";
		String byte_size = "";
		String status = "";				
		
		for(int i=0 ; i<resultList.size() ; i++) {
			host = (String)resultList.get(i).get("host_name");
			st_dt = (String)resultList.get(i).get("sTDate");
			ed_dt = (String)resultList.get(i).get("endDate");
			size = (String)resultList.get(i).get("scannedbytes");
			byte_size = (String)resultList.get(i).get("scannedbyte");
			status = (String)resultList.get(i).get("scanStatus");
			
			SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			
			if(ed_dt.contains("0000-")) {
				ed_dt = "-";
			}else {
				Date format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ed_dt);
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(format1);
				cal1.add(Calendar.HOUR, 9);
				ed_dt =  sdformat.format(cal1.getTime()).toString();
			}
			
			
			Date format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(st_dt);
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(format2);
			cal2.add(Calendar.HOUR, 9);
			st_dt =  sdformat.format(cal2.getTime()).toString();
			
			
			data.add(new String[] {host, st_dt, ed_dt, size, byte_size, status});
			
		}
        
        String saveDir = AppConfig.getProperty("config.file.path");
        
        Calendar cal = new GregorianCalendar(Locale.KOREA);
	    SimpleDateFormat fm = new SimpleDateFormat("yyyyMMddHHmmss");
	    String toDate = fm.format(cal.getTime());
	    
        String fileName = "data_"+toDate+".csv";

        try {
            // CSV 파일로 저장
        	CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(saveDir+fileName), "MS949"));
        	/*CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(saveDir+fileName), StandardCharsets.UTF_8));*/
            /*CSVWriter writer = new CSVWriter(new FileWriter(saveDir + File.separator + fileName));*/
        	/*BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveDir+fileName), "UTF-8"));*/
            writer.writeAll(data);
            writer.close();

            System.out.println(saveDir + fileName + ">>>> File downloaded success");
        } catch (IOException e) {
            System.out.println("Error downloading file: " + e.getMessage());
            e.printStackTrace();
        }
	}
	
	
	
	private String numberFormat(String dataSize) {
		String fomatSize = "";
		
		Double size = Double.parseDouble(dataSize);
		
		if(size > 0) {
			
			String[] s = {"bytes", "KB", "MB", "GB", "TB", "PB" };
			
			int idx = (int) Math.floor(Math.log(size) / Math.log(1024));
            DecimalFormat df = new DecimalFormat("#,###.##");
            double ret = ((size / Math.pow(1024, Math.floor(idx))));
            fomatSize = df.format(ret) + " " + s[idx];
		}
		
		return fomatSize;
	}
	
	private void licenseRun() throws ParseException {
		this.tr = new DBInsertTable();
		
		String user = AppConfig.getProperty("config.recon.user");
		String pass = AppConfig.getProperty("config.recon.pawwsord");
		String ip = AppConfig.getProperty("config.recon.ip");
		String port = AppConfig.getProperty("config.recon.port");
		String api_ver = AppConfig.getProperty("config.recon.api.version");
		
		
		try {
			// 전체 기간 모든 서버 목록 불러오기 & 삭제된 서버 목록
			List<targetVo> targetList = (List<targetVo>) sqlMap.openSession().queryForList("query.getTargets");
			
			// 검색 진행항 서버목록 검색 기록 갖고 오기
			for(int i = 0; i < targetList.size(); i++) {
				targetVo vo = targetList.get(i);
				String target_id = vo.getTarget_id();
				String host_name = vo.getHost_name();
				
				String isolated_curlurl = String.format("-k -X GET -u %s:%s https://%s:%s/%s/targets/%s/isolated", user, pass, ip, port, api_ver, target_id);
				logger.info("isolated Check curlurl [" + isolated_curlurl + "]");
				
				String[] isolated_array = isolated_curlurl.split(" ");
				String isolated_json_string = new IoptsCurl().opt(isolated_array).exec(null);
				
				if (isolated_json_string == null || isolated_json_string.length() < 1 || isolated_json_string.contains("Resource not found.")) {
					logger.info("==============="+host_name+"("+target_id+")_is not scann("+i+1+"/"+targetList.size()+")==============");
					logger.error("isolated Data Null Check IP or ID: " + isolated_curlurl);
					
					continue;
				} else {
					// 시간 분할
					JSONArray isolatedJsonArray = new JSONArray(isolated_json_string);
					
					// 최초 검색 일부터 확인
					for(int index = (isolatedJsonArray.length()-1); index >= 0; index--) {
						String timeDate = isolatedJsonArray.get(index).toString();
						long timeStamp = 0;

						// 경우에 따라 timeStamp- '' 출력되는 경우가 있음.
						if(timeDate.contains("-")) {
							String[] timeArray = timeDate.split("-");
							timeStamp = Integer.parseInt(timeArray[0]);
						}else {
							timeStamp = Integer.parseInt(timeDate);
						}
						
						// 23년 11월 1일 이전인지 확인
						boolean timeChk = dateFormatCompare(timeStamp);
						
						if(timeChk) {
							String rawisolated_curlurl = String.format("-k -X GET -u %s:%s https://%s:%s/%s/targets/%s/rawisolated/%s", user, pass, ip, port, api_ver, target_id, timeStamp);
							
							logger.info("rawisolated_date Check curlurl [" + rawisolated_curlurl + "]");
							String[] rawisolated_date_array = rawisolated_curlurl.split(" ");
							
							StringBuffer sb = new StringBuffer();
							String rawisolated_json_string = new IoptsCurl().opt(rawisolated_date_array).header("Accept: application/json").exec(null);
							
							if (rawisolated_json_string == null || rawisolated_json_string.length() < 1 || rawisolated_json_string.contains("Resource not found.")) {
								logger.error("Rawisolated Null Check IP or ID: " + rawisolated_curlurl);
								
								continue;
							} else {
								//logger.info(rawisolated_json_string);
								JSONObject RawisolatedJsonArray = new JSONObject(rawisolated_json_string);
								
								//logger.info("Time ::: 1. " + timeStamp + " , 2. " + RawisolatedJsonArray.getInt("timestamp"));
								targetScanDataVo dataVo = new targetScanDataVo();
								targetList.get(i).setHost_name(RawisolatedJsonArray.getString("target"));
								
								dataVo.setTarget_id(target_id);
								dataVo.setTimestamp(timeStamp);
								dataVo.setHost_name(RawisolatedJsonArray.getString("target"));
								dataVo.setBytes(RawisolatedJsonArray.getLong("scanned_bytes"));
								dataVo.setPath(RawisolatedJsonArray.getString("path"));
								break;
								// 라이선스 사이즈 비교
								//licenseSizeCompare(dataVo);
								
							}
							
						} else {	// 11월 1일 이후

							Timestamp date =  new Timestamp(timeStamp*1000);
							SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							logger.info("Timestamp Over :: " +timeStamp+ " , Date ::" + fm.format(date) );
							// time결과가 false 일 경우, 이후 결과도 11월 1일 이후 결과이므로 반복문 종료
							break;
						}
						
					}
					
				}
				
			}
			
			targetList = (List<targetVo>) sqlMap.openSession().queryForList("query.getLicenseTargets", chkTimeStamp);
			
			
			// max byte 10월 말까지 저장
			for(int i = 0; i < targetList.size(); i++) {
				logger.info(targetList.get(i).toString());
				monthlyLicenseCompare(targetList.get(i));
			}
			
		} catch (Exception e) {
			logger.error(e.toString());
		}
		
		
		logger.info("========================== end ==========================");
		
	}
	
	// 기간 확인 비교 (~ 11/1)
	private boolean dateFormatCompare(long timeStamp) {
		boolean result = false;
		// 2022년 11월 1일 00시 00분 00초 계산한 결과 UnixTime(1667228400)
		// int chkTimeStamp = 1667228400;
		try {
			if(timeStamp < chkTimeStamp) {
				result = true;
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		
		return result;
	}
	
	// 라이선스 사이즈 비교
	private void licenseSizeCompare(targetScanDataVo vo) {
		long max_scanSize = 0;
		
		try {
			max_scanSize = 0;
			max_scanSize = (long) sqlMap.openSession().queryForObject("query.getLicenseCompare", vo);
			
			if (max_scanSize > vo.getBytes()) {
				vo.setBytes(max_scanSize);
			}

			licenseUpload(vo);
			
			
		} catch (Exception e) {
			logger.error("licenseSizeCompare :: " + e.toString());
		}
		
		
	}
	
	// 라이선스 Data DB 저장
	private void licenseUpload(targetScanDataVo vo) {
		try {
			if(vo.getBytes() == 0) {
				logger.info("Data 0 byte Return Target("+vo.getHost_name()+")");
				return;
			}
			
			sqlMap.openSession().update("insert.setLicenseData", vo);

			logger.info("LicenseData Insert Success:: " + vo.toString());
			
		} catch (Exception e) {
			logger.error(e.toString());
		}
		
	}
	
	// 업데이트 안된 월 확인 후 업데이트
	/*private void licenseTableUpDate() {
		try {
			List<licenseVo> dateList = (List<licenseVo>) sqlMap.openSession().queryForList("query.setDate");
			Date stDt = dateList.get(0).getCredate();
			Date edDt = dateList.get(dateList.size()-1).getCredate();
			
			int CDt_cnt = 0;
			Date CDt = stDt;
			boolean chkDt = true;
			
			while(chkDt) {
				
				Date chDt = dateList.get(CDt_cnt).getCredate();
				
				for (licenseVo lo : dateList) {
					
					logger.info("lo.getCredate() >>>> " + lo.getCredate());
					logger.info("chDt >>>> " + chDt);
					
					if(chDt.compareTo(lo.getCredate()) <= 0) {
						
					}
				}
				
				
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(CDt);
				calendar.add(Calendar.MONTH, 1);
				
				CDt = calendar.getTime();
				
				
				if(chDt.compareTo(edDt) == 0) {
					chkDt = false;
				}
			}
			
			
		} catch (Exception e) {
			logger.error(e.toString());
		}
		
	}*/
	
	private void monthlyLicenseCompare(targetVo vo) {
		SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = new GregorianCalendar(Locale.KOREA);
		Date selDate;

		try {
			// 최초 일자 뽑기
			targetScanDataVo scanVo = (targetScanDataVo) sqlMap.openSession().queryForObject("query.getFirst", vo);
			long timeStamp = scanVo.getTimestamp();
			Map<String, Object> map;
			int count = 0;
			
			if(scanVo.getTimestamp() == 0) {
				logger.info("Time Error 0 Time");
				return;
			}
			
			while(timeStamp < chkTimeStamp) {
				count = 0;
				map = new HashMap<>();
				//String dateTime = (String) sqlMap.openSession().queryForObject("query.getDate", map);
				//logger.info("index :: " + (i++) + " timeStamp :: " + dateTime);

				selDate = dateFormat(timeStamp);
				
				cal.setTime(selDate);
				
				cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				cal.set(cal.getWeekYear(), cal.getTime().getMonth(), lastDay);
				
				map.put("target_id", scanVo.getTarget_id());
				map.put("host_name", scanVo.getHost_name());
				map.put("time", fm.format(cal.getTime()));
				
				logger.info("host_name :: " +scanVo.getHost_name()+  ", test ::: " + map.get("time"));
				
				count = (int) sqlMap.openSession().queryForObject("query.getDateCheck", map);
				
				// 마지막 달에 데이터가 있을 경우 넘어감
				if(count > 0) {
					logger.info("Data Presence !! Count :::" + count);
					cal.add(Calendar.DATE, lastDay+1);
					
					timeStamp = timeStampFormat(cal.getTime());
					continue;
				}
				
				
				// 검색 해당일자의 달의 마지막에 대한 data 사용량
				targetScanDataVo serchVo = (targetScanDataVo) sqlMap.openSession().queryForObject("query.getScanData", map);
				serchVo.setTimestamp(timeStampFormat(cal.getTime()));
				licenseUpload(serchVo);
				
				// 다음달 기간
				cal.add(Calendar.DATE, lastDay+1);
				
				timeStamp = timeStampFormat(cal.getTime());
				
				Thread.sleep(500);
				//Date confirmDate = (Date) sqlMap.openSession().queryForObject("query.getDate", timeStamp);
			
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("monthlyLicense :: " + e.toString());
		}
		
	}
	
	private Date dateFormat(long timestamp) {
		SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		ts = new Timestamp(timestamp * 1000);  
		logger.info("DateFormat Date :: " + fm.format(ts));
		
		return ts;
	}
	
	private long timeStampFormat(Date date) {
		SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long timestamp = date.getTime() / 1000;
		
		
		logger.info("DateFormat Time :: " + timestamp);
		
		return timestamp;
	}

}
