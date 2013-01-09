package com.nayidisha.twitter.domain;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.nayidisha.twitter.interceptor.Auditable;
import com.nayidisha.utils.NDDateUtils;
import com.nayidisha.utils.NDStringUtils;

import twitter4j.User;


/**
 * Persisted in TWITTER_USER table
 * @author 721479
 *
 */
public class TwitterUser extends TAUser implements java.io.Serializable, Auditable{

	private static final long serialVersionUID = 4085236395205915873L;




	public TwitterUser(){}
	
	public TwitterUser(User eu){
		this.setTwitterId(eu.getScreenName());
		this.setId(new Integer(eu.getId()).toString());
		this.setTwitteringSince(eu.getCreatedAt());
		this.setProfileDescription(eu.getDescription());
		this.setFollowersCount(eu.getFollowersCount());
		this.setFriendsCount(eu.getFriendsCount());
		this.setCreatedDate(eu.getCreatedAt());
		this.setTwitteringSince(eu.getCreatedAt());
		//this.setCreatedDate(new Date());//Hack: Should be done by the auditInterceptor
	}

	private Long campaignId;
	private String twitterId;
	private String profileDescription;
	private int friendsCount;
	private int followersCount;
	private int acquireCount;
	private Date capturedOn;
	private Date twitteringSince;
	private Long weight;
	private boolean onHold;
	private Date lastSent;


	public boolean hasProfile(){
		boolean boo = false;
		if (NDStringUtils.hasSomeMeaningfulValue(this.getProfileDescription())){
			boo = true;
		}
		return boo;
	}
	
	public long getDaysTweeting(){
		long i = 0;
		if (this.getCreatedDate() != null){
			Date now = new Date();
			if (this.getCreatedDate().before(now)){
				i = NDDateUtils.daysBetween(this.getCreatedDate(), now);
			}
		}
		return i;
	}
	
	public int getFriendsCount() {
		return friendsCount;
	}

	public void setFriendsCount(int friendsCount) {
		this.friendsCount = friendsCount;
	}

	public int getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}



	public String toString() {
		   return ToStringBuilder.reflectionToString(this);
	}

	public String getTwitterId() {
		return twitterId;
	}

	public void setTwitterId(String twitterId) {
		this.twitterId = twitterId;
	}

	public String getProfileDescription() {
		return profileDescription;
	}

	public void setProfileDescription(String profileDescription) {
		this.profileDescription = profileDescription;
	}

	public int getAcquireCount() {
		return acquireCount;
	}

	public void setAcquireCount(int acquireCount) {
		this.acquireCount = acquireCount;
	}

	public Date getCapturedOn() {
		return capturedOn;
	}

	public void setCapturedOn(Date capturedOn) {
		this.capturedOn = capturedOn;
	}

	public Long getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}

	public Date getTwitteringSince() {
		return twitteringSince;
	}

	public void setTwitteringSince(Date twitteringSince) {
		this.twitteringSince = twitteringSince;
	}

	public Long getWeight() {
		return weight;
	}

	public void setWeight(Long weight) {
		this.weight = weight;
	}

	public boolean isOnHold() {
		return onHold;
	}

	public void setOnHold(boolean onHold) {
		this.onHold = onHold;
	}

	public Date getLastSent() {
		return lastSent;
	}

	public void setLastSent(Date lastSent) {
		this.lastSent = lastSent;
	}




}
