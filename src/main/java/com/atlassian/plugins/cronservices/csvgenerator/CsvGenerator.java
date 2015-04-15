package com.atlassian.plugins.cronservices.csvgenerator;

import java.io.File;
import java.util.List;

import com.atlassian.jira.issue.Issue;

public interface CsvGenerator{
	
	public File getCsvReport();
	
	public void setIssues (List<Issue> issues);
	
}