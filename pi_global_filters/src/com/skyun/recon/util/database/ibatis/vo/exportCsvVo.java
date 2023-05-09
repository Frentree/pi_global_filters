package com.skyun.recon.util.database.ibatis.vo;

import java.sql.Date;

public class exportCsvVo {
	
	private String target_id;
	private String host_name;
	private int start_date;
	private int end_date;
	
	public  exportCsvVo() {
		
	}

	public exportCsvVo(String target_id, String host_name, int start_date, int end_date) {
		super();
		this.target_id = target_id;
		this.host_name = host_name;
		this.start_date = start_date;
		this.end_date = end_date;
	}

	public String getTarget_id() {
		return target_id;
	}

	public void setTarget_id(String target_id) {
		this.target_id = target_id;
	}

	public String getHost_name() {
		return host_name;
	}

	public void setHost_name(String host_name) {
		this.host_name = host_name;
	}

	public int getStart_date() {
		return start_date;
	}

	public void setStart_date(int start_date) {
		this.start_date = start_date;
	}

	public int getEnd_date() {
		return end_date;
	}

	public void setEnd_date(int end_date) {
		this.end_date = end_date;
	}

	
	

}
