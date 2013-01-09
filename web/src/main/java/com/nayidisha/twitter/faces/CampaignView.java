package com.nayidisha.twitter.faces;

import org.apache.commons.beanutils.BeanUtils;

import com.nayidisha.common.NDRuntimeException;
import com.nayidisha.twitter.domain.Campaign;

public class CampaignView extends Campaign {

	private boolean delete;


	public CampaignView(Campaign campaign){
		try {
			BeanUtils.copyProperties(this, campaign);
		} catch (Exception e){
			throw new NDRuntimeException("Could not create campaignView. ", e); 
		}
	}
	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}
}
