package com.iopts.skyun.recon.vo.groupall;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class exportCsvCo {
	@SerializedName("id")
	private String id;
	
	@SerializedName("name")
	private String name;
	
	@SerializedName("type")
	private String type;

	@SerializedName("version")
	private String version;
	
	@SerializedName("platform")
	private String platform;
	
	@SerializedName("platform_compatibility")
	private String platform_compatibility;
	
	@SerializedName("verified")
	private String verified;
	
	@SerializedName("connected")
	private String connected;
	
	@SerializedName("connected_ip")
	private String connected_ip;
	
	@SerializedName("time_difference")
	private String time_difference;
	
	@SerializedName("proxy")
	private String proxy;
	
	@SerializedName("user")
	private String user;
	
	@SerializedName("cpu")
	private String cpu;
	
	@SerializedName("cores")
	private String cores;
	
	@SerializedName("boot")
	private String boot;

	@SerializedName("ram")
	private String ram;

	@SerializedName("started")
	private String started;

	private String regdate;
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getPlatform_compatibility() {
		return platform_compatibility;
	}

	public void setPlatform_compatibility(String platform_compatibility) {
		this.platform_compatibility = platform_compatibility;
	}

	public String getVerified() {
		return verified;
	}

	public void setVerified(String verified) {
		this.verified = verified;
	}

	public String getConnected() {
		return connected;
	}

	public void setConnected(String connected) {
		this.connected = connected;
	}

	public String getConnected_ip() {
		return connected_ip;
	}

	public void setConnected_ip(String connected_ip) {
		this.connected_ip = connected_ip;
	}

	public String getTime_difference() {
		return time_difference;
	}

	public void setTime_difference(String time_difference) {
		this.time_difference = time_difference;
	}

	public String getProxy() {
		return proxy;
	}

	public void setProxy(String proxy) {
		this.proxy = proxy;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getCpu() {
		return cpu;
	}

	public void setCpu(String cpu) {
		this.cpu = cpu;
	}

	public String getCores() {
		return cores;
	}

	public void setCores(String cores) {
		this.cores = cores;
	}

	public String getBoot() {
		return boot;
	}

	public void setBoot(String boot) {
		this.boot = boot;
	}

	public String getRam() {
		return ram;
	}

	public void setRam(String ram) {
		this.ram = ram;
	}

	public String getStarted() {
		return started;
	}

	public void setStarted(String started) {
		this.started = started;
	}


	public String getRegdate() {
		return regdate;
	}

	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}
	
	
	
}
