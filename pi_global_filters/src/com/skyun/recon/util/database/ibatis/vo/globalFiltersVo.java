package com.skyun.recon.util.database.ibatis.vo;

import java.sql.Date;

public class globalFiltersVo {
	
	private String target_id;
	private String host_name;
	private String data_usage;
	private int timestamp;
	
	public globalFiltersVo() {
		// TODO Auto-generated constructor stub
	}
	
	
	public globalFiltersVo(String target_id, String host_name, String data_usage, int timestamp) {
		super();
		this.target_id = target_id;
		this.host_name = host_name;
		this.data_usage = data_usage;
		this.timestamp = timestamp;
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


	public String getData_usage() {
		return data_usage;
	}


	public void setData_usage(String data_usage) {
		this.data_usage = data_usage;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}


	@Override
	public String toString() {
		return "licenseVo [target_id=" + target_id + ", host_name=" + host_name + ", data_usage=" + data_usage
				+ ", timestamp=" + timestamp + "]";
	}

}
