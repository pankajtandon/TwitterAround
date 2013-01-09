package com.nayidisha.twitter.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.nayidisha.common.NDRuntimeException;
import com.nayidisha.twitter.interceptor.Auditable;

public class Campaign  implements Auditable, Comparable<Campaign>, Cloneable {

	public static final String CAMPAIGN_NAME = "campaignName";
	private Long id;

	private String name;
	private String sourceTwitterId;
	//private String sourceTwitterIdPassword;
	private Date createdDate;
	private String createdBy;
	private String updatedBy;
	private Date updatedDate;
	private boolean enabled;
	private String profileKeywords;
	private boolean checkAllProfileKeywords;
	private String excludeProfileKeywords; // words that should NOT be in the selected twitterers profile.
	private String tweetKeywords;
	//private String findUserFreq;
	//private String tweetFreq;
	private TAUser owner;
	private Set<TATweet> associatedTweets = new HashSet<TATweet>();
	
	/**
	 * Clones the primitive members PLUS the associated TATweet objects
	 * See also TATweet.clone()
	 */
	public Campaign clone(){
		Campaign clonedCampaign = null;
		try {
			clonedCampaign = (Campaign)super.clone();
			if (this.getAssociatedTweets() != null){
				Set<TATweet> associatedTATweetSet = this.getAssociatedTweets();
				Set<TATweet> clonedAssociatedTATweetSet = new HashSet<TATweet>();
				for (TATweet taTweet : associatedTATweetSet) {
					TATweet clonedTweet = taTweet.clone();
					clonedAssociatedTATweetSet.add(clonedTweet);
				}
				clonedCampaign.setAssociatedTweets(clonedAssociatedTATweetSet);
			}
			
		} catch (Exception e){
		     throw new NDRuntimeException("Could not clone TATweet");
		}
		return clonedCampaign;
	}
	
	public void addTweetToCampaign(TATweet tweet){
		associatedTweets.add(tweet);
		//tweet.addCampaignToTweet(this);
	}
	
	public void addTweetsToCampaign(HashSet<TATweet> tweetSet){
		associatedTweets.addAll(tweetSet);
		/*
		for (Iterator iterator = tweetSet.iterator(); iterator.hasNext();) {
			TATweet tweet = (TATweet) iterator.next();
			tweet.addCampaignToTweet(this);
		}
		*/
	}
	public boolean hasId(){
		boolean boo = false;
		if (id == null){
			boo = false;
		} else if (id.longValue() == 0){
			boo = false;
		} else {
			boo = true;
		}
		return boo;
	}
	public void removeTweet(TATweet taTweet){
		this.getAssociatedTweets().remove(taTweet);
	}
	
	public void removeAllTweets(){
		this.getAssociatedTweets().clear();
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
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	public TAUser getOwner() {
		return owner;
	}
	public void setOwner(TAUser owner) {
		this.owner = owner;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSourceTwitterId() {
		return sourceTwitterId;
	} 
	public void setSourceTwitterId(String sourceTwitterId) {
		this.sourceTwitterId = sourceTwitterId;
	}
/*	public String getSourceTwitterIdPassword() {
		return sourceTwitterIdPassword;
	}
	public void setSourceTwitterIdPassword(String sourceTwitterIdPassword) {
		this.sourceTwitterIdPassword = sourceTwitterIdPassword;
	}*/

	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getProfileKeywords() {
		return profileKeywords;
	}
	public void setProfileKeywords(String profileKeywords) {
		this.profileKeywords = profileKeywords;
	}
	public String getTweetKeywords() {
		return tweetKeywords;
	}
	public void setTweetKeywords(String tweetKeywords) {
		this.tweetKeywords = tweetKeywords;
	}
	public Set<TATweet> getAssociatedTweets() {
		return associatedTweets;
	}

	protected void setAssociatedTweets(Set<TATweet> associatedTweets) {
		this.associatedTweets = associatedTweets;
	}

	public int compareTo(Campaign o) {
		return (int)(this.getId().longValue() - ((Campaign)o).getId().longValue());
	}

	public boolean isCheckAllProfileKeywords() {
		return checkAllProfileKeywords;
	}

	public void setCheckAllProfileKeywords(boolean checkAllProfileKeywords) {
		this.checkAllProfileKeywords = checkAllProfileKeywords;
	}

	public String getExcludeProfileKeywords() {
		return excludeProfileKeywords;
	}

	public void setExcludeProfileKeywords(String excludeProfileKeywords) {
		this.excludeProfileKeywords = excludeProfileKeywords;
	}


	
	
	
}
