package com.nayidisha.twitter.dataaccess;

import java.util.Collection;
import java.util.Date;

import com.nayidisha.twitter.domain.Campaign;
import com.nayidisha.twitter.domain.Dashboard;
import com.nayidisha.twitter.domain.TATweet;
import com.nayidisha.twitter.domain.TweetsSent;
import com.nayidisha.twitter.domain.TwitterFollower;
import com.nayidisha.twitter.domain.TwitterUser;

import com.nayidisha.twitter.domain.TAUser;

public interface CampaignDAO {
	public TAUser addUser(TAUser user);
	public void updateUser(TAUser user) ;
	public TAUser getUser(String userId);
	public void deleteUser(String userId);
	public Collection<Campaign> loadCampaignsByUser(String userId);
	public void addCampaign(Campaign campaign);
	public Collection<Campaign> getCampaignsByName(String name);
	public Campaign getCampaignById(Long campaignId);
	public Campaign getUniqueCampaignByName(String name);
	public void updateCampaign(Campaign c);
	public void deleteCampaign(Campaign c);
	public void deleteCampaignButNotRelatedTweets(Long campaignId);
	public void deleteCampaignAndRelatedTweets(Long campaignId);
	public TATweet addTweet(TATweet t); 
	public TATweet addTweet(TATweet t, Campaign campaign);
	public TATweet addTweet(TATweet t, String campaignName);
	public TATweet updateTweet(TATweet t);
	public TATweet getTweet(Long id);
	public Collection<TATweet> getTweetsForACampaign(Campaign campaign);
	public Collection<TATweet> getTweetsForAUser(String userId);
	public void deleteTweet(TATweet tweet);
	public void deleteTweet(Long tweetId);
	public TwitterUser getTwitterer(Long campaignId, String twitterId) ;
	public TwitterUser addTwitterer(TwitterUser twitterUser);
	public TwitterUser updateTwitterer(TwitterUser twitterUser);
	public Collection<TwitterUser> getTwitterersForACampaign(Long campaignId);
	public void saveOrUpdateTweetsSent(TweetsSent tweetsSent);
	public Collection<TweetsSent> getSentTweetForCampaign(Long tweetId, Long campaignId);
	public Collection<TweetsSent> getSentToTwitterersForACampaign(Long campaignId);
	public Dashboard  getDashboard();
	public TATweet getEarliestTweetForCampaign(Long campaignId);
	public TATweet getLatestTweetForCampaign(Long campaignId);
	public TwitterUser getEarliestSentTwitterUserForCampaign(Long campaignId);
	public TwitterUser getLatestSentTwitterUserForCampaign(Long campaignId);
	public TwitterUser getNextTwitterUserToFollowForCampaign(Long campaignId, String followerId);
	public TwitterFollower addFollower(TwitterFollower twiterFollower);
	public TwitterUser getTwitterUserTweetedToInThePastNHoursForCampaign(Long campaignId, String followerId, int hours);
	
	/**
	 * Hibernate or ORM DAOs can implement this
	 */
	public void flushSession();
	
}
