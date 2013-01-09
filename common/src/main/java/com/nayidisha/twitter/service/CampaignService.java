package com.nayidisha.twitter.service;

import java.util.Collection;
import java.util.Date;

import com.nayidisha.twitter.domain.Campaign;
import com.nayidisha.twitter.domain.Dashboard;
import com.nayidisha.twitter.domain.TATweet;
import com.nayidisha.twitter.domain.TAUser;
import com.nayidisha.twitter.domain.TweetsSent;
import com.nayidisha.twitter.domain.TwitterFollower;
import com.nayidisha.twitter.domain.TwitterUser;

public interface CampaignService {
	public Collection<Campaign>getCampaignsForAUser(String userId);
	public TAUser addUser(TAUser user);
	public TAUser getUser(String userId);
	public void updateUser(TAUser user);
	public void deleteUser(String userId);
	public void updateTwitterer(TwitterUser tu) ;
	public Campaign addCampaign(Campaign campaign);
	public Collection<Campaign>getCampaignsByName(String name);
	public Campaign getUniqueCampaignByName(String name);
	public void updateCampaign(Campaign campaign);
	public void deleteCampaignsByUser(String userId, boolean withTweets);
	public void deleteCampaignButNotRelatedTweets(Long campaignId);
	public void deleteCampaignAndRelatedTweets(Long campaignId);
	public Collection<Campaign> loadCampaignsByUser(String userId);
	public TATweet addTweet(TATweet taTweet);
	public TATweet addTweet(TATweet taTweet, Campaign campaign);
	public TATweet addTweet(TATweet taTweet, String campaignName);
	public TATweet updateTweet(TATweet taTweet);
	public TATweet getTweet(Long id);
	public Collection<TATweet> getTweetsForACampaign(Campaign campaign);
	public Collection<TATweet> getTweetsForAUser(String userId);
	public void deleteTweet(Long id);
	public TwitterUser getTwitterer(Long campaignId, String twitterId) ;
	public void storeTargetTwitterers(Collection<TwitterUser> twitterUserList, Campaign campaign);
	public Collection<TwitterUser> getTwitterersForACampaign(Long campaignId);
	public void storeTweetsSent(TweetsSent tweetsSent);
	public void storeTweetsSent(Collection<TweetsSent> tweetsSentList);
	public boolean wasTweetSentToAnybodyInLastNHoursForCampaign(Long tweetId, int hours, Long campaignId);
	public boolean wasAnyTweetSentToTwittererInLastNMinutesForCampaign(String twitterId , int minutes, Collection<TweetsSent>  tweetsSentList);
	public Collection<TweetsSent> getSentToTwitterersForACampaign(Long campaignId);
	public Dashboard  getDashboard();
	public TATweet getEarliestTweetForCampaign(String campaignName);
	public TATweet getEarliestTweetForCampaign(Campaign campaign);
	public TATweet getLatestTweetForCampaign(Campaign campaign);
	public TATweet getLatestTweetForCampaign(String campaignName);
	public TwitterUser getEarliestSentTwitterUserForCampaign(Campaign campaign);
	public TwitterUser getLatestSentTwitterUserForCampaign(Campaign campaign);
	public TwitterUser getNextTwitterUserToFollowForCampaign(Campaign campaign, String followerId);
	public TwitterUser getTwitterUserTweetedToInThePastNHoursForCampaign(Campaign campaign,  String followerId, int hours);
	public TwitterFollower addFollower(TwitterFollower twitterFollower);

	public void flushSession();
}
