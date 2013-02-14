package com.sysiq.hostkeeper;

import java.util.UUID;

public class StatusRecord {
	
	private UUID id;
	
	private String host;
	
	private HostStatus hostStatus;
	
	private String date;
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
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
