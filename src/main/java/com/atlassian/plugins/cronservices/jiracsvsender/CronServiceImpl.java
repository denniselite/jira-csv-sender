package com.atlassian.plugins.cronservices.jiracsvsender;

import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.scheduling.PluginScheduler;

import java.util.Date;
import java.util.HashMap;

public class CronServiceImpl implements CronService, LifecycleAware {

	    static final String KEY = CronServiceImpl.class.getName() + ":instance";
	    private static final String JOB_NAME = CronServiceImpl.class.getName() + ":job";

	    private final PluginScheduler pluginScheduler;  

	    private long interval = 5000L;         
	    private Date lastRun = null;        

	    public CronServiceImpl(PluginScheduler pluginScheduler) {
	        this.pluginScheduler = pluginScheduler;
	    }

	    public void onStart() {
	        reschedule(interval);
	    }

	    public void reschedule(long interval) {
	        this.interval = interval;
	        
	        pluginScheduler.scheduleJob(
	                JOB_NAME,                   // unique name of the job
	                CronServiceTask.class,     // class of the job
	                new HashMap<String,Object>() {{
	                    put(KEY, CronServiceImpl.this);
	                }},                         // data that needs to be passed to the job
	                new Date(),                 // the time the job is to start
	                interval);                  // interval between repeats, in milliseconds
	        System.out.println("Issues search task scheduled to run every "+interval);
	    }

	    /* package */ void setLastRun(Date lastRun) {
	        this.lastRun = lastRun;
	    }
}