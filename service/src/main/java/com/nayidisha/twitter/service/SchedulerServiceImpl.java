package com.nayidisha.twitter.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;


import com.nayidisha.twitter.domain.Campaign;
import com.nayidisha.twitter.domain.GenericLineItem;
import com.nayidisha.twitter.domain.NDJobDetail;
import com.nayidisha.twitter.domain.SchedulerDetail;
import com.nayidisha.twitter.jobs.NDJob;
import com.nayidisha.twitter.jobs.NDTrigger;
import com.nayidisha.utils.NDStringUtils;

public class SchedulerServiceImpl implements SchedulerService {

	static final Log log = LogFactory.getLog(SchedulerServiceImpl.class.getName());
	
	private Scheduler quartzScheduler;
	private CampaignService campaignService;
	
	public SchedulerDetail showJobs(String jobName, String jobGroup, String userId) throws Exception{
	       Scheduler sched = this.getQuartzScheduler();

	        ArrayList<NDJobDetail> allJobs = new ArrayList<NDJobDetail>();
	        Collection<Campaign> campaignList = campaignService.getCampaignsForAUser(userId);
	        int count = 1;
	        for (Iterator iterator = campaignList.iterator(); iterator.hasNext();) {
	        	Campaign campaign = (Campaign) iterator.next();
		        for(NDJob job:NDJob.values()){
		        	String name = job.getName();
		        	String group = job.getGroup();
		        	String shortDesc = job.getShortDesc();
		        	Class clazz = job.getClazz();
		        	JobDetail jobDetail = new JobDetail();
		        	jobDetail.setName(name);
		        	jobDetail.setGroup(group);
		        	jobDetail.setDescription(shortDesc);
		        	jobDetail.setJobClass(clazz);
		        	log.debug("Before calling scheduler for JobName: " +  jobDetail.getName() + ": " + sched.toString());
		    		NDJobDetail njd = new NDJobDetail(campaign.getName(), sched, jobDetail);
		    		log.debug("After calling scheduler for JobName: " +  jobDetail.getName() + ": " + sched.toString());
		    		njd.setId(count++);
		    		allJobs.add(njd);
		        }
			}
	        
	        ArrayList<GenericLineItem> allTriggers = new ArrayList<GenericLineItem>();
	        
	        for (NDTrigger trigger:NDTrigger.values()){
	        	String name = trigger.getName();
	        	String desc = trigger.getDesc();
	        	GenericLineItem gli = new GenericLineItem();
	        	gli.setId(name);
	        	gli.setDescription(desc);
	        	allTriggers.add(gli);
	        }
	        SchedulerDetail sd = new SchedulerDetail();
	        sd.setJobDetailList(allJobs);
	        sd.setTriggerList(allTriggers);
	        sd.setNextAction("startJob");

	        if (NDStringUtils.hasSomeMeaningfulValue(jobName) &&
	        		NDStringUtils.hasSomeMeaningfulValue(jobGroup)) {
	        	
	        	JobDetail jd = null;
	        	//The showJobs command was called to schedule one job.
	        	Trigger[] triggers = sched.getTriggersOfJob(jobName, jobGroup);
	        	if (triggers != null && triggers.length == 1){
	        		//Only one trigger per job is allowed
	        		Trigger trigger = triggers[0];
	        		sd.setTriggerName(trigger.getName());
	        		sd.setNextAction("stopJob");
	        	}
	        }

		return sd;
	}

	public void startJob(String campaignName, String jobName, String jobGroup, String triggerName) throws Exception{
    	
            if (NDStringUtils.hasSomeMeaningfulValue(jobName) &&
            		NDStringUtils.hasSomeMeaningfulValue(jobGroup) &&
            		NDStringUtils.hasSomeMeaningfulValue(triggerName) ){
	    		NDJob ndJob = NDJob.getNDJob(jobName , jobGroup);
	            Class clazz = ndJob.getClazz();
	            NDTrigger ndTrigger = NDTrigger.getNDTrigger(triggerName);
	            String triggerGroup = ndTrigger.getGroup();
	            String cronExpression = ndTrigger.getCronExpression();
	            
	            Scheduler sched = this.getQuartzScheduler();
	            
	            
	            Date fireTime = null;
	            JobDetail job = new JobDetail(jobName, jobGroup, clazz);
	            job.getJobDataMap().put(Campaign.CAMPAIGN_NAME, campaignName);
	            sched.addJob(job, true);
	            
	            CronTrigger newTrigger = new CronTrigger(triggerName, triggerGroup, jobName, jobGroup, cronExpression);
	            Trigger[] existingTriggers = sched.getTriggersOfJob(jobName, jobGroup);
	            if (existingTriggers == null || existingTriggers.length == 0){   
	            	//No existing... sched a new one
		            fireTime = sched.scheduleJob(newTrigger);
	            } else {
	            	//replace the FIRST existing trigger with the new one
	            	Trigger existingTrigger = existingTriggers[0];
	            	fireTime = sched.rescheduleJob(existingTrigger.getName(), existingTrigger.getGroup(), newTrigger);
	            }
	            log.info("Job " + jobName  + " scheduled to fire at " + fireTime + "(CampaignName is " + campaignName + ")");
            }

	}

	public void stopJob(String jobName, String jobGroup) throws Exception {    	
        if (NDStringUtils.hasSomeMeaningfulValue(jobName) &&
        		NDStringUtils.hasSomeMeaningfulValue(jobGroup) ){
            Scheduler sched = this.getQuartzScheduler();
            sched.deleteJob(jobName, jobGroup);
            log.info("Job " + jobName + " deleted.");
        }

	}


	public Scheduler getQuartzScheduler() {
		return quartzScheduler;
	}

	public void setQuartzScheduler(Scheduler quartzScheduler) {
		this.quartzScheduler = quartzScheduler;
	}

	public CampaignService getCampaignService() {
		return campaignService;
	}

	public void setCampaignService(CampaignService campaignService) {
		this.campaignService = campaignService;
	}

}
