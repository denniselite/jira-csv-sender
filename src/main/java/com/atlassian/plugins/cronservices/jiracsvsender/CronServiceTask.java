package com.atlassian.plugins.cronservices.jiracsvsender;

import java.util.Date;
import java.util.Map;
import javax.mail.MessagingException;

import com.atlassian.plugins.cronservices.settings.SettingsManager;
import com.atlassian.sal.api.scheduling.PluginJob;
import com.atlassian.jira.mail.Email;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.mail.queue.SingleMailQueueItem;
import com.atlassian.plugins.cronservices.letter.LetterServiceImpl;

public class CronServiceTask implements PluginJob{

	private Email email;

	private SettingsManager settingsManager;

	public void execute(Map<String, Object> jobDataMap) {
		final CronServiceImpl monitor = (CronServiceImpl)jobDataMap.get(CronServiceImpl.KEY);
		settingsManager = monitor.getSettingsManager();
		LetterServiceImpl LetterService = new LetterServiceImpl(settingsManager);
		try {
			email = LetterService.getLetter();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		SingleMailQueueItem item = new SingleMailQueueItem(email);
        ComponentAccessor.getMailQueue().addItem(item);
		assert monitor != null;
		monitor.setLastRun(new Date());
	}

}