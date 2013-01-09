package com.nayidisha.twitter.service;

import com.nayidisha.twitter.domain.SchedulerDetail;


public interface SchedulerService {

	public SchedulerDetail showJobs(String jobName, String jobGroup, String userId) throws Exception;
	public void startJob(String campaignName, String jobName, String jobGroup, String triggerName) throws Exception;
	public void stopJob(String jobName, String jobGroup) throws Exception ; 
	
}
