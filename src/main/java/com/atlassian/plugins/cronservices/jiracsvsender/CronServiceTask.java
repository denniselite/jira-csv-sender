package com.atlassian.plugins.cronservices.jiracsvsender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import com.atlassian.plugins.cronservices.settings.SettingsManager;
import com.atlassian.query.Query;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.scheduling.PluginJob;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.mail.Email;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.mail.queue.SingleMailQueueItem;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

public class CronServiceTask implements PluginJob{

	public static final String MAIL = "mail";
	private Email email;

	private SettingsManager settingsManager;

	public void execute(Map<String, Object> jobDataMap) {
		final CronServiceImpl monitor = (CronServiceImpl)jobDataMap.get(CronServiceImpl.KEY);
		this.settingsManager = monitor.getSettingsManager();
		final List<Issue> issues = this.getIssues();
		try {
			this.email = this.getEmail(issues);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		SingleMailQueueItem item = new SingleMailQueueItem(this.email);
        ComponentAccessor.getMailQueue().addItem(item);
		assert monitor != null;
		monitor.setLastRun(new Date());
	}
	
	private Email getEmail(List<Issue> issues) throws IOException{
		Email email = new Email(settingsManager.getValue("email").toString());
		email.setFrom(settingsManager.getValue("email").toString());
		email.setSubject("CSV Issues");
		
		if (issues.isEmpty()){
			email.setMimeType("text/plain");
			email.setBody("No issues");
		} else {
			email.setMimeType("multipart/mixed");
			StringBuilder sb = new StringBuilder();
			String csvTableHeader = "Key;Creator;Assign to;Reporter;Summary;Description\n";
			sb.append(csvTableHeader);
			for(Issue item:issues){
				String str = 
						item.getKey() + ";" + 
						item.getCreatorId() + ";" + 
						item.getAssigneeId() + ";" + 
						item.getReporterId() + ";" + 
						item.getSummary() + ";" + 
						item.getDescription() + "\n";
	            sb.append(str);
	        }

			File tmp = File.createTempFile("file", ".csv");
			tmp.deleteOnExit();
			BufferedWriter out = new BufferedWriter(new FileWriter(tmp));
			out.write(sb.toString());
			out.close();
			Multipart mp = new MimeMultipart();
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(tmp);
			try {
				messageBodyPart.setDataHandler(new DataHandler(source));
			} catch (MessagingException e2) {
				e2.printStackTrace();
			}
			try {
				messageBodyPart.setFileName(tmp.getName());
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
			try {
				mp.addBodyPart(messageBodyPart);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			email.setMultipart(mp);
		}
		return email;
	}

	private List<Issue> getIssues() {
		List<Issue> issues = null;
		UserUtil userUtil = (UserUtil) ComponentAccessor.getComponentOfType(UserUtil.class);
		ApplicationUser appUser = userUtil.getUserByName("admin");
		JiraAuthenticationContext jac = (JiraAuthenticationContext) ComponentAccessor.getComponentOfType (JiraAuthenticationContext.class);
		jac.setLoggedInUser(appUser);
		User user = jac.getUser().getDirectoryUser();
		final JqlQueryBuilder builder = JqlQueryBuilder.newBuilder();
		builder.where().project("TestProject").and().status("IN PROGRESS");
		Query query = builder.buildQuery();
		try
		{
			SearchService searchService = ComponentAccessor.getComponent(SearchService.class);
			final SearchResults results = searchService.search(user,query, PagerFilter.getUnlimitedFilter());
		    issues = results.getIssues();
		}
		catch (SearchException e)
		{
			e.printStackTrace();
		}
		return issues;
	}

}