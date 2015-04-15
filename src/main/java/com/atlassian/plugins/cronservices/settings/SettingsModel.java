package com.atlassian.plugins.cronservices.settings;

public interface SettingsModel{
	
	public void setEmail(String email);
	
	public String getEmail();
	
	public void setInterval(long interval);
	
	public long getInterval();
	
	public void setContextPath(String contextPath);
	
	public String getContextPath();
	
}