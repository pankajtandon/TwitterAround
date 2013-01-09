package com.nayidisha.twitter.domain;

import java.util.Date;

public class TwitterFollower implements java.io.Serializable {

	private static final long serialVersionUID = -3136139182332278745L;
	private String followeeId;
	private String followerId;
	private Date followStartDate;
	private Date followEndDate;
	
	public String getFolloweeId() {
		return followeeId;
	}
	public void setFolloweeId(String followeeId) {
		this.followeeId = followeeId;
	}
	public String getFollowerId() {
		return followerId;
	}
	public void setFollowerId(String followerId) {
		this.followerId = followerId;
	}
	public Date getFollowStartDate() {
		return followStartDate;
	}
	public void setFollowStartDate(Date followStartDate) {
		this.followStartDate = followStartDate;
	}
	public Date getFollowEndDate() {
		return followEndDate;
	}
	public void setFollowEndDate(Date followEndDate) {
		this.followEndDate = followEndDate;
	}
}
