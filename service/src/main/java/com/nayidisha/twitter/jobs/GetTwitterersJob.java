package com.nayidisha.twitter.jobs;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;


import com.nayidisha.twitter.domain.Campaign;
import com.nayidisha.twitter.service.TwitterService;
import com.nayidisha.utils.AppContextUtils;
import com.nayidisha.utils.NDUtils;

public class GetTwitterersJob implements Job {
	
	private Logger log = Logger.getLogger(GetTwitterersJob.class.getName());

	public void execute(JobExecutionContext jec) throws JobExecutionException {
		
		log.info("Starting GetTwitterersJob job...");
		TwitterService twitterService = AppContextUtils.getTwitterService();
		
		//At this time we also need to set a user in the context
		//TODO: Change this to a batchUser and read password from a prop file that is filtered.
		NDUtils.setUpSecurityContext("testUser", "password", "ROLE_USER"); 

		JobDataMap jdm = jec.getJobDetail().getJobDataMap();
		String campaignName = (String)jdm.get(Campaign.CAMPAIGN_NAME);
		twitterService.storeNonRepeatingTwitterersForACampaign(campaignName);
		log.info("Ended GetTwitterersJob");
	}

	private void setUpContext(String u, String p){
		GrantedAuthority authoritiesArray[] = new GrantedAuthority[]{new GrantedAuthorityImpl("ROLE_USER")};
		Authentication authentication = new UsernamePasswordAuthenticationToken(u, p, authoritiesArray);
		SecurityContextHolder.getContext().setAuthentication(authentication);

	}

}
