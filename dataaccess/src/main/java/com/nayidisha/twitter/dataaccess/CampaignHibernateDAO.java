package com.nayidisha.twitter.dataaccess;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.nayidisha.common.CampaignNameNotUniqueException;
import com.nayidisha.common.NDRuntimeException;
import com.nayidisha.twitter.domain.Campaign;
import com.nayidisha.twitter.domain.Dashboard;
import com.nayidisha.twitter.domain.TATweet;
import com.nayidisha.twitter.domain.TAUser;
import com.nayidisha.twitter.domain.TweetsSent;
import com.nayidisha.twitter.domain.TwitterFollower;
import com.nayidisha.twitter.domain.TwitterUser;
import com.nayidisha.utils.NDUtils;

public class CampaignHibernateDAO extends HibernateDaoSupport implements CampaignDAO{ 

	private Logger log = Logger.getLogger(CampaignHibernateDAO.class.getName());
	
	public void addCampaign(Campaign campaign) {
		
        try {
        	
        	TAUser user = (TAUser)getSession(false).load(TAUser.class, NDUtils.getCurrentUserId());
        	
        	user.addCampaignToUser(campaign);
        	getSession(false).save(user);
        	getSession(false).save(campaign);
        }
        catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
	}
	

	public void updateCampaign(Campaign c){
		
		
		Campaign existingCampaign = this.getCampaignById(c.getId());
		//store the id
		Long id = existingCampaign.getId();
		TAUser owner = null;
		if (c.getOwner() != null){
			owner = c.getOwner();
		} else {
			owner = existingCampaign.getOwner();
		}
		try{
			BeanUtils.copyProperties(existingCampaign, c);
		} catch (Exception e){
			throw new NDRuntimeException("Could not clone campaign ", e);
		}
		//restore the id
		existingCampaign.setId(id);
		existingCampaign.setOwner(owner);
		
		//getSession(false).update(c);
	}
	
	public Collection<Campaign> getCampaignsByName(String name){
        Query query = getSession(false).createQuery("from Campaign campaign where campaign.name = ?");
        query.setString(0, name);
        Collection<Campaign> campaignList = query.list();
		return campaignList;
		
	}
	public Campaign getCampaignById(Long campaignId) {
		return (Campaign)this.getSession(false).load(Campaign.class, campaignId);
	}
	public Campaign getUniqueCampaignByName(String name) throws CampaignNameNotUniqueException{
        Query query = getSession(false).createQuery("from Campaign campaign where campaign.name = ?");
        query.setString(0, name);
        List<Campaign> campaignList = query.list();
        if (campaignList != null && campaignList.size() > 1){
        	throw new CampaignNameNotUniqueException("There are " + campaignList.size() + " campaigns with name " + name);
        }
		return campaignList.get(0);
	}
	
	public void deleteCampaignButNotRelatedTweets(Long campaignId){
		Campaign c  = this.getCampaignById(campaignId);
		deleteCampaign(c);
	}
	
	public void deleteCampaignAndRelatedTweets(Long campaignId){
		Campaign c  = this.getCampaignById(campaignId);
		Set<TATweet> associatedTweets = c.getAssociatedTweets();
		for (Iterator iterator = associatedTweets.iterator(); iterator
				.hasNext();) {
			TATweet tweet = (TATweet) iterator.next();
			TATweet tbd = this.getTweet(tweet.getId());
			this.getSession(false).delete(tbd);
		}
		deleteCampaign(c);
	}
	
	public void deleteCampaign(Campaign c){
		this.getSession(false).delete(c);
	}
	public void deleteTweet(TATweet tweet){
		this.deleteTweet(tweet.getId());
	}
	public void deleteTweet(Long tweetId){
		TATweet t = this.getTweet(tweetId);
		if (t != null){
			Set<Campaign> associatedCampaigns = t.getAssociatedCampaigns();
			for (Iterator iterator = associatedCampaigns.iterator(); iterator
					.hasNext();) {
				Campaign campaign = (Campaign) iterator.next();
				campaign.removeTweet(t);
			}
			t.removeAllAssociatedCampaigns();
		
			this.getSession(false).delete(t);
		}
	}
	
	public TATweet getTweet(Long id) {
		return (TATweet)this.getSession(false).get(TATweet.class, id);
	}
	public Collection<Campaign> loadCampaignsByUser(String userId) {
        Query query = getSession(false).createQuery("from com.nayidisha.twitter.domain.Campaign campaign where campaign.owner.id = ?");
        query.setString(0, userId);
        Collection<Campaign> campaignList = query.list();
        Collections.sort((List)campaignList); //sort arbitrarily so that it is deterministic
        return campaignList;
	}
	
	public TwitterFollower addFollower(TwitterFollower twiterFollower){
		this.getSession(false).save(twiterFollower);
		return twiterFollower;
	}
	
	public TATweet addTweet(TATweet t){
		this.getSession(false).save(t);
		return t;
	}
	public TATweet addTweet(TATweet t, Campaign campaign){
		
		Campaign retrievedCampaign  = this.getCampaignById(campaign.getId());
		retrievedCampaign.addTweetToCampaign(t);
		t.addCampaignToTweet(retrievedCampaign);	
		this.getSession(false).save(t);
		return t;
	}

	public TATweet addTweet(TATweet t, String campaignName){
		TATweet tat = null;
		Collection<Campaign> campaignList = this.getCampaignsByName(campaignName);
		for (Iterator iterator = campaignList.iterator(); iterator.hasNext();) {
			Campaign campaign = (Campaign) iterator.next();
			tat = this.addTweet(t, campaign);
		}
		return tat;
	}
	public TATweet updateTweet(TATweet taTweet){
		this.getSession(false).merge(taTweet);
		return taTweet;		
	}
	
	public Collection<TATweet> getTweetsForACampaign(Campaign campaign){
		
		Campaign camp = getCampaignById(campaign.getId());
		Collection<TATweet> tweetList = camp.getAssociatedTweets(); 
		return tweetList;
	}
	
	
	public TAUser getUser(String userId) {
		return (TAUser)this.getSession(false).get(TAUser.class, userId);
	}
	public TAUser addUser(TAUser user) {
		this.getSession(false).save(user);
		return user;
	}
	public void updateUser(TAUser user) {
		this.getSession(false).update(user);
	}
	
	
	
	public void deleteUser(String userId) {
		TAUser user = getUser(userId);
		if (user != null){
			this.getSession(false).delete(user);
		}
	}


	public Collection<TATweet> getTweetsForAUser(String userId) {
		Query query =this.getSession(false).createQuery("from com.nayidisha.twitter.domain.TATweet tweet where tweet.createdBy = ?");
        query.setString(0, userId);
        Collection<TATweet> tweetList = query.list();
        return tweetList;		
	}
	
	public Collection<TwitterUser> getTwitterersForACampaign(Long campaignId) {
		Query query =this.getSession(false).createQuery("from com.nayidisha.twitter.domain.TwitterUser tu where tu.campaignId = ?");
        query.setLong(0, campaignId);
        Collection<TwitterUser> tuList = query.list();
        return tuList;		
	}

	public TwitterUser getTwitterer(Long campaignId, String twitterId) {
		Query query =this.getSession(false).createQuery( " from com.nayidisha.twitter.domain.TwitterUser tu " +
				                                         " where tu.campaignId = ? " +
				                                         " and tu.twitterId = ?"
				);
        query.setLong(0, campaignId);
        query.setString(1, twitterId);
        Collection<TwitterUser> tuList = query.list();
        //tulist should be 1 long
        TwitterUser tu = null;
        for (Iterator iterator = tuList.iterator(); iterator.hasNext();) {
			TwitterUser twitterUser = (TwitterUser) iterator.next();
			tu = twitterUser;
			break;
		}
		return tu;
	}


	public TwitterUser addTwitterer(TwitterUser twitterUser) {
		this.getSession(false).saveOrUpdate(twitterUser);
		return twitterUser;
	}


	public TwitterUser updateTwitterer(TwitterUser twitterUser) {
		this.getSession(false).update(twitterUser);
		return twitterUser;
	}
	
	public void saveOrUpdateTweetsSent(TweetsSent tweetsSent){
		this.getSession(false).saveOrUpdate(tweetsSent);
		
	}
	public Collection<TweetsSent> getSentTweetForCampaign(Long tweetId, Long campaignId){
		Query query =this.getSession(false).createQuery("from com.nayidisha.twitter.domain.TweetsSent ts where ts.campaignId = ? and ts.tweetId = ? ");
        query.setLong(0, campaignId);
        query.setLong(1, tweetId);
        Collection<TweetsSent> tsList = query.list();
        return tsList;
	}
	public TATweet getEarliestTweetForCampaign(Long campaignId){

		
		String sql = " SELECT tw.id as id, tw.description as description, tw.from_user as from_user, tw.to_user as to_user, tw.last_sent as last_sent " +
			         " FROM ta_tweet tw " +
			         " LEFT OUTER JOIN tweet_campaign tc ON tw.id = tc.tweet_id " +
			         " WHERE tc.campaign_id = ? " +
			         " AND tw.last_sent = " +
			         " (SELECT  min(tw.last_sent) " +
			         " FROM ta_tweet tw " +
			         " LEFT OUTER JOIN tweet_campaign tc ON tw.id = tc.tweet_id " +
			         " WHERE tc.campaign_id = ? ) " +
			         " limit 1 ";


		Session session = this.getSession(false);
		log.debug("Session :" + session.toString());
		Query query = session.createSQLQuery(sql);
		
		//Query query =this.getSession(false).createQuery(q);
		log.debug("CampaignId: " + campaignId);
        query.setLong(0, campaignId);
        query.setLong(1, campaignId);
        query.setFlushMode(FlushMode.COMMIT);
        
        ArrayList<Object[]> al = (ArrayList<Object[]>)query.list();
//        Object[] o = (Object[])query.uniqueResult();
        Object[] o = al.get(0); //Shd get only one
        log.debug("Object returned " + (o==null?"NULL":o.toString()));
        TATweet tw = new TATweet();
        BigInteger bi = (BigInteger)o[0];
        tw.setId(new Long(bi.longValue()));
        tw.setDescription((String)o[1]);
        tw.setFromUser((String)o[2]);
        tw.setToUser((String)o[3]);
        tw.setLastSentDate((Date)o[4]);
        
        return tw;
	}
	
	public TATweet getLatestTweetForCampaign(Long campaignId){

		String sql = " SELECT tw.id as id, tw.description as description " +
			         " FROM ta_tweet tw " +
			         " LEFT OUTER JOIN tweet_campaign tc ON tw.id = tc.tweet_id " +
			         " WHERE tc.campaign_id = ? " +
			         " AND tw.last_sent = " +
			         " (SELECT  max(tw.last_sent) " +
			         " FROM ta_tweet tw " +
			         " LEFT OUTER JOIN tweet_campaign tc ON tw.id = tc.tweet_id " +
			         " WHERE tc.campaign_id = ? ) " +
			         " limit 1 ";


		Session session = this.getSession(false);
		log.debug("Session :" + session.toString());
		Query query = session.createSQLQuery(sql);
		
		//Query query =this.getSession(false).createQuery(q);
		log.debug("CampaignId: " + campaignId);
        query.setLong(0, campaignId);
        query.setLong(1, campaignId);
        query.setFlushMode(FlushMode.COMMIT);
        
        ArrayList<Object[]> al = (ArrayList<Object[]>)query.list();
//        Object[] o = (Object[])query.uniqueResult();
        Object[] o = al.get(0); //Shd get only one
        log.debug("Object returned " + (o==null?"NULL":o.toString()));
        TATweet tw = new TATweet();
        BigInteger bi = (BigInteger)o[0];
        tw.setId(new Long(bi.longValue()));
        tw.setDescription((String)o[1]);
        
        return tw;
	}
	
/*	public TATweet getTestDate(Long campaignId){

		String sql = " (SELECT  max(tw.last_sent) " +
			         " FROM ta_tweet tw " +
			         " LEFT OUTER JOIN tweet_campaign tc ON tw.id = tc.tweet_id " +
			         " WHERE tc.campaign_id = ? ) " ;

		String sql2 = " (SELECT  tw.last_sent " +
        " FROM ta_tweet tw " +
        " WHERE tw.id = ? ) " ;
		
		String sql3 = "SELECT tw.last_sent " +
		" FROM ta_tweet tw " +
		" LEFT OUTER JOIN tweet_campaign tc ON tw.id = tc.tweet_id " +
		" WHERE tc.campaign_id = ? " +
		" AND tc.tweet_id =308 " ;
		
		Session session = this.getSession(false);
		log.debug("Session :" + session.toString());
		Query query = session.createSQLQuery(sql3);
		
		//Query query =this.getSession(false).createQuery(q);
		log.debug("CampaignId: " + campaignId);
        query.setLong(0, campaignId);
        query.setFlushMode(FlushMode.COMMIT);
        
        ArrayList<Timestamp> al = (ArrayList<Timestamp>)query.list();
//        Object[] o = (Object[])query.uniqueResult();
        TATweet tweet = new TATweet();
        Timestamp o = al.get(0); //Shd get only one
        log.debug("Object returned " + (o==null?"NULL":o.toString()));
        Date bi = (Date)o;
        tweet.setLastSentDate(bi);
        
        return tweet;
	}*/
	
	/**
	 * This method returns the earliest Tweeter that was tweeted to <code>hours</code> ago for a campaign
	 * @param campaignId
	 * @param followerId
	 * @param hours
	 * @return
	 */
	public TwitterUser getTwitterUserTweetedToInThePastNHoursForCampaign(Long campaignId, String followerId, int hours){
		
		String sql = 
		 " SELECT " +
			" tu.campaign_id as campaign_id, " +
			" tu.twitter_id as twitter_id, " +
			" tu.email as email, " +
			" tu.first_name as first_name," + 
			" tu.last_name as last_name," + 
			" tu.middle_name as middle_name," + 
			" tu.profile_description as profile_description," + 
			" tu.friends_count as friends_count, " +
			" tu.followers_count as followers_count," + 
			" tu.twittering_since as twittering_since, " +
			" tu.on_hold as on_hold," +
			" tu.weight as weight, " +
			" tu.last_sent as last_sent," + 
			" tu.created_date as created_date," + 
			" tu.created_by as created_by " +
			" FROM twitter_user tu " +
			" WHERE tu.campaign_id = ? " + 
			" AND YEAR(tu.last_sent) != '2000' " +
			" AND tu.on_hold = 'false' " +
			" AND TIMESTAMPDIFF(MINUTE, tu.last_sent, sysdate()) < ? " +
			" AND tu.twitter_id NOT IN  " +
			" ( SELECT tf.followee_id " +
			" FROM twitter_follower tf " +
			" WHERE tf.follower_id = ? " +
			" )     " +
			" ORDER BY TIMESTAMPDIFF(MINUTE, tu.last_sent, sysdate()) DESC " +
			" LIMIT 1" ;

		Query query = this.getSession(false).createSQLQuery(sql);
		
		log.debug("CampaignId4: " + campaignId );
		query.setLong(0, campaignId);
		query.setLong(1, new Long(hours * 60));
		query.setString(2, followerId);
		
		Object[] o = (Object[])query.uniqueResult();
		TwitterUser tu = new TwitterUser();
		BigInteger bi = (BigInteger)o[0];
		tu.setCampaignId(new Long(bi==null?0L:bi.longValue()));
		tu.setTwitterId((String)o[1]);
		tu.setEmail((String)o[2]);
		tu.setFirstName((String)o[3]);
		tu.setLastName((String)o[4]);
		tu.setMiddleName((String)o[5]);
		tu.setProfileDescription((String)o[6]);
		BigInteger friends = (BigInteger)o[7];
		tu.setFriendsCount(friends==null?0:friends.intValue());
		BigInteger followers = (BigInteger)o[8];
		tu.setFollowersCount(followers==null?0:followers.intValue());
		tu.setTwitteringSince((Timestamp)o[9]);
		tu.setOnHold((Boolean)o[10]);
		BigInteger weight = (BigInteger)o[11];
		tu.setWeight(weight==null?0L:weight.longValue());
		tu.setLastSent((Timestamp)o[12]);
		tu.setCreatedDate((Timestamp)o[13]);
		tu.setCreatedBy((String)o[14]);
		log.debug("TU from getTwitterUserTweetedToInThePastNHoursForCampaign is " + tu.toString());
		return tu;
	}
	
	
	/**
	 * This method is not currently being used.
	 * What it does is to get the earliest of tweeters that are NOT being followed by followerId
	 * 
	 * @param campaignId
	 * @param followerId
	 * @return
	 */
	public TwitterUser getNextTwitterUserToFollowForCampaign(Long campaignId, String followerId){
		
		String sql = " SELECT tu.campaign_id as campaign_id, " +
		             " tu.twitter_id as twitter_id, " +
		             " tu.email as email, " +
		             " tu.first_name as first_name, " +
		             " tu.last_name as last_name, " +
		             " tu.middle_name as middle_name, " +
		             " tu.profile_description as profile_description, " +
		             " tu.friends_count as friends_count, " +
		             " tu.followers_count as followers_count, " +
		             " tu.twittering_since as twittering_since, " +
		             " tu.on_hold as on_hold, " +
		             " tu.weight as weight, " +
		             " tu.last_sent as last_sent, " +
		             " tu.created_date as created_date, " +
		             " tu.created_by as created_by " +
			         " FROM twitter_user tu " +
			         " WHERE tu.campaign_id = ? " +
			         " AND tu.on_hold = 'false' " +
			         " AND tu.last_sent = " +
				         " (" +
				         " SELECT  min(tu.last_sent) " +
				         " FROM twitter_user tu " +
				         " WHERE tu.campaign_id = ? " +
				         " AND tu.on_hold = 'false' " +
				         
				       //Don't want to follow a person just added
				       //When a person is added, the last_sent is set to the year 2000
				       //This will also ensure that we will only follow AFTER a tweet has been sent to this 
				       //Twitterer
				         " AND YEAR(tu.last_sent) != '2000' " + 
				         										
				         ") " +
				     " AND tu.twitter_id NOT IN  " +
				     "  ( SELECT tf.followee_id " + 
				     "    FROM twitter_follower tf " + 
				     "    WHERE tf.follower_id = ? ) " +
			         " limit 1 ";

		Query query = this.getSession(false).createSQLQuery(sql);
		
		log.debug("CampaignId3: " + campaignId );
		query.setLong(0, campaignId);
		query.setLong(1, campaignId);
		query.setString(2, followerId);
		Object[] o = (Object[])query.uniqueResult();
		TwitterUser tu = new TwitterUser();
		BigInteger bi = (BigInteger)o[0];
		tu.setCampaignId(new Long(bi==null?0L:bi.longValue()));
		tu.setTwitterId((String)o[1]);
		tu.setEmail((String)o[2]);
		tu.setFirstName((String)o[3]);
		tu.setLastName((String)o[4]);
		tu.setMiddleName((String)o[5]);
		tu.setProfileDescription((String)o[6]);
		BigInteger friends = (BigInteger)o[7];
		tu.setFriendsCount(friends==null?0:friends.intValue());
		BigInteger followers = (BigInteger)o[8];
		tu.setFollowersCount(followers==null?0:followers.intValue());
		tu.setTwitteringSince((Timestamp)o[9]);
		tu.setOnHold((Boolean)o[10]);
		BigInteger weight = (BigInteger)o[11];
		tu.setWeight(weight==null?0L:weight.longValue());
		tu.setLastSent((Timestamp)o[12]);
		tu.setCreatedDate((Timestamp)o[13]);
		tu.setCreatedBy((String)o[14]);
		log.debug("TU to follow is " + tu.toString());
		return tu;
	}
	
	
	public TwitterUser getEarliestSentTwitterUserForCampaign(Long campaignId){
		
		String sql = " SELECT tu.campaign_id as campaign_id, " +
		             " tu.twitter_id as twitter_id, " +
		             " tu.email as email, " +
		             " tu.first_name as first_name, " +
		             " tu.last_name as last_name, " +
		             " tu.middle_name as middle_name, " +
		             " tu.profile_description as profile_description, " +
		             " tu.friends_count as friends_count, " +
		             " tu.followers_count as followers_count, " +
		             " tu.twittering_since as twittering_since, " +
		             " tu.on_hold as on_hold, " +
		             " tu.weight as weight, " +
		             " tu.last_sent as last_sent, " +
		             " tu.created_date as created_date, " +
		             " tu.created_by as created_by " +
			         " FROM twitter_user tu " +
			         " WHERE tu.campaign_id = ? " +
			         " AND tu.on_hold = 'false' " +
			         " AND tu.last_sent = " +
				         " (" +
				         " SELECT  min(tu.last_sent) " +
				         " FROM twitter_user tu " +
				         " WHERE tu.campaign_id = ? " +
				         " AND tu.on_hold = 'false' " +
				         ") " +
			         " limit 1 ";

		Query query = this.getSession(false).createSQLQuery(sql);
		
		log.debug("CampaignId2: " + campaignId );
		query.setLong(0, campaignId);
		query.setLong(1, campaignId);
		Object[] o = (Object[])query.uniqueResult();
		TwitterUser tu = new TwitterUser();
		BigInteger bi = (BigInteger)o[0];
		tu.setCampaignId(new Long(bi==null?0L:bi.longValue()));
		tu.setTwitterId((String)o[1]);
		tu.setEmail((String)o[2]);
		tu.setFirstName((String)o[3]);
		tu.setLastName((String)o[4]);
		tu.setMiddleName((String)o[5]);
		tu.setProfileDescription((String)o[6]);
		BigInteger friends = (BigInteger)o[7];
		tu.setFriendsCount(friends==null?0:friends.intValue());
		BigInteger followers = (BigInteger)o[8];
		tu.setFollowersCount(followers==null?0:followers.intValue());
		tu.setTwitteringSince((Timestamp)o[9]);
		tu.setOnHold((Boolean)o[10]);
		BigInteger weight = (BigInteger)o[11];
		tu.setWeight(weight==null?0L:weight.longValue());
		tu.setLastSent((Timestamp)o[12]);
		tu.setCreatedDate((Timestamp)o[13]);
		tu.setCreatedBy((String)o[14]);
		
		return tu;
	}
	
	public TwitterUser getLatestSentTwitterUserForCampaign(Long campaignId){
		
		String sql = " SELECT tu.campaign_id as campaign_id, " +
		             " tu.twitter_id as twitter_id, " +
		             " tu.email as email, " +
		             " tu.first_name as first_name, " +
		             " tu.last_name as last_name, " +
		             " tu.middle_name as middle_name, " +
		             " tu.profile_description as profile_description, " +
		             " tu.friends_count as friends_count, " +
		             " tu.followers_count as followers_count, " +
		             " tu.twittering_since as twittering_since, " +
		             " tu.on_hold as on_hold, " +
		             " tu.weight as weight, " +
		             " tu.last_sent as last_sent, " +
		             " tu.created_date as created_date, " +
		             " tu.created_by as created_by " +
			         " FROM twitter_user tu " +
			         " WHERE tu.campaign_id = ? " +
			         " AND tu.on_hold = 'false' " +
			         " AND tu.last_sent = " +
				         " (" +
				         " SELECT  max(tu.last_sent) " +
				         " FROM twitter_user tu " +
				         " WHERE tu.campaign_id = ? " +
				         " AND tu.on_hold = 'false' " +
				         ") " +
			         " limit 1 ";

		Query query = this.getSession(false).createSQLQuery(sql);
		
		log.debug("CampaignId2: " + campaignId );
		query.setLong(0, campaignId);
		query.setLong(1, campaignId);
		Object[] o = (Object[])query.uniqueResult();
		TwitterUser tu = new TwitterUser();
		BigInteger bi = (BigInteger)o[0];
		tu.setCampaignId(new Long(bi==null?0L:bi.longValue()));
		tu.setTwitterId((String)o[1]);
		tu.setEmail((String)o[2]);
		tu.setFirstName((String)o[3]);
		tu.setLastName((String)o[4]);
		tu.setMiddleName((String)o[5]);
		tu.setProfileDescription((String)o[6]);
		BigInteger friends = (BigInteger)o[7];
		tu.setFriendsCount(friends==null?0:friends.intValue());
		BigInteger followers = (BigInteger)o[8];
		tu.setFollowersCount(followers==null?0:followers.intValue());
		tu.setTwitteringSince((Timestamp)o[9]);
		tu.setOnHold((Boolean)o[10]);
		BigInteger weight = (BigInteger)o[11];
		tu.setWeight(weight==null?0L:weight.longValue());
		tu.setLastSent((Timestamp)o[12]);
		tu.setCreatedDate((Timestamp)o[13]);
		tu.setCreatedBy((String)o[14]);
		
		return tu;
	}

	public Collection<TweetsSent> getSentToTwitterersForACampaign(
			Long campaignId) {
		Query query =this.getSession(false).createQuery("from com.nayidisha.twitter.domain.TweetsSent ts where ts.campaignId = ? ");
        query.setLong(0, campaignId);
        Collection<TweetsSent> tsList = query.list();
        return tsList;
	}
	
	public Dashboard  getDashboard(){
		return (Dashboard)this.getSession(false).get(Dashboard.class, Dashboard.DASHBOARD_ID);
	}
	
	
	public void flushSession() {
		this.getSession(false).flush();
	}
	

}
