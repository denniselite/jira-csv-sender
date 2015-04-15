package com.atlassian.plugins.cronservices.webactions;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.UrlMode;

import com.atlassian.plugins.cronservices.settings.AbstractPluginSettingsManager;

public class JiraAdminWebAction extends JiraWebActionSupport{

	private static final long serialVersionUID = 1L;

	private String email;
	private String interval;

	private final AbstractPluginSettingsManager settingsManager;

	public JiraAdminWebAction(PluginSettingsFactory pluginSettingsFactory){
		super();
		this.settingsManager = new AbstractPluginSettingsManager(pluginSettingsFactory);
	}

	@SuppressWarnings("unused")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@SuppressWarnings("unused")
	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	public String execute() throws Exception
	{
		settingsManager.setValue("email", getEmail());
		settingsManager.setValue("interval", getInterval());
//		addActionMessage("Saved");
		return SUCCESS;
	}
	
	public String doDefault() throws Exception
    {
        setEmail(settingsManager.getValue("email").toString());
        setInterval(settingsManager.getValue("interval").toString());
        return super.doDefault();
    }
}