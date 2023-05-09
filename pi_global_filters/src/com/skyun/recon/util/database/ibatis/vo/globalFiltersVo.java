package com.skyun.recon.util.database.ibatis.vo;

import java.sql.Date;
import com.iopts.skyun.recon.vo.groupall.globalFiltersCo;

public class globalFiltersVo {
	
	private String fiters_id;
	private int ap_no;
	private String type;
	private String path;
	private String target_id;

	
	public globalFiltersVo() {
		// TODO Auto-generated constructor stub
	}

	public void setValue(globalFiltersCo c) {
		this.fiters_id = c.getId();
		this.type = c.getType();
		this.path = c.getExpression();
		this.target_id = c.getApply_to();
	}


	public String getFiters_id() {
		return fiters_id;
	}
	public void setFiters_id(String fiters_id) {
		this.fiters_id = fiters_id;
	}
	public int getAp_no() {
		return ap_no;
	}
	public void setAp_no(int ap_no) {
		this.ap_no = ap_no;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getTarget_id() {
		return target_id;
	}
	public void setTarget_id(String target_id) {
		this.target_id = target_id;
	}


	@Override
	public String toString() {
		return "globalFiltersVo [fiters_id=" + fiters_id + ", ap_no=" + ap_no + ", type=" + type + ", path=" + path
				+ ", target_id=" + target_id + "]";
	}

	
	
}
