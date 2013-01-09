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
import com.icesoft.faces.component.ext.HtmlSelectBooleanCheckbox;
import com.icesoft.faces.component.paneltabset.TabChangeEvent;
import com.icesoft.faces.component.paneltabset.TabChangeListener;
import com.icesoft.faces.context.DisposableBean;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.nayidisha.twitter.domain.TATweet;
import com.nayidisha.twitter.faces.utils.FacesUtils;
import com.nayidisha.twitter.service.CampaignService;
import com.nayidisha.utils.NDStringUtils;

public class TweetBean extends TopLevelBackingBean implements DisposableBean {

	private static Log log = LogFactory.getLog(TweetBean.class);
	
	private CampaignService campaignService;
	private PersistentFacesState state;
	private boolean dataTableChanged;
	private HtmlDataTable tweetDataTable;
	Collection<TATweet> tweetList;
	private Collection<TATweetView> tweetViewList;
	private boolean renderDetailsPanel = false;
	private TATweetView currentTweetView;
	private Hashtable<String, Boolean> deleteCheckboxStates = new Hashtable<String, Boolean> ();
	
	public void dispose() throws Exception {
        if (log.isInfoEnabled()) {
            log.info("Dispose TweetBean - cleaning up");
        }
	}
	public TweetBean(){
		dataTableChanged = true;
	}

/*	public void processDeleteRequest(ValueChangeEvent e){
		HtmlSelectBooleanCheckbox cb = (HtmlSelectBooleanCheckbox)e.getSource();
		String id = cb.getId();
		if(NDStringUtils.hasSomeMeaningfulValue(id)){
			campaignService.deleteTweet(Long.parseLong(id));
		}
	}*/
	public void createNewTweet(ActionEvent ae){

		this.setRenderDetailsPanel(true);
		currentTweetView = new TATweetView(new TATweet());
		currentTweetView.setDescription("<New Description>");
	}	
	
	public void processTweet(ActionEvent ae){
		
		CampaignView currentCampaignView = getCurrentCampaign();
		

		if (!currentTweetView.hasId()){
			currentTweetView.setId(null);
			TATweet t = currentTweetView.getTATweetForView();
			
			campaignService.addTweet(t, currentCampaignView);
		}else {
			//existing tweet
			campaignService.updateTweet(currentTweetView);
		}
		dataTableChanged = true;
		
	}
	public void deleteTweet(ActionEvent ve){
		if (currentTweetView.hasId()){
			campaignService.deleteTweet(currentTweetView.getId());
			dataTableChanged = true;
		} else {
			log.warn("Deleting a tweet without a current Tweet! Igonring...");
		}
	}
	
	public void setSelectedTweet(ActionEvent ae){
		String s = ae.getComponent().getId();
		this.setRenderDetailsPanel(true);
		currentTweetView = (TATweetView)tweetDataTable.getRowData();

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

	public HtmlDataTable getTweetDataTable() {
		return tweetDataTable;
	}

	public void setTweetDataTable(HtmlDataTable tweetDataTable) {
		this.tweetDataTable = tweetDataTable;
	}


	public boolean isRenderDetailsPanel() {
		return renderDetailsPanel;
	}

	public void setRenderDetailsPanel(boolean renderDetailsPanel) {
		this.renderDetailsPanel = renderDetailsPanel;
	}



	public TATweetView getCurrentTweetView() {
		return currentTweetView;
	}

	public void setCurrentTweetView(TATweetView currentTweetView) {
		this.currentTweetView = currentTweetView;
	}

	public Collection<TATweetView> getTweetViewList() {
		if (dataTableChanged){
			CampaignView currentCampaign = getCurrentCampaign();
			tweetList = campaignService.getTweetsForACampaign(currentCampaign);
			dataTableChanged = false;
		}
		ArrayList<TATweetView> avl = new ArrayList<TATweetView>();
		
		for (Iterator iterator = tweetList.iterator(); iterator.hasNext();) {
			TATweet tweet = (TATweet) iterator.next();
			TATweetView v = new TATweetView(tweet);
			avl.add(v);
		}
		Collections.sort((List)avl); 
		return avl;
	}

	public void setTweetViewList(Collection<TATweetView> tweetViewList) {
		this.tweetViewList = tweetViewList;
	}

	public Hashtable<String, Boolean> getDeleteCheckboxStates() {
		return deleteCheckboxStates;
	}

	public void setDeleteCheckboxStates(
			Hashtable<String, Boolean> deleteCheckboxStates) {
		this.deleteCheckboxStates = deleteCheckboxStates;
	}
	
	private CampaignView getCurrentCampaign(){
		CampaignBean  campaignBean = (CampaignBean)FacesUtils.getBean("campaignBean");
		CampaignView currentCampaignView = campaignBean.getCurrentCampaignView();
		return currentCampaignView;
	}

	public boolean getHasCurrentCampaign(){
		boolean boo =false;
		CampaignView currentCampaignView = this.getCurrentCampaign();
		if (currentCampaignView != null && currentCampaignView.getId() != null && currentCampaignView.getId() != 0) {
			boo = true;
		}
		return boo;
	}


	public boolean isDataTableChanged() {
		return dataTableChanged;
	}


	public void setDataTableChanged(boolean dataTableChanged) {
		this.dataTableChanged = dataTableChanged;
	}


}
