package com.atlassian.plugins.cronservices.settings;

public class SettingsModelImpl implements SettingsModel{

	private String email;
	private long interval;
	private String contextPath;

	public SettingsModelImpl(){
		
	}

	@Override
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String getEmail() {
		return this.email;
	}

	@Override
	public void setInterval(long interval) {
		this.interval = interval;		
	}

	@Override
	public long getInterval() {
		return this.interval;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

}