package com.atlassian.plugins.cronservices.csvgenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.atlassian.jira.issue.Issue;

public class CsvGeneratorImpl implements CsvGenerator{
	
	private File csvReport;
	private List<Issue> issues;
	
	@Override
	public File getCsvReport() {
		try {
			generate();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.csvReport;
	}

	@Override
	public void setIssues(List<Issue> issues) {
		this.issues = issues;
	}
	
	private void generate() throws IOException{
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
		csvReport = File.createTempFile("file", ".csv");
		csvReport.deleteOnExit();
		BufferedWriter out = new BufferedWriter(new FileWriter(csvReport));
		out.write(sb.toString());
		out.close();
	}
	
}