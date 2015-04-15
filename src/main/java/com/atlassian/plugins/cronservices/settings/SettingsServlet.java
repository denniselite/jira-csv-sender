package com.atlassian.plugins.cronservices.settings;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.plugins.cronservices.jiracsvsender.CronService;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.templaterenderer.TemplateRenderer;

import java.io.IOException;
import java.util.Map;

import com.google.common.collect.Maps;

public class SettingsServlet extends HttpServlet{

	private final TemplateRenderer templateRenderer;
	private final SettingsManager settingsManager;
	private final CronService cronService;
	private final Map<String, Object> context = Maps.newHashMap();
	private final SettingsModelImpl outSettings = new SettingsModelImpl();
	
	public SettingsServlet(TemplateRenderer templateRenderer, PluginSettingsFactory pluginSettingsFactory, CronService cronService){
		super();
		this.templateRenderer = templateRenderer;
		this.settingsManager = new SettingsManager(pluginSettingsFactory);
		this.cronService = cronService;
	}

	//GET
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		outSettings.setEmail(settingsManager.getValue("email").toString());
		outSettings.setInterval(Long.parseLong(settingsManager.getValue("interval").toString()));
		outSettings.setContextPath(request.getContextPath());
		context.put("Settings", outSettings);
		templateRenderer.render("templates/Settings.vm", context, response.getWriter());
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
		settingsManager.setValue("email", req.getParameter("email"));
		settingsManager.setValue("interval", req.getParameter("interval"));
		outSettings.setEmail(settingsManager.getValue("email").toString());
		outSettings.setInterval(Long.parseLong(settingsManager.getValue("interval").toString()));
		outSettings.setContextPath(req.getContextPath());
		cronService.reschedule();
		context.put("Settings", outSettings);
		templateRenderer.render("templates/Settings.vm", context, res.getWriter());
    }
}