package com.nayidisha.twitter.jobs;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nayidisha.twitter.domain.Campaign;
import com.nayidisha.twitter.service.TwitterService;
import com.nayidisha.utils.AppContextUtils;
import com.nayidisha.utils.NDUtils;

public class SendTweetsJob implements Job {

	private Logger log = Logger.getLogger(SendTweetsJob.class.getName());
	
	public void execute(JobExecutionContext jec) throws JobExecutionException {
		log.info("Starting SendTweetsJob job...");
		TwitterService twitterService = AppContextUtils.getTwitterService();
		
		//At this time we also need to set a user in the context
		//TODO: Change this to a batchUser and read password from a prop file that is filtered.
		NDUtils.setUpSecurityContext("testUser", "password", "ROLE_USER");
		
		JobDataMap jdm = jec.getJobDetail().getJobDataMap();
		String campaignName = (String)jdm.get(Campaign.CAMPAIGN_NAME);
		twitterService.sendEarliestTweetToEarliestTwitterer(campaignName);
		log.info("Ended SendTweetsJob");
	}




}
