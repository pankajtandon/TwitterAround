
package com.nayidisha.twitter.jobs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public class TestJob implements Job { 
	static final Log log = LogFactory.getLog(TestJob.class.getName());
	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.info("Doing nothing... just logging");
	}

}
