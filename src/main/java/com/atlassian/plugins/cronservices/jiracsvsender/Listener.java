package com.atlassian.plugins.cronservices.jiracsvsender;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import com.atlassian.plugins.cronservices.settings.SettingsManager;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

public class Listener implements InitializingBean, DisposableBean {

	private final SettingsManager settingsManager;
    
	public Listener(PluginSettingsFactory pluginSettingsFactory){
		this.settingsManager = new SettingsManager(pluginSettingsFactory);
	}
	
	@Override
    public void destroy() throws Exception {
		settingsManager.remove("EMPTY_SETTINGS");
		settingsManager.remove("email");
		settingsManager.remove("interval");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Installation complete");
        settingsManager.setValue("EMPTY_SETTINGS", "YES");
    }
}