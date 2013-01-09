package com.nayidisha.twitter.dataaccess;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.nayidisha.twitter.domain.TAOAuthToken;



public class TwitterHibernateDAO extends HibernateDaoSupport implements TwitterDAO {

	/**
	 * Will assume that the screenName is unique in the ta_access_token table, even though the id column is the PK.
	 */
	public TAOAuthToken getAccessTokenForTwitterId(String twitterId){
        Query query = getSession(false).createQuery("from com.nayidisha.twitter.domain.TAOAuthToken tao where tao.screenName = ?");
        query.setString(0, twitterId);
        TAOAuthToken token = (TAOAuthToken)query.uniqueResult();
		return token;
		
	}
	
	public void flushSession() {
		this.getSession(false).flush();
	}
}
