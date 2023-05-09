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

public class licenseData {
	private static Logger logger = LoggerFactory.getLogger(licenseData.class);
	private DBInsertTable tr = null;
	private static SqlMapClient sqlMap = null;
	private static String customer_id = "";
	private long chkTimeStamp = Long.parseLong(AppConfig.getProperty("config.chkTimeStamp"));
	private long chkDeleteTimeStamp = Long.parseLong(AppConfig.getProperty("config.chkDeleteTimeStamp"));
	
//	private long chkTimeStamp = 1667228400;
//	private long chkDeleteTimeStamp = 1672498800;
	Timestamp ts;
	
	public licenseData() {
		this.sqlMap = SqlMapInstance.getSqlMapInstance();
		this.customer_id = AppConfig.getProperty("config.customer");
		
		List<targetVo> targetDeleteList;
		List<targetVo> targetList;
		
		try {
			//executeRun();
			targetDeleteList = licenseDeleteRun();
			targetList = licenseRun();
			
			// max byte 10월 말까지 저장
			for(int i = 0; i < targetDeleteList.size(); i++) {
				monthlyDeleteLicenseCompare(targetDeleteList.get(i));
			}
			
			// max byte 10월 말까지 저장
			for(int i = 0; i < targetList.size(); i++) {
				monthlyLicenseCompare(targetList.get(i));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		logger.info("========================== end ==========================");
	}
	
	private List<targetVo> licenseDeleteRun() throws ParseException {
		logger.info("============================== Delete Target License Data =====================================");
		
		String user = AppConfig.getProperty("config.recon.user");
		String pass = AppConfig.getProperty("config.recon.pawwsord");
		String ip = AppConfig.getProperty("config.recon.ip");
		String port = AppConfig.getProperty("config.recon.port");
		String api_ver = AppConfig.getProperty("config.recon.api.version");
		List<targetVo> targetList = new ArrayList<targetVo>();
		
		
		try {
			targetList = (List<targetVo>) sqlMap.openSession().queryForList("query.getDeleteTargets");

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
							
							// 라이선스 사이즈 비교
							licenseSizeCompare(dataVo);
							
						}
						
					}
					
				}
				
			}
			
			
			
		} catch (Exception e) {
			logger.error(e.toString());
		}
		

		return targetList;
	}

	private List<targetVo> licenseRun() throws ParseException {
		logger.info("============================== License Data =====================================");
		
		String user = AppConfig.getProperty("config.recon.user");
		String pass = AppConfig.getProperty("config.recon.pawwsord");
		String ip = AppConfig.getProperty("config.recon.ip");
		String port = AppConfig.getProperty("config.recon.port");
		String api_ver = AppConfig.getProperty("config.recon.api.version");
		
		List<targetVo> targetList = new ArrayList<>();
		
		
		try {
			// 전체 기간 모든 서버 목록 불러오기 & 삭제된 서버 목록
			targetList = (List<targetVo>) sqlMap.openSession().queryForList("query.getTargets");
			
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
								
								// 라이선스 사이즈 비교
								licenseSizeCompare(dataVo);
								
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
			
			
			
		} catch (Exception e) {
			logger.error(e.toString());
		}
		
		return targetList;
		
		
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
			
			logger.info("Size :: " + vo.getBytes() + ", DB Size :: " + max_scanSize);

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
	
	private void monthlyDeleteLicenseCompare(targetVo vo) {
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
			
			while(timeStamp < chkDeleteTimeStamp) {
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
				int lastDate = cal.get(Calendar.DATE);
				
				// 마지막 달에 데이터가 있을 경우 넘어감
				if(count > 0) {
					logger.info("Data Presence !! Count :::" + count);
					cal.add(Calendar.DATE, 1);
					
					timeStamp = timeStampFormat(cal.getTime());
					logger.info(vo.getHost_name() + " ::: TimeStamp : " + timeStamp + " , Date : " + fm.format(timeStamp*1000));
					
					continue;
				}
				
				
				// 검색 해당일자의 달의 마지막에 대한 data 사용량
				targetScanDataVo serchVo = (targetScanDataVo) sqlMap.openSession().queryForObject("query.getScanData", map);
				serchVo.setTimestamp(timeStampFormat(cal.getTime()));
				licenseUpload(serchVo);
				
				// 다음달 기간
				cal.add(Calendar.DATE, 1);
				
				timeStamp = timeStampFormat(cal.getTime());
				
				//Date confirmDate = (Date) sqlMap.openSession().queryForObject("query.getDate", timeStamp);
			
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("monthlyLicense :: " + e.toString());
		}
		
	}
	
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
					cal.add(Calendar.DATE, 1);
					
					timeStamp = timeStampFormat(cal.getTime());
					continue;
				}
				
				
				// 검색 해당일자의 달의 마지막에 대한 data 사용량
				targetScanDataVo serchVo = (targetScanDataVo) sqlMap.openSession().queryForObject("query.getScanData", map);
				serchVo.setTimestamp(timeStampFormat(cal.getTime()));
				licenseUpload(serchVo);
				
				// 다음달 기간
				cal.add(Calendar.DATE, 1);
				
				timeStamp = timeStampFormat(cal.getTime());
				//Date confirmDate = (Date) sqlMap.openSession().queryForObject("query.getDate", timeStamp);
			
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("monthlyLicense :: " + e.toString());
		}
		
	}
	
	private Date dateFormat(long timestamp) {
		ts = new Timestamp(timestamp * 1000);  
		
		return ts;
	}
	
	private long timeStampFormat(Date date) {
		long timestamp = date.getTime() / 1000;
		
		return timestamp;
	}

}
