package com.skyun.recon.util.database.ibatis.vo;

import java.sql.Date;

public class targetVo {
	private String group_id;
	private String target_id;
	private String host_name;
	
	public  targetVo() {
		
	}

	public targetVo(String target_id, String host_name) {
		super();
		this.target_id = target_id;
		this.host_name = host_name;
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

	@Override
	public String toString() {
		return "targetVo [group_id=" + group_id + ", target_id=" + target_id + ", host_name=" + host_name + "]";
	}
}
