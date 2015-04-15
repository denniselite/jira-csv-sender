package com.atlassian.plugins.cronservices.jiracsvsender;

import com.atlassian.plugins.cronservices.settings.SettingsManager;

public interface CronService {
	
	public void reschedule();
	
	public SettingsManager getSettingsManager();
    
}