package com.atlassian.plugins.cronservices.jiracsvsender;

public interface CronService {
	
	public void reschedule(long interval);
    
}