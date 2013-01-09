package com.nayidisha.twitter.domain;

import java.util.Date;

public class TweetsSent {
	private Long id;
	private Long campaignId;
	private String targetTwitterId;
	private Long tweetId;
	private Date sentOn;
	private Boolean testOnly;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTargetTwitterId() {
		return targetTwitterId;
	}
	public void setTargetTwitterId(String targetTwitterId) {
		this.targetTwitterId = targetTwitterId;
	}

	public Date getSentOn() {
		return sentOn;
	}
	public void setSentOn(Date sentOn) {
		this.sentOn = sentOn;
	}
	public Long getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}
	public Long getTweetId() {
		return tweetId;
	}
	public void setTweetId(Long tweetId) {
		this.tweetId = tweetId;
	}
	public Boolean getTestOnly() {
		return testOnly;
	}
	public void setTestOnly(Boolean testOnly) {
		this.testOnly = testOnly;
	}


}
