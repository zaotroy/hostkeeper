package com.sysiq.hostkeeper;


public class StatusRecord {
	
	private Integer id;
	
	private String host;
	
	private HostStatus hostStatus;
	
	private String date;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public HostStatus getHostStatus() {
		return hostStatus;
	}

	public void setHostStatus(HostStatus hostStatus) {
		this.hostStatus = hostStatus;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
