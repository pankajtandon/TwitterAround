package com.nayidisha.twitter.dataaccess;

import com.nayidisha.twitter.domain.TAOAuthToken;


public interface TwitterDAO {

	public TAOAuthToken getAccessTokenForTwitterId(String twitterId);
	
	/**
	 * Hibernate or ORM DAOs can implement this appropriately
	 */
	public void flushSession();
}
