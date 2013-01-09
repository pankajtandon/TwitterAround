package com.nayidisha.twitter.service;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

import com.nayidisha.common.NDRuntimeException;
import com.nayidisha.twitter.dataaccess.TwitterDAO;
import com.nayidisha.twitter.domain.Campaign;
import com.nayidisha.twitter.domain.TAOAuthToken;
import com.nayidisha.twitter.domain.TATweet;
import com.nayidisha.twitter.domain.TweetsSent;
import com.nayidisha.twitter.domain.TwitterFollower;
import com.nayidisha.twitter.domain.TwitterUser;
import com.nayidisha.utils.NDDateUtils;
import com.nayidisha.utils.NDStringUtils;
import com.nayidisha.utils.NDUtils;
import com.nayidisha.utils.PropertyReader;

public class TwitterServiceImpl implements TwitterService{

	private Logger log = Logger.getLogger(TwitterServiceImpl.class.getName());
	
	private Twitter twitter;

	private TwitterDAO twitterDAO;
	
	private CampaignService campaignService;


	//Should not be able to create service without a valid twitterId
	private TwitterServiceImpl(){}
	
	public TwitterServiceImpl(String userId, String password, String httpProxy, String httpProxyPort){

	    ConfigurationBuilder cb = new ConfigurationBuilder();
	    if (!StringUtils.isEmpty(httpProxy)){
	    	cb.setHttpProxyHost(httpProxy); 
	    	cb.setHttpProxyPort(Integer.parseInt(httpProxyPort));
	    }
	    
	    // The factory instance is re-useable and thread safe.
	    TwitterFactory factory = new TwitterFactory(cb.build());
	    twitter = factory.getInstance();
	    
	    //This key points to ap's account
		String consumerKey = PropertyReader.getProperty("consumerKey");
		String consumerSecret = PropertyReader.getProperty("consumerSecret");
	    twitter.setOAuthConsumer(consumerKey, consumerSecret); 
	}

	public String getAuthorizationURL() throws Exception {


	    RequestToken requestToken = twitter.getOAuthRequestToken();

	    String url = requestToken.getAuthorizationURL();
	    return url;
	}
	
	public void storeAccessKey() throws Exception {
		AccessToken accessToken = twitter.getOAuthAccessToken();
		//TODO: Store
	}
	
	private AccessToken getAccessToken(String twitterId){
		AccessToken at = null;
		TAOAuthToken token = this.getTwitterDAO().getAccessTokenForTwitterId(twitterId);
		if (token != null){
			at = new AccessToken(token.getToken(), token.getTokenSecret());
		} else {
			throw new NDRuntimeException("Error getting authentication token for " + twitterId + ". Make sure that you have issued an OAuth for the TwitAround application for this twitterer!");
		}
		return at;
	}
	
	public void authenticateTwitter(String twitterId){
		
		twitter.setOAuthAccessToken(getAccessToken(twitterId));
	}
	
	public static void main( String[] args ){ 

		if(args.length != 2){
			System.out.println("ERROR - you must specify a twitter username and a password.");
			System.out.println("Usage: [username] [password]");
			return;
		}

		try {
			Twitter twitter = new Twitter(args[0], args[1]);
			//twitter.setHttpProxy("internet.ground.fedex.com", 80);
			
			
			//twitter.update("Updating Twitter from my own Java code!");
			
			/*
			QueryResult qr = twitter.search(new Query("monkey"));
			List<Tweet> tweetList = qr.getTweets();
			System.out.println("Size: " + tweetList.size());
			for (Iterator iterator = tweetList.iterator(); iterator.hasNext();) {
				Tweet tweet = (Tweet) iterator.next();
				System.out.println("Tweet: "  + tweet.getFromUser() + "-" + tweet.getText());
			}
			List statuses = twitter.getUserTimeline();
	
			
			System.out.println(" -- " + args[0].toUpperCase() + " timeline --");
			//prints each status of the public user timeline
			for (Object status : statuses) {
				Status sta = (Status)status;
				System.out.println(sta.getId() + " - " + sta.getText());
			}
			*/
			
			String userId = "aplusk";
			User eu = twitter.showUser(userId);
			System.out.println("Stats: Friends Count:" + eu.getFriendsCount() + ", FollowersCount: " + eu.getFollowersCount() + ", numUpdates: " + eu.getStatusesCount());;
	
			System.out.println("First Tweet: " + eu.getCreatedAt());
			List<Status> mentionList = twitter.getMentions();
			for (Iterator iterator = mentionList.iterator(); iterator.hasNext();) {
				Status status = (Status) iterator.next();
				System.out.println("Mentions: " + status.getText());
			}
			
			List<Status> timeLineList = twitter.getUserTimeline(userId, new Paging(10));


			for (Iterator iterator = timeLineList.iterator(); iterator.hasNext();) {
				Status status = (Status) iterator.next();
				System.out.println("TL:: " + status.getCreatedAt() + "-" + status.getText());
			}

			
			Query query = new Query();
			long id = query.getSinceId();
			
			System.out.println("ID: " + id);
			
			System.out.println("Done!");
		} catch (TwitterException ex) {
			ex.printStackTrace();
		}
	}

	/* TOFIX: Probably not needed anymore
  	public TwitterUser getDefaultTwitterUser() {
		TwitterUser tu = null;

		try {
			

			User eu = twitter.getUserDetail(twitter.getUserId());
			tu = new TwitterUser(eu);
			
		} catch (TwitterException te){
			throw new NDRuntimeException("Error getting user info.", te);
		}

		return tu;
	}
	*/
	public TwitterUser getTwitterUser(String twitterId) {
		TwitterUser tu = null;
		try {
			User eu = twitter.showUser(twitterId);
			log.debug("ExtendedUser: " + (eu==null?"NULL":eu.toString()));
			
			tu = new TwitterUser(eu);
			
		} catch (TwitterException te){
			log.warn("Error retrieving TwitterUser for "  + twitterId + ": " + te.getMessage());
			//squelch: Could be because of rate limit exceeded
			//return a null tu
		}
		return tu;
	}
	


	public ArrayList<Tweet> searchTweets(String searchString){
		ArrayList<Tweet> al = new ArrayList<Tweet>();
		try {
		    Query query = new Query(URLEncoder.encode(searchString, "UTF-8"));
		    QueryResult result = twitter.search(query);
		    
		    al.addAll(result.getTweets());
		} catch (IllegalArgumentException iae){
			//supress throw new NDRuntimeException("Error searching", iae);
			log.warn("Error getting results", iae);
		} catch (Exception te){
			throw new NDRuntimeException("Error searching", te);
		}
		log.debug("Search with searchString " + searchString + " resulted in " + al.size() + " tweets.");
		return al;

	}
	
	public void storeNonRepeatingTwitterersForACampaign(String campaignName){
		log.debug("Starting getTwitterersForACampaign");
		Campaign campaign = this.getCampaignService().getUniqueCampaignByName(campaignName);
		if (campaign != null){
			//String tweetKeywords = campaign.getTweetKeywords();
			//String profileKeywords = campaign.getProfileKeywords();
			
			Collection<TwitterUser> twitterUserList = this.findNonRepeatingTwitterersAmongstThoseWhoAreTalkingAbout(campaign, campaign.getTweetKeywords());
			log.info("Going to store " + (twitterUserList==null?"0":twitterUserList.size()) + " twitterers ...");
			this.getCampaignService().storeTargetTwitterers(twitterUserList, campaign);
		} else {
			throw new NDRuntimeException("This campaign " + campaignName + " does not exist!");
		}
		log.debug("Ending getTwitterersForACampaign");
	}
	
	/**
	 * This method sends out the earliest sent tweet to the earliest sent twitterer for a campaign
	 */
	public void sendEarliestTweetToEarliestTwitterer(String campaignName){
		log.debug("Starting sendEarliestTweetToEarliestTwitterer");
		Campaign campaign = this.getCampaignService().getUniqueCampaignByName(campaignName);
		if (campaign != null){

			
 
			TATweet earliestTweet = campaignService.getEarliestTweetForCampaign(campaign);
			
			TwitterUser earliestTwitterUser = campaignService.getEarliestSentTwitterUserForCampaign(campaign);

			earliestTweet.setLastSentDate(new Date());
			campaignService.updateTweet(earliestTweet);
			log.info("Check: Updated lastSent for TweetId: " + earliestTweet.getId());

			//Update time on TwitterUser			
			earliestTwitterUser.setLastSent(new Date());
			campaignService.updateTwitterer(earliestTwitterUser);
			log.info("Check: Updated lastSent for TwittererId: " + earliestTwitterUser.getTwitterId());
			
			//Update the TweetSent db
			TweetsSent ts = new TweetsSent();
			ts.setCampaignId(campaign.getId());
			ts.setTargetTwitterId(earliestTwitterUser.getTwitterId());
			ts.setTweetId(earliestTweet.getId());
			ts.setSentOn(new Date());
			campaignService.storeTweetsSent(ts);
			log.info("Check: Updated TweetSent");
			
			TATweet clonedTweet = earliestTweet.clone();
			try {
				clonedTweet.directTo(earliestTwitterUser, true);
				
				String sourceTwitterId = campaign.getSourceTwitterId();
				authenticateTwitter(sourceTwitterId);
				//Since there is an unresolvable problem on the Tomcat/Linux combination that causes the transaction not
				//to commit (http://forums.mysql.com/read.php?39,28450,28450#msg-28450),
				//I am doing the db save first. It the db save fails, Tweet is not sent out
				//If the tweet fails then the db is rolled back.
				sendOutTheTweet(ts, clonedTweet);

			} catch (TwitterException te){
				log.warn("Exception tweeting " + clonedTweet.getDescription() + ". ", te);
				//Cause a rollback of db transaction
				throw new NDRuntimeException("Exception Tweeting.", te); 
			}
		}
		log.debug("Ending sendEarliestTweetToEarliestTwitterer");
	}

	
	public void followTwitterers(String campaignName){
		log.debug("Starting followTwitterer");
		Campaign campaign = this.getCampaignService().getUniqueCampaignByName(campaignName);
		if (campaign != null){
			String sourceTwitterId = campaign.getSourceTwitterId();
			/* TOFIX			String sourceTwitterPassword = campaign.getSourceTwitterIdPassword();
			//THIS person will Follow twitterers
			twitter.setUserId(sourceTwitterId);
			twitter.setPassword(sourceTwitterPassword);*/
			
			TwitterUser nextTwitterUserToFollow = campaignService.getTwitterUserTweetedToInThePastNHoursForCampaign(campaign, sourceTwitterId, 1);

			try {
				int counter = 0;
				for(;;){
					log.debug("Going to try to befriend " + nextTwitterUserToFollow.getTwitterId());
					
					//if ((nextTwitterUserToFollow != null) && (!twitter.existsFriendship(nextTwitterUserToFollow.getTwitterId(), sourceTwitterId))){
					//For some reason the existsFriendship is issuing a permission denied, proceeding without checking
					if ((nextTwitterUserToFollow != null) && (true)){
						//First insert into table
						//Insert into the twitter_follower table to prevent a follow next time
						TwitterFollower tf = new TwitterFollower();
						tf.setFolloweeId(nextTwitterUserToFollow.getTwitterId());
						tf.setFollowerId(sourceTwitterId);
						tf.setFollowStartDate(new Date());
						campaignService.addFollower(tf);
						
						//Then try to befriend
						//twitter.enableNotification(nextTwitterUserToFollow.getTwitterId());
						//Above api not working, using createFreindship instead. 
						//http://code.google.com/p/twitter-api/issues/detail?id=933
						if (!campaignService.getDashboard().isFollowTest()){
							twitter.createFriendship(nextTwitterUserToFollow.getTwitterId(), true);
							log.info("Befriended TwitterId: " + nextTwitterUserToFollow.getTwitterId());
						} else {
							log.info("TEST: Befriended TwitterId: " + nextTwitterUserToFollow.getTwitterId());
						}
						
						
						break;
					} else {
						if (counter > 5){
							//Don't try more than 5 times
							log.warn("Could not befriend 5 times... punting till the next run...");
							break;
						}
						log.debug("Already friends with " + nextTwitterUserToFollow.getTwitterId());
						nextTwitterUserToFollow = campaignService.getTwitterUserTweetedToInThePastNHoursForCampaign(campaign, sourceTwitterId, 1);
					}
					counter++;
				}

				
			} catch (TwitterException te){
				throw new NDRuntimeException("Exception Tweeting.", te); 
			}			
		}
	}
	
	
	/**
	 * Get the earliest sent tweet
	 * Send to all twitterers
	 * upate timestamp on the sent tweet
	 * done
	 */
	
	public void sendTweetsToTwitterers(String campaignName){
		log.info("Starting to send tweets");
		Campaign campaign = this.getCampaignService().getUniqueCampaignByName(campaignName);
		if (campaign != null){
			String sourceTwitterId = campaign.getSourceTwitterId();
			/* TOFIX			String sourceTwitterPassword = campaign.getSourceTwitterIdPassword();
			//Send tweets as this person
			twitter.setUserId(sourceTwitterId);
			twitter.setPassword(sourceTwitterPassword);*/
			
			Collection<TwitterUser> twitterUserList = campaignService.getTwitterersForACampaign(campaign.getId());
			Collection<TATweet> tweetList = campaignService.getTweetsForACampaign(campaign);
						
			int twittererCount = 0;
			TATweet earliestTweet = campaignService.getEarliestTweetForCampaign(campaign);
			
			for (Iterator iterator = twitterUserList.iterator(); iterator.hasNext();) {
				TwitterUser twitterUser = (TwitterUser) iterator.next();
				if (!twitterUser.isOnHold()){
					//Only tweet to those folks that are not on hold
					TATweet clonedTweet = earliestTweet.clone();
					clonedTweet.directTo(twitterUser, true);
	
					try {
						if (campaignService.getDashboard().isSendTweetTest()){
							log.debug("TEST: Sending: " + clonedTweet.getDescription());
						} else {
							//Actually send the tweet
							//twitter.updateStatus(tweet.getDescription());
							if (false) throw new TwitterException("test");
							log.debug("Sending: " + clonedTweet.getDescription());
						}
						twittererCount++;
					} catch (TwitterException te){
						log.warn("Exception tweeting " + clonedTweet.getDescription() + ". Moving on...", te);
					}
				}else {
					log.debug("Skipping tweet to twitterId " + twitterUser.getTwitterId() + " because s/he is on hold");
				}
			}
			
			//Update time on the tweet
			TATweet pulledTweet = campaignService.getTweet(earliestTweet.getId());
			
			try {
				BeanUtils.copyProperties(earliestTweet, pulledTweet);
			} catch (Exception e){
				//not good
				throw new NDRuntimeException("Error copying objects", e);
			}
			
			earliestTweet.setLastSentDate(new Date());
			campaignService.updateTweet(earliestTweet);
			log.info("TweetSendStats: Sent :" + earliestTweet.getDescription() + " to " + twittererCount + " twitterers." );
		}
	}
	
	//----------------- P R I V A T E ------------------------------
	
	private TwitterUser getTwitterUser(String twitterId, String profileKeywords) {
		TwitterUser tu = null;
		try {
			User eu = twitter.showUser(twitterId);
			tu = new TwitterUser(eu);			
		} catch (TwitterException te){
			log.warn("Proceeding with partial TU, even tho' error in retrieving TwitterUser for "  + twitterId + ": " + te.getMessage());
			//squelch: Could be because of rate limit exceeded
			//return a partially populated tu and force in the profileKeywords for future processing
			tu = new TwitterUser();
			tu.setTwitterId(twitterId);
			tu.setProfileDescription(profileKeywords);
			tu.setOnHold(true); //This flag is purposely not set to false in the code, but is set via the UI.
		}
		return tu;
	}
	
	/**
	 * First get all tweets in the tweet-o-sphere of folks that are tweeting about the 'talkingAbout' string.
	 * Note, this uses Twitters Search API.
	 * Then get the fromUser from each tweet and get that user's profile (using TwitterAPI (non-Search))
	 * and look for ALL of the profile keywords in ANY order for that twitterer to be included.
	 * Finally, make sure that this twitterer is not already captured.
	 */
	//TODO: Make sure that these are unique because > 1 can tweet and get snagged. Use a Set instead
	private ArrayList<TwitterUser> findNonRepeatingTwitterersAmongstThoseWhoAreTalkingAbout(Campaign campaign, String talkingAboutString){
		Collection<TwitterUser> existingTwitterUsers = campaignService.getTwitterersForACampaign(campaign.getId());
		
		ArrayList<TwitterUser> al = new ArrayList<TwitterUser>();

		ArrayList<Tweet> tweetList = searchTweets(talkingAboutString);
		log.info("We found " + tweetList.size() + " tweets while searching for " + talkingAboutString);
		for (Iterator iterator = tweetList.iterator(); iterator.hasNext();) {
			Tweet tweet = (Tweet) iterator.next();
			String twitterId = tweet.getFromUser();
			TwitterUser tu = null;
			processPotentialTwitterer(campaign, existingTwitterUsers, al,
					twitterId);
		}
		log.info("We found " + (al==null?"0":al.size()) + " twitterers in the tweets ");
		return al;

	}

	private void processPotentialTwitterer(Campaign c,
			Collection<TwitterUser> existingTwitterUsers,
			ArrayList<TwitterUser> al, String twitterId) {
		Date earlyDate = NDDateUtils.toDateFromNDStandardDate("Jan 01 2000");
		TwitterUser tu;
		//The call to get a twitterId profile is expensive (counts towards the rate limit also)
		//So first make sure that we don't already have this twitterer and if we do, then don't waste a call
		if (!alreadyExistsWithGoodProfile(existingTwitterUsers, twitterId,  c.getProfileKeywords())){
			//Doesn't exist or has a bad profile... retrieve...
			tu = this.getTwitterUser(twitterId, c.getProfileKeywords());
			if (tu != null && tu.getProfileDescription() != null){
				
				//First check that this twitterer does not have an excluded keyword in his/her profile
				boolean hasExcludedKeywordsInProfile = false;
				Collection<String> excludedProfileKeywordList = NDStringUtils.commaDelimitedStringToCollection(c.getExcludeProfileKeywords());
				
				if (!NDUtils.isZeroLengthOrNullCollection(excludedProfileKeywordList)){
					//Some words need to be excluded...
					for (Iterator iterator = excludedProfileKeywordList
							.iterator(); iterator.hasNext();) {
						String string = (String) iterator.next();
						if (tu.getProfileDescription().toLowerCase().indexOf(string.toLowerCase()) != -1){
							//excluded keywords IS in description
							hasExcludedKeywordsInProfile = true;
							break;
						}
					}
				}
				
				if (!hasExcludedKeywordsInProfile){
					//All words in profile are legit.. proceed to see if profile has desired words...
					
					Collection<String> profileKeywordList = NDStringUtils.commaDelimitedStringToCollection(c.getProfileKeywords());
					if (!NDUtils.isZeroLengthOrNullCollection(profileKeywordList)){
						boolean oneKeywordAbsent = false;
						boolean anyKeywordPresent = false;
						
						for (Iterator iterator2 = profileKeywordList.iterator(); iterator2
								.hasNext();) {
							String string = (String) iterator2.next();
							if (tu.getProfileDescription().toLowerCase().indexOf(string.toLowerCase()) == -1){
								//keyword is not in description.
								oneKeywordAbsent = true;
							} else {
								//Keyword is in description
								anyKeywordPresent = true;
							}
							
						}
	
						
	
						if (c.isCheckAllProfileKeywords()){
							//All keywords must be present
						    if (!oneKeywordAbsent){
								//All keywords in profile are present
								//Since last_sent column in db cannot be null
								//because null messes up the getting of earliestSent Twitterer
								//we will force capture time in it.
								tu.setLastSent(earlyDate);
								al.add(tu);
						    }
						} else {
							//If ANY keyword is present, add twitterer...
							if (anyKeywordPresent){
								//All keywords in profile are present
								//Since last_sent column in db cannot be null
								//because null messes up the getting of earliestSent Twitterer
								//we will force capture time in it.							
								tu.setLastSent(earlyDate);
								al.add(tu);
							}
							
						}
	
					}
				}
			}
		}else {
			//Already existing twitterer with a good profile
			log.info("User " + twitterId + " already existed in our db. Incrementing acquire count");
			tu = getFromExistingTwitterers(existingTwitterUsers, twitterId);
			//Don't up the lastSentDate because it should already exist.
			al.add(tu);
		}
	}
	
	private TwitterUser getFromExistingTwitterers(Collection<TwitterUser> existingTwitterUserList, String twitterId){
		TwitterUser tu = null;
		for (Iterator iterator = existingTwitterUserList.iterator(); iterator
				.hasNext();) {
			TwitterUser twitterUser = (TwitterUser) iterator.next();
			if (twitterUser.getTwitterId().equals(twitterId)){
				tu = twitterUser;
				break;
			}
		}
		return tu;
	}
	

	
	private boolean alreadyExistsWithGoodProfile(Collection<TwitterUser> existingTwittererUserList, String twitterId, String tokenInProfile){
		boolean boo = false;
		for (Iterator iterator = existingTwittererUserList.iterator(); iterator
				.hasNext();) {
			TwitterUser twitterUser = (TwitterUser) iterator.next();
			
			if (twitterUser.getTwitterId().equals(twitterId)){

				if (twitterUser.hasProfile() && !twitterUser.getProfileDescription().equals(tokenInProfile)){
					boo = true;
				} else {
					log.info("User " + twitterId + " exists but with no profile data OR profileData that has been forced to be tokenInProfile due to rateLimit or other error");
				}

			}
		}
		return boo;
	}
	/**
	 * Get the earliest sent tweet and the earliest Sent Twitterer and send one tweet
	 * upate timestamp on the sent tweet and the sent twitterer
	 * done
	 */
	

	private void sendOutTheTweet(TweetsSent ts, TATweet clonedTweet)
			throws TwitterException {
		if (campaignService.getDashboard().isSendTweetTest()){
			log.info("Check: TEST: Sending: " + clonedTweet.getDescription());
			ts.setTestOnly(true);
		} else {
			//Actually send the tweet
			twitter.updateStatus(clonedTweet.getDescription());
			if (false) throw new TwitterException("test");
			ts.setTestOnly(false);
			log.info("Check: Sending: " + clonedTweet.getDescription());
		}
	}
	
	
	
	//-------------------- G / S ------------------------------
	public TwitterDAO getTwitterDAO() {
		return twitterDAO;
	}

	public void setTwitterDAO(TwitterDAO twitterDAO) {
		this.twitterDAO = twitterDAO;
	}

	public CampaignService getCampaignService() {
		return campaignService;
	}

	public void setCampaignService(CampaignService campaignService) {
		this.campaignService = campaignService;
	}


	

	//------------------- D E P R E C A T E D -------------------
	
	public void sendTweetsToTwitterersOld(String campaignName){
		log.info("Starting to send tweets");
		Campaign campaign = this.getCampaignService().getUniqueCampaignByName(campaignName);
		if (campaign != null){
			String sourceTwitterId = campaign.getSourceTwitterId();
			/* TOFIX			String sourceTwitterPassword = campaign.getSourceTwitterIdPassword();
			//Send tweets as this person
			twitter.setUserId(sourceTwitterId);
			twitter.setPassword(sourceTwitterPassword);*/
			
			Collection<TwitterUser> twitterUserList = campaignService.getTwitterersForACampaign(campaign.getId());
			Collection<TATweet> tweetList = campaignService.getTweetsForACampaign(campaign);
			
			//In the beginning get all twitterers (to whom tweets have been sent for this campaign) and store for efficiency
			Collection<TweetsSent> tweetsSentToTwittererList = campaignService.getSentToTwitterersForACampaign(campaign.getId());
			
			int tweetCount = 0;
			int twittererCount = 0;
			//Iterate over tweets
			for (Iterator iterator = tweetList.iterator(); iterator.hasNext();) {
				TATweet tweet = (TATweet) iterator.next();
				int hours = 24;
				boolean tweetSentOrNot = campaignService.wasTweetSentToAnybodyInLastNHoursForCampaign(tweet.getId(), hours, campaign.getId() );
				if (tweetSentOrNot == false){
					//This tweet has not been sent out in 'hours'.
					ArrayList<TweetsSent> tweetsSentList = new ArrayList<TweetsSent>();
					
					twittererCount = 0;
					//Iterate over twitterers
					for (Iterator iterator2 = twitterUserList.iterator(); iterator2.hasNext();) {
						TwitterUser twitterUser = (TwitterUser) iterator2.next();
						
						//Ensure that this twitterer was not tweeted to in the past 10 minutes
						//because we don't want to bombard the twitterer with tweets
						int minutes = 10;
						boolean twittererdToOrNot = campaignService.wasAnyTweetSentToTwittererInLastNMinutesForCampaign(twitterUser.getTwitterId(), minutes,  tweetsSentToTwittererList);
						if (twittererdToOrNot == false){
							//This 
							TATweet clone = (TATweet)tweet.clone();
							clone.directTo(twitterUser, true);
							try {
								TweetsSent ts = new TweetsSent();
								if (campaignService.getDashboard().isSendTweetTest()){
									ts.setTestOnly(true);
									log.debug("TEST: Sending: " + clone.getDescription());
								} else {
									//Actually send the tweet
									//twitter.updateStatus(tweet.getDescription());
									if (false) throw new TwitterException("test");
									ts.setTestOnly(false);
									log.debug("Sending: " + clone.getDescription());
								}
								
								
								ts.setCampaignId(campaign.getId());
								ts.setTargetTwitterId(twitterUser.getTwitterId());
								ts.setTweetId(clone.getId());
								ts.setSentOn(new Date());
								
								
								twittererCount++;
								
								tweetsSentList.add(ts);
							} catch (TwitterException te){
								log.warn("Exception tweeting " + tweet.getDescription() + ". Moving on...", te);
							}
						}else {
							log.debug("This twitterer was already tweeted to in the past " + minutes + " minutes. Skipping...");
						}
					}
					tweetCount++;
					campaignService.storeTweetsSent(tweetsSentList);
				} else {
					log.debug("This tweet (" + tweet.getDescription() + ") was sent less than " + hours + " hours ago. Skipping...");
				}
			}
			log.info("TweetSendStats: Sent :" + tweetCount + " tweets to " + twittererCount + " twitterers." );
		} else {
			throw new NDRuntimeException("This campaign " + campaignName + " does not exist!");
		}
	}


	public void flushSession() {
		this.getTwitterDAO().flushSession();
	}
	


}
