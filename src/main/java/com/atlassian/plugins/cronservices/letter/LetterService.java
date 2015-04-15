package com.atlassian.plugins.cronservices.letter;

import javax.activation.DataSource;
import javax.mail.MessagingException;

import com.atlassian.jira.mail.Email;

public interface LetterService{
	
	public Email getLetter() throws MessagingException;

}