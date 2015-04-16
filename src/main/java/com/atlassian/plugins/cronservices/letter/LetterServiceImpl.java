package com.atlassian.plugins.cronservices.letter;

import java.io.File;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.mail.Email;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugins.cronservices.csvgenerator.CsvGeneratorImpl;
import com.atlassian.plugins.cronservices.settings.SettingsManager;
import com.atlassian.query.Query;

public class LetterServiceImpl implements LetterService{

	private Email email;
	private List<Issue> issues;
	private SettingsManager settingsManager;

	public LetterServiceImpl(SettingsManager settingsManager){
		this.settingsManager = settingsManager;
	}
	@Override
	public Email getLetter() throws MessagingException {
		issues = getIssues();
		email = new Email(settingsManager.getValue("email").toString());
		email.setFrom(settingsManager.getValue("email").toString());
		email.setSubject("CSV Issues");
		if (issues.isEmpty()){
			email.setMimeType("text/plain");
			email.setBody("No issues");
		} else {
			email.setMimeType("multipart/mixed");
			CsvGeneratorImpl csvGenerator = new CsvGeneratorImpl();
			csvGenerator.setIssues(issues);
			File report = csvGenerator.getCsvReport();
			Multipart mp = new MimeMultipart();
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(report);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(report.getName());
			mp.addBodyPart(messageBodyPart);
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
		builder.where().project(settingsManager.getValue("projectName").toString()).and().status("IN PROGRESS");
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