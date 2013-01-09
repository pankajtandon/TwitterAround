package com.nayidisha.common;

public class CampaignDoesNotExistException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3146256724981579167L;

	/**
     *@param str  Text describing exception (exception message)
     */
    public CampaignDoesNotExistException(String str) {
        super(str);
    }

    /**
     *@param t    wrapped exception
     *@since jdk1.4
     */
    public CampaignDoesNotExistException(Throwable t){
    	super(t);
    }

    /**
     * Allows for nesting of exceptions
     *@param str  Text describing exception (exception message)
     *@param t    wrapped exception
     *@since jdk1.4
     */
    public CampaignDoesNotExistException(String str, Throwable t){
    	super(str, t);
    }
}
