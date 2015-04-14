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

import com.atlassian.query.Query;
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
import com.atlassian.mail.MailException;
import com.atlassian.mail.MailFactory;
import com.atlassian.mail.server.SMTPMailServer;
import com.atlassian.jira.security.JiraAuthenticationContext;


public class CronServiceTask implements PluginJob{

	public static final String MAIL = "mail";
	private Email email;

	public void execute(Map<String, Object> jobDataMap) {

		final List<Issue> issues = this.getIssues();
		try {
			this.email = this.getEmail(issues);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		SMTPMailServer  mailServer = MailFactory.getServerManager().getDefaultSMTPMailServer();
		try {
			mailServer.send(this.email);
		} catch (MailException e) {
			e.printStackTrace();
		}
		final CronServiceImpl monitor = (CronServiceImpl)jobDataMap.get(CronServiceImpl.KEY);
		assert monitor != null;
		monitor.setLastRun(new Date());
	}
	
	private Email getEmail(List<Issue> issues) throws IOException{
		Email email = new Email("test@email");
		email.setFrom("test@email");
		email.setSubject("CSV Issues");
		
		if (issues.isEmpty()){
			email.setMimeType("text/plain");
			email.setBody("No issues");
		} else {
			email.setMimeType("multipart/mixed");
			StringBuilder sb = new StringBuilder();
			for(Issue item:issues){
				String str = item.getAssigneeId() + "," + item.getCreatorId() + ',' + item.getDescription();
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
				messageBodyPart.setDataHandler(new DataHandler(source, "multipart/mixed"));
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
		builder.where().project("TestProject");
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