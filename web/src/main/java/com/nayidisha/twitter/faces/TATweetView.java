package com.nayidisha.twitter.faces;

import org.apache.commons.beanutils.BeanUtils;

import com.nayidisha.common.NDRuntimeException;
import com.nayidisha.twitter.domain.TATweet;

public class TATweetView extends TATweet {

	private boolean delete;


	public TATweetView(TATweet tweet){
		try {
			BeanUtils.copyProperties(this, tweet);
		} catch (Exception e){
			throw new NDRuntimeException("Could not create tweetView. ", e);
		}
	}
	
	/**
	 * Because this object needs to be persited using JPA, it needs to be passed to the service layer.
	 * However, it is not scoped in the service layer (correctly).
	 * Also, casting this to the parent domain object does not do the trick because the Persister still 
	 * tries to persist the view and not the domain object.
	 * For this reason, this convenience method converts this view to a corresponding domain object
	 *  
	 * @return
	 */
	public TATweet getTATweetForView(){
		TATweet t = new TATweet();
		try {
			BeanUtils.copyProperties(t, this);
		} catch (Exception e){
			throw new NDRuntimeException("Could not create tweetView. ", e);
		}
		return t;
	}
	
	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}
}
