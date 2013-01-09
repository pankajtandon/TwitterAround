package com.nayidisha.twitter.domain;

import java.util.Date;

import com.nayidisha.twitter.interceptor.Auditable;

public class Dashboard implements Auditable {

	public static Long DASHBOARD_ID = new Long(1);
	
	private Long id;
	private boolean sendTweetTest;
	private boolean followTest;
	
	private Date createdDate;
	private String createdBy;
	private String updatedBy;
	private Date updatedDate;
	

	public boolean isSendTweetTest() {
		return sendTweetTest;
	}

	public void setSendTweetTest(boolean sendTweetTest) {
		this.sendTweetTest = sendTweetTest;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isFollowTest() {
		return followTest;
	}

	public void setFollowTest(boolean followTest) {
		this.followTest = followTest;
	}
}
