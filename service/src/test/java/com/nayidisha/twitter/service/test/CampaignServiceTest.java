package com.nayidisha.twitter.service.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



import com.nayidisha.common.CampaignNameNotUniqueException;
import com.nayidisha.testing.TestingConstants;
import com.nayidisha.testing.TwitterTestExecutionListener;
import com.nayidisha.twitter.domain.Campaign;
import com.nayidisha.twitter.domain.TATweet;
import com.nayidisha.twitter.domain.TAUser;
import com.nayidisha.twitter.domain.TwitterUser;
import com.nayidisha.twitter.service.CampaignService;


@RunWith(SpringJUnit4ClassRunner.class)

@TestExecutionListeners(TwitterTestExecutionListener.class)

public class CampaignServiceTest  extends AbstractTwitterTests {
	
	@Resource
	private CampaignService campaignService;

	@Test
	public void addANewUser() {	
    	TAUser user = createNewUser();
    	//change the id
    	user.setId(TestingConstants.TEST_USER2);
    	campaignService.addUser(user);
		TAUser reterivedUser = campaignService.getUser(user.getId());
		Assert.assertNotNull(reterivedUser);
	}	
	
	@Test
	public void upateUser() {	
    	TAUser user = createNewUser();
    	user.setEmail("junk");
    	campaignService.addUser(user);
		TAUser reterivedUser = campaignService.getUser(user.getId());
		Assert.assertEquals("junk", reterivedUser.getEmail());
	}	
	
	@Test
	public void addANewCampaignThatDoesntExist() {	
    	Campaign campaign = createNewCampaignWithName(TestingConstants.TEST_CAMPAIGN);
    	
		campaignService.addCampaign(campaign);
		Campaign camp = campaignService.getUniqueCampaignByName(TestingConstants.TEST_CAMPAIGN);
		Assert.assertNotNull(camp);
	}	
	
	@Test
	public void addANewCampaignThatExists() {	
    	Campaign campaign = createNewCampaignWithName(TestingConstants.TEST_CAMPAIGN);
    	
		campaignService.addCampaign(campaign);
		Campaign camp = campaignService.getUniqueCampaignByName(TestingConstants.TEST_CAMPAIGN);
		boolean works = false;
		try {
			campaignService.addCampaign(camp);
		} catch (CampaignNameNotUniqueException e){
			System.out.println(e.getMessage());
			works = true;
		}
		
		Assert.assertTrue(works);
		
	}	
	@Test
	public void loadExistingCampaignsByName() {	
		Campaign campaignA = createNewCampaignWithName("A");
		campaignService.addCampaign(campaignA);
		
		Collection<Campaign> campaignList = campaignService.getCampaignsByName("A");
		Assert.assertEquals(1, campaignList.size());
	}		
	
	@Test
	public void updateAnExistingCampaignPlainAttribute() {		
    	Campaign campaign = createNewCampaignWithName(TestingConstants.TEST_CAMPAIGN);
    	
		campaignService.addCampaign(campaign);
		
		//Change an attrib and upadte
		campaign.setProfileKeywords("dummy");
		campaignService.updateCampaign(campaign);
		
		//retrieve
		Campaign camp = campaignService.getUniqueCampaignByName(TestingConstants.TEST_CAMPAIGN);
		
		Assert.assertEquals("Update of attrib not successful" , "dummy", camp.getProfileKeywords());
		
	}		
	
	
	@Test
	public void updateAnExistingCampaignAssociatedAttribute() {		
    	Campaign campaign = createNewCampaignWithName(TestingConstants.TEST_CAMPAIGN);
    	
		campaignService.addCampaign(campaign);
		
		//Change an associative attrib and upadte
		TAUser u = new TAUser();
		u.setId("un");
		campaign.setOwner(u);
		campaignService.updateCampaign(campaign);
		
		//retrieve
		Campaign camp = campaignService.getUniqueCampaignByName(TestingConstants.TEST_CAMPAIGN);
		Assert.assertEquals("Update of attrib not successful" , "un", camp.getOwner().getId());
	}		
	
	@Test
	public void checkCampaignsByUserId() {		
		
		//First delete all campaigns by this user
		campaignService.deleteCampaignsByUser(TestingConstants.TEST_USER, true);
	
		
		//Add a campaign
    	Campaign campaign = createNewCampaignWithName(TestingConstants.TEST_CAMPAIGN);
		campaignService.addCampaign(campaign);
		
		//Add another
    	Campaign campaign2 = createNewCampaignWithName(TestingConstants.TEST_CAMPAIGN2);
		campaignService.addCampaign(campaign2);
		
		//Add a third
    	Campaign campaign3 = createNewCampaignWithName(TestingConstants.TEST_CAMPAIGN3);
		campaignService.addCampaign(campaign3);
		
		
		Collection <Campaign> c1 = campaignService.loadCampaignsByUser(TestingConstants.TEST_USER);
		
		Assert.assertEquals(3, c1.size());
	}
	

	
	@Test
	public void addOneTweetForCampaign() {	
		//First add a campaign
		Campaign campaign = createNewCampaignWithName(TestingConstants.TEST_CAMPAIGN);
		campaignService.addCampaign(campaign);
		
		TATweet taTweet = createNewTweet();

		TATweet tw = campaignService.addTweet(taTweet, TestingConstants.TEST_CAMPAIGN);
		
		Assert.assertNotSame(new Long(0), tw.getId());
	}		

	@Test
	public void deleteCampaignWithoutDeletingRelatedTweets() {	
		//First add a campaign
		Campaign campaign = createNewCampaignWithName(TestingConstants.TEST_CAMPAIGN);
		campaignService.addCampaign(campaign);
		
		TATweet taTweet1 = createNewTweet();
		TATweet taTweet2 = createNewTweet();

		TATweet tw1 = campaignService.addTweet(taTweet1, TestingConstants.TEST_CAMPAIGN);
		TATweet tw2 = campaignService.addTweet(taTweet2, TestingConstants.TEST_CAMPAIGN);
		


		campaignService.deleteCampaignButNotRelatedTweets(campaign.getId());
		
		TATweet rt1 = campaignService.getTweet(tw1.getId());
		TATweet rt2 = campaignService.getTweet(tw2.getId());
		Assert.assertNotNull(rt1);
		Assert.assertNotNull(rt2);
	}	
	
	@Test
	public void deleteCampaignAndDeleteRelatedTweets() {	
		//First add a campaign
		Campaign campaign = createNewCampaignWithName(TestingConstants.TEST_CAMPAIGN);
		campaignService.addCampaign(campaign);
		
		TATweet taTweet1 = createNewTweet();
		TATweet taTweet2 = createNewTweet();

		TATweet tw1 = campaignService.addTweet(taTweet1, TestingConstants.TEST_CAMPAIGN);
		TATweet tw2 = campaignService.addTweet(taTweet2, TestingConstants.TEST_CAMPAIGN);
		


		campaignService.deleteCampaignAndRelatedTweets(campaign.getId());
		
		TATweet rt1 = campaignService.getTweet(tw1.getId());
		TATweet rt2 = campaignService.getTweet(tw2.getId());
		Assert.assertNull(rt1);
		Assert.assertNull(rt2);
	}	
	
	
	@Test
	public void deleteOneTweetRelatedToACampaign() {	
		//First add a campaign
		Campaign campaign = createNewCampaignWithName(TestingConstants.TEST_CAMPAIGN);
		campaignService.addCampaign(campaign);
		
		TATweet taTweet1 = createNewTweet();
		TATweet taTweet2 = createNewTweet();

		TATweet tw1 = campaignService.addTweet(taTweet1, TestingConstants.TEST_CAMPAIGN);
		TATweet tw2 = campaignService.addTweet(taTweet2, TestingConstants.TEST_CAMPAIGN);
		
		campaignService.deleteTweet(tw1.getId());
		
		TATweet rt1 = campaignService.getTweet(tw1.getId());
		TATweet rt2 = campaignService.getTweet(tw2.getId());
		Assert.assertNull(rt1);
		Assert.assertNotNull(rt2);
		
	}
	
	
	@Test
	public void addTweetNotRelatedToACampaign() {
		TATweet taTweet1 = createNewTweet();
		TATweet t = campaignService.addTweet(taTweet1);
		Assert.assertNotNull(t);
	}
	
	@Test
	public void updateTweetFromNoCampaignToSomeCampaign() {	
		//First add a campaign
		Campaign campaign1 = createNewCampaignWithName(TestingConstants.TEST_CAMPAIGN);
		campaignService.addCampaign(campaign1);

		TATweet taTweet1 = createNewTweet();
		
		TATweet tweetWithNoStringsAttached = campaignService.addTweet(taTweet1);
		
		Assert.assertEquals(0, tweetWithNoStringsAttached.getAssociatedCampaigns().size());

		
		taTweet1.addCampaignToTweet(campaign1);
		
		campaignService.updateTweet(taTweet1);
		
		//Re-retrieve
		TATweet reretrievedTweet = campaignService.getTweet(taTweet1.getId());
		
		Assert.assertEquals(1, reretrievedTweet.getAssociatedCampaigns().size());
		
	}
	

	@Test
	public void updateTweetFromSomeCampaignToNoCampaign() {	
		
		//First add a campaign
		Campaign campaign1 = createNewCampaignWithName(TestingConstants.TEST_CAMPAIGN);
		campaignService.addCampaign(campaign1);

		TATweet taTweet1 = createNewTweet();
		TATweet tweetWithStringsAttached = campaignService.addTweet(taTweet1, campaign1);
		
		TATweet reRetrievedTweet = campaignService.getTweet(tweetWithStringsAttached.getId());
		//At this time, hibernate returns the exact same object as was persisted.
		//So taTweet1 and reRetrievedTweet and tweetWithStringsAttached are the same object (same address)
		Assert.assertEquals(1, reRetrievedTweet.getAssociatedCampaigns().size());

		//remove the association
		taTweet1.removeAllAssociatedCampaigns();
		
		campaignService.updateTweet(taTweet1);
		
		//re-re-retrieve
		TATweet reReRetrievedTweet = campaignService.getTweet(taTweet1.getId());
		
		Assert.assertEquals(0, reReRetrievedTweet.getAssociatedCampaigns().size());			
	}
	
	@Test
	public void updateTweetFromCampaignAToCampaignB() {	
		
		//First add a campaign
		Campaign campaign1 = createNewCampaignWithName(TestingConstants.TEST_CAMPAIGN);
		campaignService.addCampaign(campaign1);
		//First add another campaign
		Campaign campaign2 = createNewCampaignWithName(TestingConstants.TEST_CAMPAIGN2);
		campaignService.addCampaign(campaign2);
		
		TATweet taTweet1 = createNewTweet();
		TATweet tweetWithStringsAttached1 = campaignService.addTweet(taTweet1, campaign1);
		
		
		TATweet reRetrievedTweet = campaignService.getTweet(tweetWithStringsAttached1.getId());
		//At this time, hibernate returns the exact same object as was persisted.
		//So taTweet1 and reRetrievedTweet and tweetWithStringsAttached are the same object (same address)
		Campaign retrievedCampaign = reRetrievedTweet.getFirstCampaign();
		
		Assert.assertEquals(TestingConstants.TEST_CAMPAIGN, retrievedCampaign.getName());

		taTweet1.removeAssociatedCampaign(campaign1);
		taTweet1.addCampaignToTweet(campaign2);
		
		campaignService.updateTweet(taTweet1);
		
		//re-re-retrieve
		TATweet reReRetrievedTweet = campaignService.getTweet(taTweet1.getId());
		Campaign retrievedCampaign2 = reReRetrievedTweet.getFirstCampaign();
		Assert.assertEquals(TestingConstants.TEST_CAMPAIGN2, retrievedCampaign2.getName());
		
	}
	
	@Test
	public void updateTweetValuesForNoCampaign() {	
		

		TATweet taTweet1 = createNewTweet();
		taTweet1.setDescription("aDesc");
		TATweet tweetWithNoStringsAttached = campaignService.addTweet(taTweet1);
		
		TATweet reRetrievedTweet = campaignService.getTweet(tweetWithNoStringsAttached.getId());
		Assert.assertEquals(0, reRetrievedTweet.getAssociatedCampaigns().size());

		taTweet1.setDescription("aNewDesc");
		
		campaignService.updateTweet(taTweet1);
		
		//re-re-retrieve
		TATweet reReRetrievedTweet = campaignService.getTweet(taTweet1.getId());
		
		Assert.assertEquals("aNewDesc", reReRetrievedTweet.getDescription());			
	}
	
	@Test
	public void updateTweetValuesForACampaign() {	
		
		//First add a campaign
		Campaign campaign1 = createNewCampaignWithName(TestingConstants.TEST_CAMPAIGN);
		campaignService.addCampaign(campaign1);

		TATweet taTweet1 = createNewTweet();
		taTweet1.setDescription("aDesc");
		TATweet tweetWithStringsAttached = campaignService.addTweet(taTweet1, campaign1);
		
		TATweet reRetrievedTweet = campaignService.getTweet(tweetWithStringsAttached.getId());
		//At this time, hibernate returns the exact same object as was persisted.
		//So taTweet1 and reRetrievedTweet and tweetWithStringsAttached are the same object (same address)
		Assert.assertEquals(1, reRetrievedTweet.getAssociatedCampaigns().size());

		taTweet1.setDescription("aNewDesc");
		Date wayInThePast = null;
		try {
			wayInThePast = new SimpleDateFormat("mm/dd/yyyy").parse("01/01/2001");
		} catch (Exception e){
			//shd never happen
		}
		taTweet1.setLastSentDate(wayInThePast);
		
		campaignService.updateTweet(taTweet1);
		
		//re-re-retrieve
		TATweet reReRetrievedTweet = campaignService.getTweet(taTweet1.getId());
		
		Assert.assertEquals("aNewDesc", reReRetrievedTweet.getDescription());	
		Assert.assertEquals(wayInThePast, reReRetrievedTweet.getLastSentDate());	
	}
	
	
	
	@Test
	public void getTweetsForACampaign() {	
		//First add a campaign
		Campaign campaign1 = createNewCampaignWithName(TestingConstants.TEST_CAMPAIGN);
		campaignService.addCampaign(campaign1);

		TATweet taTweet1 = createNewTweet();
		TATweet taTweet2 = createNewTweet();
		
		TATweet t1 = campaignService.addTweet(taTweet1, campaign1);
		TATweet t2 = campaignService.addTweet(taTweet2, campaign1);
		
		Collection<TATweet> tweetList = campaignService.getTweetsForACampaign(campaign1);
		
		Assert.assertEquals(2, tweetList.size());
		
	}
	
	
	@Test
	public void getTweetsForAUser() {	
		//Delete all existing tweets by this user
		Collection<TATweet> existingTweetList = campaignService.getTweetsForAUser(TestingConstants.TEST_USER);
		for (Iterator iterator = existingTweetList.iterator(); iterator
				.hasNext();) {
			TATweet tweet = (TATweet) iterator.next();
			campaignService.deleteTweet(tweet.getId());
		}

		TATweet taTweet1 = createNewTweet();
		TATweet taTweet2 = createNewTweet();
		
		//Create  tweets 
		TATweet t1 = campaignService.addTweet(taTweet1);
		TATweet t2 = campaignService.addTweet(taTweet2);
		
		Collection<TATweet> tweetList = campaignService.getTweetsForAUser(TestingConstants.TEST_USER);
		
		Assert.assertEquals(2, tweetList.size());
		
	}
	
	@Test
	public void getCampaignsForAUser() {	
		

		//Then add a campaign
		Campaign campaign1 = createNewCampaignWithName(TestingConstants.TEST_CAMPAIGN);
		campaignService.addCampaign(campaign1);

		Campaign campaign2 = createNewCampaignWithName(TestingConstants.TEST_CAMPAIGN2);
		campaignService.addCampaign(campaign2);
		
		
		Collection<Campaign> campaignList = campaignService.getCampaignsForAUser(TestingConstants.TEST_USER);
		
		Assert.assertEquals(2, campaignList.size());
		
	}
	
	
	@Test
	public void testSaveOrUpdateForTwitterersForCampaign() {
		
		//First add a campaign
		Campaign campaign1 = createNewCampaignWithName(TestingConstants.TEST_CAMPAIGN);
		campaignService.addCampaign(campaign1);
		ArrayList<TwitterUser> tuList = new ArrayList<TwitterUser>();
		TwitterUser tu1 = new TwitterUser();
		tu1.setCampaignId(campaign1.getId());
		tu1.setTwitterId("pankajtandon");
		tu1.setProfileDescription("yak yak yak1");
		//Hack:
		tu1.setCreatedDate(new Date());
		tu1.setLastSent(new Date());
		tu1.setOnHold(true);
		
		TwitterUser tu2 = new TwitterUser();
		tu2.setCampaignId(campaign1.getId());
		tu2.setTwitterId("nayidisha");
		tu2.setProfileDescription("nak nak nak12");
		tu2.setCreatedDate(new Date());
		tu2.setLastSent(new Date());
		tu2.setOnHold(true);
		
		TwitterUser tu3 = new TwitterUser();
		tu3.setCampaignId(campaign1.getId());
		tu3.setTwitterId("nayidishax");
		tu3.setProfileDescription("jak jak jak1");
		tu3.setCreatedDate(new Date());
		tu3.setLastSent(new Date());
		tu3.setOnHold(true);
		
		TwitterUser tu4 = new TwitterUser();
		tu4.setCampaignId(campaign1.getId());
		tu4.setTwitterId("nayidishay");
		tu4.setProfileDescription("tak tak tak1");
		tu4.setCreatedDate(new Date());
		tu4.setLastSent(new Date());
		tu4.setOnHold(true);
		
		tuList.add(tu1);
		tuList.add(tu2);
		tuList.add(tu3);
		tuList.add(tu4);

		campaignService.storeTargetTwitterers(tuList, campaign1);

		Assert.assertEquals(4, campaignService.getTwitterersForACampaign(campaignService.getUniqueCampaignByName(TestingConstants.TEST_CAMPAIGN).getId()).size());
	}
	

	//Introduce tests for deleteCampaignButNotRelatedTweets... etc.

	//---------------- P R I V A T E -------------------------
	private Campaign createNewCampaignWithName(String name){
		//First add a campaign
    	Campaign campaign = new Campaign();
    	campaign.setName(name);
    	campaign.setEnabled(true);
    	//campaign.setFindUserFreq("onceADay");
    	campaign.setProfileKeywords("some profile kw");
    	campaign.setSourceTwitterId("twId");
    	//campaign.setSourceTwitterIdPassword("twpasswdxxx");
    	//campaign.setTweetFreq("tweet Freq");
    	campaign.setTweetKeywords("tw kw");
    	return campaign;
	}
	
	private TATweet createNewTweet(){

		TATweet taTweet = new TATweet();
		taTweet.setDescription("A Tweet");
		taTweet.setFromUser("fromUser");
		taTweet.setToUser("toUser");
		taTweet.setWeight(new Long(50));
		return taTweet;
	}
	
	private TAUser createNewUser(){
		TAUser user = new TAUser();
		user.setId(TestingConstants.TEST_USER); //userName
		user.setEmail("my@email.com");
		user.setFirstName("firstName");
		user.setLastName("lastName");
		return user;
	}
	

}
