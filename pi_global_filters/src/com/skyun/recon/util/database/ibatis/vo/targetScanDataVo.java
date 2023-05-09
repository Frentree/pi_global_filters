package com.skyun.recon.util.database.ibatis.vo;

import java.sql.Date;

public class targetScanDataVo {
	private String target_id;
	private String host_name;
	private String path;
	private long timestamp;
	private long bytes;
	
	public  targetScanDataVo() {
		
	}

	@Override
	public String toString() {
		return "targetScanDataVo [target_id=" + target_id + ", host_name=" + host_name + ", path=" + path
				+ ", timestamp=" + timestamp + ", bytes=" + bytes + "]";
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
	
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getBytes() {
		return bytes;
	}

	public void setBytes(long bytes) {
		this.bytes = bytes;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
}
