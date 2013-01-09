package com.nayidisha.twitter.domain;

import java.util.Collection;

public class SchedulerDetail {
	private Collection<NDJobDetail> jobDetailList;
	private Collection<GenericLineItem> triggerList;
	
	private String triggerName;
	private String nextAction;
	public Collection<NDJobDetail> getJobDetailList() {
		return jobDetailList;
	}
	public void setJobDetailList(Collection<NDJobDetail> jobDetailList) {
		this.jobDetailList = jobDetailList;
	}
	public String getNextAction() {
		return nextAction;
	}
	public void setNextAction(String nextAction) {
		this.nextAction = nextAction;
	}
	public Collection<GenericLineItem> getTriggerList() {
		return triggerList;
	}
	public void setTriggerList(Collection<GenericLineItem> triggerList) {
		this.triggerList = triggerList;
	}
	public String getTriggerName() {
		return triggerName;
	}
	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}
}
