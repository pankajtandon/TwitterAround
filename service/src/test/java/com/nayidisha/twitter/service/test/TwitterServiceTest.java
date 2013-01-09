package com.nayidisha.twitter.service.test;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import twitter4j.Status;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

import com.nayidisha.testing.TwitterTestExecutionListener;
import com.nayidisha.twitter.domain.Campaign;
import com.nayidisha.twitter.domain.TATweet;
import com.nayidisha.twitter.domain.TAUser;
import com.nayidisha.twitter.domain.TweetsSent;
import com.nayidisha.twitter.domain.TwitterUser;
import com.nayidisha.twitter.service.CampaignService;
import com.nayidisha.twitter.service.TwitterService;
 
@RunWith(SpringJUnit4ClassRunner.class) 
@TestExecutionListeners(TwitterTestExecutionListener.class)
public class TwitterServiceTest extends AbstractTwitterTests {

	
	private static final Logger log = Logger.getLogger(TwitterServiceTest.class.getName());
	
	@Resource 
	private TwitterService twitterService;
	
	@Resource
	private CampaignService campaignService;
	
/*	@Before
	public void setUp(){
		twitterService = (TwitterService)twitterContext.getBean("twitterService");
		
		campaignService = (CampaignService)twitterContext.getBean("campaignService");
		//First delete and then create a user
		campaignService.deleteUser("testUser");
		User u = createNewUser();
		u.setUpdatedBy("un"); //bootstrap
		campaignService.addUser(u);
		NDUtils.setUpSecurityContext("testUser", "password", "ROLE_USER");		
	}*/
	
	//@Test 
	public void testOAuth() throws Exception  { 
	    ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setHttpProxyHost("10.98.20.23");
	    cb.setHttpProxyPort(8080);
	    //The factory instance is re-useable and thread safe.
	    TwitterFactory factory = new TwitterFactory(cb.build());  
	    Twitter twitter = factory.getInstance();
	    
	    twitter.setOAuthConsumer("jjYtUv0Cs7wm1jcMqtRKg", "pOymIxDm4R7znZcWKV6w9wXWt0if85HnqqAcUjR64"); //ap
	    //twitter.setOAuthConsumer("MBHMKwx8mukUJYxEKqtfCw", "ssfIPlVp27WE9MTWEYBwCgGOWDmD36DakFatQkgLg"); //pt
	    RequestToken requestToken = twitter.getOAuthRequestToken();
	    AccessToken accessToken = null;
	    //accessToken = new AccessToken("29079806-oGTXtiNwbVmvSz0FK5g4qK1sOCrSrHq1UjWYHA", "pXXv3h9XeZWrxANo1OWGdrS3o8bSKzdfROxwxIcqE"); //pt
	    //accessToken = new AccessToken("29080837-ayrSk8bp2K6VZm5Kg1VPpOklJq2ZBRp4XryemQTc", "NBMa9aTWsirjXZcFLm2HQyZNqWSjaSjXnfOSjIktWlM"); //ap
	    while (null == accessToken) {
	      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	      System.out.println("Open the following URL and grant access to your account:");
	      System.out.println(requestToken.getAuthorizationURL());
	      System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
	      String pin = br.readLine();
	      try{
	         if(pin.length() > 0){
	           accessToken = twitter.getOAuthAccessToken(requestToken, pin);
	         }else{
	           accessToken = twitter.getOAuthAccessToken();
	         }
	      } catch (TwitterException te) {
	        if(401 == te.getStatusCode()){
	          System.out.println("Unable to get the access token.");
	        }else{
	          te.printStackTrace();
	        }
	      }
	    }
	    
	    //persist to the accessToken for future reference.
	    //storeAccessToken(twitter.verifyCredentials().getId() , accessToken);
	    //factory.getOAuthAuthorizedInstance("MBHMKwx8mukUJYxEKqtfCw", "ssfIPlVp27WE9MTWEYBwCgGOWDmD36DakFatQkgLg", accessToken);
	    twitter.setOAuthAccessToken(accessToken);
	    Status status = twitter.updateStatus("test123");
	    System.out.println("Successfully updated the status to [" + status.getText() + "].");
	}
	
/*	@Test
	public void retrieveDefaultUserTest() {
		TwitterUser tu = twitterService.getDefaultTwitterUser();
		System.out.println("Default TU: " + tu.toString());
		Assert.assertNotNull(tu.getId());
	}*/

	@Test
	public void retrieveUserTest() { 
		TwitterUser tu = twitterService.getTwitterUser("aroundpoint");
		Assert.assertEquals("Names dont match", tu.getTwitterId(), "aroundpoint");
	}
	@Test
	public void retrieveNonExistentUserTest() {
		TwitterUser tu = twitterService.getTwitterUser("RealEstatePlus123ThatBetterNotBeThere");
		Assert.assertNull(tu);
	}	
	 
	@Test 
	public void searchTest() {
		//Likely that someone will be talking about twitter!
		List<Tweet> tweetList = twitterService.searchTweets("twitter");
		log.debug("List of Tweets using searchWord:" + tweetList.toString() );
		Assert.assertNotNull(tweetList);
	}
	
	
	/**
	 * This test is going to pass ONLY if there are some twitterers tweeting about Realtors at the time of 
	 * running the test.
	 */
	@Test
	public void testStoreNonRepeatingTwitterersForACampaign() {
		String campaignName = "krishna";
		
		//First see how many twitters exist for a campaign
		Campaign c = campaignService.getUniqueCampaignByName(campaignName);
		Collection<TwitterUser> existingTwitterUsers = campaignService.getTwitterersForACampaign(c.getId());
		
		//Likely that someone will be talking about "Realtor" 
		twitterService.storeNonRepeatingTwitterersForACampaign(campaignName); 
		
		//Query again and see if we got someone who was not already there
		Collection<TwitterUser> existingTwitterUsersAgain = campaignService.getTwitterersForACampaign(c.getId());
		
		int diff = (existingTwitterUsersAgain.size() - existingTwitterUsers.size());
		log.debug("List of NEW Users using searchWord:" + c.getTweetKeywords() + " is: " +  diff);
		
		Assert.assertTrue(diff > 0);
	}


	//Before running this ensure that dashboard.send_tweet_test is sent to 1 (to prevent tweets from being sent out)
	@Test
	public void testSendEarliestTweetToEarliestTwitterer() {
		//Get campaign
		Campaign c = campaignService.getUniqueCampaignByName("krishna");
		
		//First determine eariestTweet
		TATweet earliestTweet = campaignService.getEarliestTweetForCampaign(c);
		//...and earliest Twitterer
		TwitterUser earliestTwitterUser = campaignService.getEarliestSentTwitterUserForCampaign(c);
		//..and if the TWEETS_SENT table is updated
		Collection<TweetsSent> tweetsSentBeforeList = campaignService.getSentToTwitterersForACampaign(c.getId());
		
		int tweetsSentBefore = tweetsSentBeforeList==null?0:tweetsSentBeforeList.size();
		
		
		//call to test
		twitterService.sendEarliestTweetToEarliestTwitterer("krishna");
		
		
		//In an ORM, this ensures that commits have happened before the db is queried again
		twitterService.flushSession();
		
		//get the modified state
		TATweet latestTweet = campaignService.getLatestTweetForCampaign(c);
		TwitterUser latestTwitterUser = campaignService.getLatestSentTwitterUserForCampaign(c);
		Collection<TweetsSent> tweetsSentAfterList = campaignService.getSentToTwitterersForACampaign(c.getId());
		int tweetsSentAfter = tweetsSentAfterList==null?0:tweetsSentAfterList.size();
		
		//Asserts
		Assert.assertEquals(latestTweet.getId(), earliestTweet.getId());
		Assert.assertEquals(latestTwitterUser.getId(), earliestTwitterUser.getId());
		Assert.assertEquals(1, (tweetsSentAfter - tweetsSentBefore));
		
	}
	
	//TODO: Jan 12-2011 Build and fix this test
	//@Test 
	public void followTwitterer() {
		twitterService.followTwitterers("krishna");
	}
	
	private TAUser createNewUser(){
		TAUser user = new TAUser();
		user.setId("testUser"); //userName
		user.setEmail("my@email.com");
		user.setFirstName("firstName"); 
		user.setLastName("lastName");
		return user;
	}



}
