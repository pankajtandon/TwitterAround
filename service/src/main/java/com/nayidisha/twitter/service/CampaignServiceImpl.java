package com.nayidisha.twitter.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;


import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;
import org.hibernate.NonUniqueObjectException;

import com.nayidisha.common.CampaignDoesNotExistException;
import com.nayidisha.common.CampaignNameNotUniqueException;
import com.nayidisha.common.NDRuntimeException;
import com.nayidisha.twitter.dataaccess.CampaignDAO;
import com.nayidisha.twitter.domain.Campaign;
import com.nayidisha.twitter.domain.Dashboard;
import com.nayidisha.twitter.domain.TATweet;
import com.nayidisha.twitter.domain.TweetsSent;
import com.nayidisha.twitter.domain.TwitterFollower;
import com.nayidisha.twitter.domain.TwitterUser;
import com.nayidisha.twitter.domain.TAUser;
import com.nayidisha.utils.NDDateUtils;
import com.nayidisha.utils.NDStringUtils;
import com.nayidisha.utils.NDUtils;

public class CampaignServiceImpl implements CampaignService {
	
	private Logger log = Logger.getLogger(CampaignServiceImpl.class.getName());
	
	private CampaignDAO campaignDAO;

	public Campaign addCampaign(Campaign c){
		Collection<Campaign> list = campaignDAO.getCampaignsByName(c.getName());
		if (!NDUtils.isZeroLengthOrNullCollection(list)) {
			throw new CampaignNameNotUniqueException("Campaign with name " + c.getName() + " already exists!");
		}
		campaignDAO.addCampaign(c);
		return c;
	}
	
	public void updateCampaign(Campaign newCampaign){
			campaignDAO.updateCampaign(newCampaign);
	}
	
	public Campaign getUniqueCampaignById(Long campaignId){
		return campaignDAO.getCampaignById(campaignId);
	}
	public void deleteCampaignButNotRelatedTweets(Long campaignId){
		campaignDAO.deleteCampaignButNotRelatedTweets(campaignId);
	}
	

	public void deleteCampaignAndRelatedTweets(Long campaignId) {
		campaignDAO.deleteCampaignAndRelatedTweets(campaignId);
	}
	
	public void deleteTweet(Long id){
		campaignDAO.deleteTweet(id);
	}

	public Collection<Campaign>getCampaignsByName(String name){
		return campaignDAO.getCampaignsByName(name);
	}
	public Campaign getUniqueCampaignByName(String name){
		return campaignDAO.getUniqueCampaignByName(name);
	}
	
	public Collection<Campaign> loadCampaignsByUser(String userId){
		return campaignDAO.loadCampaignsByUser(userId);
	}
	
	public TATweet addTweet(TATweet taTweet){
		//This is being done at the time of creating a tweet because then the method
		//to get the earliest tweet has a date to work off of, instead of working with a null date
		taTweet.setLastSentDate(getEarlyDate());
		return campaignDAO.addTweet(taTweet);
	}
	public TATweet addTweet(TATweet taTweet, Campaign campaign){
		if (!campaign.hasId()) {
			throw new CampaignDoesNotExistException("The campaign does not exist. Please ensure that the campaign exists before adding a tweet ");
		}
		if (!NDStringUtils.hasSomeMeaningfulValue(taTweet.getFromUser())){
			taTweet.setFromUser(campaign.getSourceTwitterId());
		}
		//This is being done at the time of creating a tweet because then the method
		//to get the earliest tweet has a date to work off of, instead of working with a null date
		taTweet.setLastSentDate(getEarlyDate());
		return campaignDAO.addTweet(taTweet, campaign);
	}
	
	public TATweet addTweet(TATweet taTweet, String campaignName){
		//This is being done at the time of creating a tweet because then the method
		//to get the earliest tweet has a date to work off of, instead of working with a null date
		taTweet.setLastSentDate(getEarlyDate());
		return campaignDAO.addTweet(taTweet, campaignName);
	}
	
	/**
	 * This should be a fully populated Tweet (with campaigns etc).
	 * If there are no campaigns associated then they will get whacked from the db
	 * if they existed previously
	 */
	public TATweet updateTweet(TATweet taTweet){
		return campaignDAO.updateTweet(taTweet);
	}
	
	public TATweet getTweet(Long id){
		return campaignDAO.getTweet(id);
	}
	
	
	public TwitterFollower addFollower(TwitterFollower twitterFollower){
		return campaignDAO.addFollower(twitterFollower);
	}
	
	public Collection<TATweet> getTweetsForACampaign(Campaign campaign){
		Collection<TATweet> tweetList = campaignDAO.getTweetsForACampaign(campaign);
		
		//Iterate to ensure we get all tweets before session closes
		for (Iterator iterator = tweetList.iterator(); iterator.hasNext();) {
			TATweet tweet = (TATweet) iterator.next();
			//nothing
		}
		return tweetList;
	}
	
	public Collection<Campaign>getCampaignsForAUser(String userId){
		TAUser user = this.getCampaignDAO().getUser(userId);
		
		//For getting all objects put in a dummy loop
		for (Iterator iterator = user.getAssociatedCampaigns().iterator(); iterator.hasNext();) {
			Campaign campaign = (Campaign) iterator.next();
			//do nothing
		}
		return user.getAssociatedCampaigns();
	}


	public void setcampaignDAO(CampaignDAO campaignDAO) {
		this.campaignDAO = campaignDAO;
	}

	public TAUser getUser(String userId) {
		return campaignDAO.getUser(userId);
	}
	
	public TAUser addUser(TAUser user) {
		return campaignDAO.addUser(user);
	}
	public void updateUser(TAUser user) {
		campaignDAO.updateUser(user);
	}
	
	public void deleteUser(String userId) {
		campaignDAO.deleteUser(userId);
		
	}
	
	public void updateTwitterer(TwitterUser tu) {
		campaignDAO.updateTwitterer(tu);
	}
	
	public Collection<TATweet> getTweetsForAUser(String userId){
		return this.getCampaignDAO().getTweetsForAUser(userId);
	}

	public void deleteCampaignsByUser(String userId, boolean withTweets) {
		Collection<Campaign> campaignList = this.getCampaignsForAUser(userId);
		if (!NDUtils.isZeroLengthOrNullCollection(campaignList)){
			for (Iterator iterator = campaignList.iterator(); iterator
					.hasNext();) {
				Campaign campaign = (Campaign) iterator.next();
				if (withTweets){
					this.deleteCampaignAndRelatedTweets(campaign.getId());
				}else {
					this.deleteCampaignButNotRelatedTweets(campaign.getId());
				}
			}
		}
	}
	public Collection<TwitterUser> getTwitterersForACampaign(Long campaignId){
		return this.getCampaignDAO().getTwitterersForACampaign(campaignId);
	}
	
	public TwitterUser getTwitterer(Long campaignId, String twitterId) {
		return this.getCampaignDAO().getTwitterer(campaignId, twitterId);
	}
	
	public void storeTargetTwitterers(Collection<TwitterUser> twitterUserList, Campaign campaign){
		
		for (Iterator iterator = twitterUserList.iterator(); iterator.hasNext();) {
			TwitterUser twitterUser = (TwitterUser) iterator.next();
			//Retrieve the twitterer from db to up the acquire count
			TwitterUser tu = this.getCampaignDAO().getTwitterer(campaign.getId(), twitterUser.getTwitterId());
			if (tu != null){
				int previousAcquireCount = tu.getAcquireCount();
				//The only thing that can be re-captured is the profile info
				if (twitterUser.getProfileDescription() != null && !twitterUser.getProfileDescription().equals(campaign.getProfileKeywords())){
					//profileKeywords are not forced into the proileDesc because of error etc
					tu.setProfileDescription(twitterUser.getProfileDescription());
				}
//				try {
//					BeanUtils.copyProperties(tu, twitterUser);
//				}catch (Exception ite){
//					throw new NDRuntimeException("Could not copy TwitterUser ", ite); 
//				}
				
				tu.setAcquireCount(previousAcquireCount + 1);
				log.info("Updating twitterer " + tu.getTwitterId());
			} else {
				//New twitterer
				twitterUser.setAcquireCount(1);
				tu = twitterUser;
				tu.setCapturedOn(new Date());
				log.info("Adding twitterer " + tu.getTwitterId());
			}
			tu.setCampaignId(campaign.getId());
			this.getCampaignDAO().addTwitterer(tu);
			

			
		}
	}
	
	public void storeTweetsSent(Collection<TweetsSent> tweetsSentList){
		for (Iterator iterator = tweetsSentList.iterator(); iterator.hasNext();) {
			TweetsSent tweetsSent = (TweetsSent) iterator.next();
			this.storeTweetsSent(tweetsSent);
		}
	}

	public void storeTweetsSent(TweetsSent tweetsSent){
		this.getCampaignDAO().saveOrUpdateTweetsSent(tweetsSent);
	}
	
	
	public boolean wasTweetSentToAnybodyInLastNHoursForCampaign(Long tweetId, int hours, Long campaignId){
		boolean tweetSentOrNot = false;
		Collection<TweetsSent> tweetsSentList = this.getCampaignDAO().getSentTweetForCampaign(tweetId, campaignId);
		Date now = new Date();
		for (Iterator iterator = tweetsSentList.iterator(); iterator.hasNext();) {
			TweetsSent tweetSent = (TweetsSent) iterator.next();
			if (tweetSent.getSentOn() != null){
				if (NDDateUtils.hoursBetween(tweetSent.getSentOn(), now) < hours) {
					tweetSentOrNot = true;
					break;
				}
			}
		}
		return tweetSentOrNot;
	}


	public boolean wasAnyTweetSentToTwittererInLastNMinutesForCampaign(String twitterId, int minutes, Collection<TweetsSent>  tweetsSentList) {
		boolean twitteredToToOrNot = false;
		Date now = new Date();
		for (Iterator iterator = tweetsSentList.iterator(); iterator.hasNext();) {
			TweetsSent tweetSent = (TweetsSent) iterator.next();
			if (tweetSent.getSentOn() != null){
				if (tweetSent.getTargetTwitterId().equals(twitterId)){
					if (NDDateUtils.minutesBetween(tweetSent.getSentOn(), now) < minutes) {
						//This twitterer was sent SOME tweet in 'minutes' interval
						twitteredToToOrNot = true;
						break;
					}
				}
			}
		}
		return twitteredToToOrNot;
	}


	public Collection<TweetsSent> getSentToTwitterersForACampaign(Long campaignId){
		return this.getCampaignDAO().getSentToTwitterersForACampaign(campaignId);
	}

	public TATweet getEarliestTweetForCampaign(String campaignName){
		Campaign campaign = this.getUniqueCampaignByName(campaignName);
		//this.getTweetsForACampaign(campaign);
		TATweet tw =  this.getCampaignDAO().getEarliestTweetForCampaign(campaign.getId());
		
		//Get associated Campaigns
		TATweet fullTweet = this.getCampaignDAO().getTweet(tw.getId());
		tw.addCampaignsToTweet(fullTweet.getAssociatedCampaigns());
		
		return tw;
	}
	

	public TATweet getEarliestTweetForCampaign(Campaign campaign){
		//this.getTweetsForACampaign(campaign);
		TATweet tw =  this.getEarliestTweetForCampaign(campaign.getName());
		return tw;
	}
	
	public TATweet getLatestTweetForCampaign(String campaignName){
		Campaign campaign = this.getUniqueCampaignByName(campaignName);
		//this.getTweetsForACampaign(campaign);
		TATweet tw =  this.getCampaignDAO().getLatestTweetForCampaign(campaign.getId());
		
		return tw;
	}
	

	public TATweet getLatestTweetForCampaign(Campaign campaign){
		//this.getTweetsForACampaign(campaign);
		TATweet tw =   this.getCampaignDAO().getLatestTweetForCampaign(campaign.getId());
		
		return tw;
	}
	
	public TwitterUser getEarliestSentTwitterUserForCampaign(Campaign campaign){
		TwitterUser tu =  this.getCampaignDAO().getEarliestSentTwitterUserForCampaign(campaign.getId());
		
		return tu;
	}
	
	public TwitterUser getLatestSentTwitterUserForCampaign(Campaign campaign){
		TwitterUser tu =  this.getCampaignDAO().getLatestSentTwitterUserForCampaign(campaign.getId());
		
		return tu;
	}
	
	public TwitterUser getNextTwitterUserToFollowForCampaign(Campaign campaign, String followerId){
		TwitterUser tu =  this.getCampaignDAO().getNextTwitterUserToFollowForCampaign(campaign.getId(), followerId);
		
		return tu;
	}
	public TwitterUser getTwitterUserTweetedToInThePastNHoursForCampaign(Campaign campaign,  String followerId, int hours){
		TwitterUser tu =  this.getCampaignDAO().getTwitterUserTweetedToInThePastNHoursForCampaign(campaign.getId(), followerId, hours);
		
		return tu;
	}	
	public TwitterUser getEarliestSentTwitterUserForCampaign(String campaignName){
		Campaign campaign = this.getUniqueCampaignByName(campaignName);
		//this.getTweetsForACampaign(campaign);
		TwitterUser tu =  this.getCampaignDAO().getEarliestSentTwitterUserForCampaign(campaign.getId());
		
		return tu;
	}
	
	public Dashboard getDashBoard(){
		return this.getCampaignDAO().getDashboard();
	}
	
	public CampaignDAO getCampaignDAO() {
		return campaignDAO;
	}

	public void setCampaignDAO(CampaignDAO campaignDAO) {
		this.campaignDAO = campaignDAO;
	}

	public Dashboard getDashboard() {
	   return this.getCampaignDAO().getDashboard();
	}
	
	private Date getEarlyDate(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.YEAR, 2000);
		cal.getTime();

		return cal.getTime();
		
	}
	public void flushSession() {
		this.getCampaignDAO().flushSession();
	}
	

}
