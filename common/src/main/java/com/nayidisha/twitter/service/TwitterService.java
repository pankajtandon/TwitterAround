package com.nayidisha.twitter.service;

import java.util.ArrayList;

import twitter4j.Tweet;

import com.nayidisha.twitter.domain.TwitterUser;



public interface TwitterService {


	//public TwitterUser getDefaultTwitterUser() ;
	public TwitterUser getTwitterUser(String userId) ;
	public ArrayList<Tweet> searchTweets(String searchString);
	//public ArrayList<TwitterUser> findNonRepeatingTwitterersAmongstThoseWhoAreTalkingAbout(String campaignName, String talkingAboutString);
	public void storeNonRepeatingTwitterersForACampaign(String campaignName);
	public void sendEarliestTweetToEarliestTwitterer(String campaignName);
	public void followTwitterers(String campaignName);
	
	public void flushSession();
}
