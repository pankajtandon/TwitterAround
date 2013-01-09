package com.nayidisha.common;

public class CampaignNameNotUniqueException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9090636601714692476L;

	/**
     *@param str  Text describing exception (exception message)
     */
    public CampaignNameNotUniqueException(String str) {
        super(str);
    }

    /**
     *@param t    wrapped exception
     *@since jdk1.4
     */
    public CampaignNameNotUniqueException(Throwable t){
    	super(t);
    }

    /**
     * Allows for nesting of exceptions
     *@param str  Text describing exception (exception message)
     *@param t    wrapped exception
     *@since jdk1.4
     */
    public CampaignNameNotUniqueException(String str, Throwable t){
    	super(str, t);
    }
}
