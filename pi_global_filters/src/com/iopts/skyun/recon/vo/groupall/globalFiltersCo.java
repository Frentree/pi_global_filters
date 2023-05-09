package com.iopts.skyun.recon.vo.groupall;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class globalFiltersCo {
	@SerializedName("expression")
	private String expression;
	
	@SerializedName("apply_to")
	private String apply_to;
	
	@SerializedName("id")
	private String id;

	@SerializedName("type")
	private String type;

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getApply_to() {
		return apply_to;
	}

	public void setApply_to(String apply_to) {
		this.apply_to = apply_to;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "globalFiltersCo [expression=" + expression + ", apply_to=" + apply_to + ", id=" + id + ", type=" + type + "]";
	}
	
}
