package com.atlassian.plugins.cronservices.settings;

import java.util.List;

public interface SettingsModel{
	
	public void setEmail(String email);
	
	public String getEmail();
	
	public void setInterval(long interval);
	
	public long getInterval();
	
	public void setContextPath(String contextPath);
	
	public void setProjectName(String projectName);
	
	public String getProjectName();
	
	public String getContextPath();
	
}