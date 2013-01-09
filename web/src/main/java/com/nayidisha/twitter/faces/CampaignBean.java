package com.nayidisha.twitter.faces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icesoft.faces.component.ext.HtmlDataTable; 
import com.icesoft.faces.component.paneltabset.TabChangeEvent;
import com.icesoft.faces.component.paneltabset.TabChangeListener;
import com.icesoft.faces.context.DisposableBean;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.nayidisha.twitter.domain.Campaign;
import com.nayidisha.twitter.faces.utils.FacesUtils;
import com.nayidisha.twitter.service.CampaignService;
import com.nayidisha.utils.NDUtils;

public class CampaignBean extends TopLevelBackingBean implements DisposableBean {

	private static Log log = LogFactory.getLog(CampaignBean.class);
	
	private CampaignService campaignService;
	private PersistentFacesState state;
	private HtmlDataTable campaignDataTable;
	private boolean dataTableChanged;
	Collection<Campaign> campaignList;
	private Collection<CampaignView> campaignViewList;
	private boolean renderDetailsPanel = false;
	private CampaignView currentCampaignView;
	private Hashtable<String, Boolean> deleteCheckboxStates = new Hashtable<String, Boolean> ();
	
	public void dispose() throws Exception {
        if (log.isInfoEnabled()) {
            log.info("Dispose CampaignBean - cleaning up");
        }
	}
	public CampaignBean(){
		log.debug("CampaignBean Started");
		dataTableChanged = true;
	}

	public void createNewCampaign(ActionEvent ae){

		this.setRenderDetailsPanel(true);
		currentCampaignView = new CampaignView(new Campaign());
		currentCampaignView.setName("<New Campaign>");
	}	
	public void deleteCampaignAndRelatedTweets(ActionEvent ae){
		campaignService.deleteCampaignAndRelatedTweets(((Campaign)currentCampaignView).getId());
	}
	
	public void processCampaign(ActionEvent ae){
		currentCampaignView.setEnabled(true);
		if (currentCampaignView.getId() != null && currentCampaignView.getId().longValue() == 0){
			currentCampaignView.setId(null);
			campaignService.addCampaign((Campaign)currentCampaignView);
		}else {
			//existing campaign
			campaignService.updateCampaign(currentCampaignView);
		}
		dataTableChanged = true;
		
	}
	public void deleteCampaign(ValueChangeEvent ve){
		currentCampaignView = (CampaignView)campaignDataTable.getRowData();
		campaignService.deleteCampaignButNotRelatedTweets(currentCampaignView.getId());
		dataTableChanged = true;
	}
	
	public void setSelectedCampaign(ActionEvent ae){

		String s = ae.getComponent().getId();
		this.setRenderDetailsPanel(true);
		currentCampaignView = (CampaignView)campaignDataTable.getRowData();
		//When the currentCampaign changes then the dataTableChanged boolean of the 
		//TweetBean must be set to true, so that tweets for the new Campaign should be shown.
		TweetBean tb = (TweetBean)FacesUtils.getBean("tweetBean");
		tb.setDataTableChanged(true);
		
	}

	public CampaignService getCampaignService() {
		return campaignService;
	}

	public void setCampaignService(CampaignService campaignService) {
		this.campaignService = campaignService;
	}

	public PersistentFacesState getState() {
		return state;
	}

	public void setState(PersistentFacesState state) {
		this.state = state;
	}

	public HtmlDataTable getCampaignDataTable() {
		return campaignDataTable;
	}

	public void setCampaignDataTable(HtmlDataTable campaignDataTable) {
		this.campaignDataTable = campaignDataTable;
	}


	public boolean isRenderDetailsPanel() {
		return renderDetailsPanel;
	}

	public void setRenderDetailsPanel(boolean renderDetailsPanel) {
		this.renderDetailsPanel = renderDetailsPanel;
	}



	public CampaignView getCurrentCampaignView() {
		return currentCampaignView;
	}

	public void setCurrentCampaignView(CampaignView currentCampaignView) {
		this.currentCampaignView = currentCampaignView;
	}

	public Collection<CampaignView> getCampaignViewList() {
		if (dataTableChanged){
			String userId = NDUtils.getCurrentUserId();
			campaignList = campaignService.getCampaignsForAUser(userId);
			dataTableChanged = false;
		}
		ArrayList<CampaignView> cvl = new ArrayList<CampaignView>();
		for (Iterator iterator = campaignList.iterator(); iterator.hasNext();) {
			Campaign campaign = (Campaign) iterator.next();
			CampaignView v = new CampaignView(campaign);
			cvl.add(v);
		}
		Collections.sort((List)cvl); 
		
		return cvl;
	}

	public void setCampaignViewList(Collection<CampaignView> campaignViewList) {
		this.campaignViewList = campaignViewList;
	}

	public Hashtable<String, Boolean> getDeleteCheckboxStates() {
		return deleteCheckboxStates;
	}

	public void setDeleteCheckboxStates(
			Hashtable<String, Boolean> deleteCheckboxStates) {
		this.deleteCheckboxStates = deleteCheckboxStates;
	}
	public boolean isDataTableChanged() {
		return dataTableChanged;
	}
	public void setDataTableChanged(boolean dataTableChanged) {
		this.dataTableChanged = dataTableChanged;
	}



}
