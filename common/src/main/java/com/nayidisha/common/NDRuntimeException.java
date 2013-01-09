package com.nayidisha.common;

public class NDRuntimeException extends RuntimeException{
	private static final long serialVersionUID = 8570199294706120274L;

	/**
     *@param str  Text describing exception (exception message)
     */
    public NDRuntimeException(String str) {
        super(str);
    }

    /**
     *@param t    wrapped exception
     *@since jdk1.4
     */
    public NDRuntimeException(Throwable t){
    	super(t);
    }

    /**
     * Allows for nesting of exceptions
     *@param str  Text describing exception (exception message)
     *@param t    wrapped exception
     *@since jdk1.4
     */
    public NDRuntimeException(String str, Throwable t){
    	super(str, t);
    }
}
