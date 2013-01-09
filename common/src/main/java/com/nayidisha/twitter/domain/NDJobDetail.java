package com.nayidisha.twitter.domain;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import com.nayidisha.utils.NDDateUtils;



/**
 * This class represents a Job<Job> AND a trigger<CronTrigger>
 * @author AroundPoint
 *
 */
public class NDJobDetail implements Comparable{
	private int id;
	private String campaignName;
	private String name;
	private String group;
	transient private Scheduler scheduler;
	private Date prevRunTime;
	private Date nextRunTime;

	private String prevRunTimeString;
	private String nextRunTimeString;
	private String nextPossibleStatus;
	
	private static Log log = LogFactory.getLog(NDJobDetail.class);
	
	public NDJobDetail(){}
	
	public NDJobDetail(String campaignName, Scheduler scheduler, JobDetail jd) throws Exception{
		this.campaignName = campaignName;
		if (jd != null && scheduler != null){
			this.setName(jd.getName());
			this.setGroup(jd.getGroup());
			this.setScheduler(scheduler);
			
			Trigger[] triggers = null;

			triggers = scheduler.getTriggersOfJob(jd.getName(), jd.getGroup());

			if (triggers != null && triggers.length == 1){ //allow only one trigger per job
				Trigger currentTrigger = triggers[0];
				this.setPrevRunTime(currentTrigger.getPreviousFireTime());
				this.setNextRunTime(currentTrigger.getNextFireTime());
	
			}
		}
	}

	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getNextRunTime() {
		return nextRunTime;
	}
	public void setNextRunTime(Date nextRunTime) {
		this.nextRunTime = nextRunTime;
	}
	public String getNextRunTimeString() {
		nextRunTimeString = "never";
		if (this.getNextRunTime() != null){
			this.nextRunTimeString = NDDateUtils.applyNDStandardDateTime(this.getNextRunTime());
		}
		return nextRunTimeString;
	}
	public Date getPrevRunTime() {
		return prevRunTime;
	}
	public void setPrevRunTime(Date prevRunTime) {
		this.prevRunTime = prevRunTime;
	}
	public String getPrevRunTimeString() {
		prevRunTimeString = "never";
		if (this.getPrevRunTime() != null){
			this.prevRunTimeString = NDDateUtils.applyNDStandardDateTime(this.getPrevRunTime());
		}
		return prevRunTimeString;
	}

	public String getNextPossibleStatus() throws SchedulerException {
		nextPossibleStatus = "start";
		Scheduler scheduler = this.getScheduler();
		if (scheduler != null){
			JobDetail jd = scheduler.getJobDetail(this.getName(), this.getGroup());
			if (jd != null){
				nextPossibleStatus = "pause";
			}
		}
		return nextPossibleStatus;
	}

	public void setNextPossibleStatus(String nextPossibleStatus) {
		this.nextPossibleStatus = nextPossibleStatus;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public int compareTo(Object o) {
//		return (int)((this.getCampaignName() + this.getName()).hashCode() - 
//				(((NDJobDetail)o).getCampaignName() +((NDJobDetail)o).getName()).hashCode());
//		return (int)((this.getCampaignName() ).hashCode() -  (((NDJobDetail)o).getCampaignName()).hashCode() );		
	    return ((this.getId() -  (((NDJobDetail)o).getId())));
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
		
}
