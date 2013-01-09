package com.nayidisha.twitter.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import twitter4j.Tweet;

import com.nayidisha.common.NDRuntimeException;
import com.nayidisha.twitter.interceptor.Auditable;

public class TATweet implements Auditable, Comparable<TATweet>, Cloneable{
	private Logger log = Logger.getLogger(TATweet.class.getName());
	//private Tweet tweet;
	private Long id;
	private String description;
	private String fromUser;
	private String toUser;
	private Long weight ;
	private Set<Campaign> associatedCampaigns = new HashSet<Campaign>();
	private Date lastSentDate;
	
	private Date createdDate;
	private String createdBy;
	private String updatedBy;
	private Date updatedDate;
	
	public void addCampaignToTweet(Campaign campaign){
		if (associatedCampaigns != null){
			associatedCampaigns.add(campaign);
		} else {
			associatedCampaigns = new HashSet<Campaign>();
			associatedCampaigns.add(campaign);
		}
	}
	
	public void addCampaignsToTweet(Set<Campaign> campaignSet){
		if (associatedCampaigns != null){
			associatedCampaigns.addAll(campaignSet);
		} else {
			associatedCampaigns = new HashSet<Campaign>();
			associatedCampaigns.addAll(campaignSet);
		}		
	}
	
	/**
	 * Since TATweet holds a collection of Campaign objects and each Campaign object
	 * holds a collection of TATweet objects, we will end up with a StackOverflow if we clone both.
	 * So this clone only clones the primitives, NOT the associated Campaigns. 
	 */
	public TATweet clone(){
		TATweet clonedTATweet = null;
		try {
			clonedTATweet = (TATweet)super.clone();
			
		} catch (Exception e){
		     throw new NDRuntimeException("Could not clone TATweet");
		}
		return clonedTATweet;
	}
	
	public void directTo(TwitterUser tu){
		String id = tu.getTwitterId();
		this.setDescription("@" + id + " " + this.getDescription() );
		if (this.getDescription().length() > 140){
			throw new NDRuntimeException("Tweet too long (" + this.getDescription().length() + ")");
		}
	}
	
	public void directTo(TwitterUser tu, boolean chompExtra){
		String id = tu.getTwitterId();
		this.setDescription("@" + id + " " + this.getDescription() );
		if (chompExtra && this.getDescription().length() > 140){
			log.warn("Chomped this tweet to 140 characters: " + this.getDescription());
			this.setDescription(this.getDescription().substring(0,139));
			log.info("Chomped tweet is: " + this.getDescription());
		}
		
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
	
	public String getIdString(){
		return (id==null?"null":id.toString());
	}
	

	public void removeAllAssociatedCampaigns(){
		this.getAssociatedCampaigns().clear();
	}
	
	public void removeAssociatedCampaign(Campaign campaign){
		if (associatedCampaigns != null){
			associatedCampaigns.remove(campaign);
		}
	}
	
	public Campaign getFirstCampaign(){
		Campaign campaign = null;
		if (this.getAssociatedCampaigns() != null){
			Set<Campaign> campaignSet = this.getAssociatedCampaigns();
			for (Campaign campaign2 : campaignSet) {
				campaign = campaign2;
				break;
			}
		}
		return campaign;
	}
	
/*	public Tweet getTweet() {
		return tweet;
	}
	public void setTweet(Tweet tweet) {
		this.tweet = tweet;
	}*/
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFromUser() {
		return fromUser;
	}
	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}
	public String getToUser() {
		return toUser;
	}
	public void setToUser(String toUser) {
		this.toUser = toUser;
	}
	public Long getWeight() {
		return weight;
	}
	public void setWeight(Long weight) {
		this.weight = weight;
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

	public Set<Campaign> getAssociatedCampaigns() {
		return associatedCampaigns;
	}

	protected void setAssociatedCampaigns(Set<Campaign> associatedCampaigns) {
		this.associatedCampaigns = associatedCampaigns;
	}

	public int compareTo(TATweet o) {
		return (int)(this.getId().longValue() - ((TATweet)o).getId().longValue());
	}

	public Date getLastSentDate() {
		return lastSentDate;
	}

	public void setLastSentDate(Date lastSentDate) {
		this.lastSentDate = lastSentDate;
	}
}
