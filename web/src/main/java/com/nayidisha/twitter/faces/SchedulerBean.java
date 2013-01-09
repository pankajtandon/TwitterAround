package com.nayidisha.twitter.faces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.context.DisposableBean;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.nayidisha.common.NDRuntimeException;
import com.nayidisha.twitter.domain.GenericLineItem;
import com.nayidisha.twitter.domain.NDJobDetail;
import com.nayidisha.twitter.domain.SchedulerDetail;
import com.nayidisha.twitter.jobs.NDTrigger;
import com.nayidisha.twitter.service.SchedulerService;
import com.nayidisha.utils.NDUtils;

public class SchedulerBean extends TopLevelBackingBean implements DisposableBean{
	private static Log log = LogFactory.getLog(SchedulerBean.class);
	
	private SchedulerService schedulerService;
	private PersistentFacesState state;
	private HtmlDataTable jobDataTable;
	private boolean dataTableChanged;
	private Collection<NDJobDetailView> ndJobDetailViewList;
	private boolean renderDetailsPanel = false;
	private NDJobDetailView currentNDJobDetailView;
	private SelectItem[] triggerList;
	private String currentTriggerValue;
	
	public SchedulerBean(){
		log.debug("Starting schedulerBean");
		dataTableChanged = true;
	}
	public void dispose() throws Exception {
        if (log.isInfoEnabled()) {
            log.info("Dispose SchedulerBean - cleaning up");
        }
	}

	
	public void setSelectedJob(ActionEvent ae){
		String s = ae.getComponent().getId();
		this.setRenderDetailsPanel(true);
		currentNDJobDetailView = (NDJobDetailView)jobDataTable.getRowData();

	}

	public String getCurrentUser(){
		return NDUtils.getCurrentUserId();
	}

	public PersistentFacesState getState() {
		return state;
	}

	public void setState(PersistentFacesState state) {
		this.state = state;
	}

	public HtmlDataTable getjobDataTable() {
		return jobDataTable;
	}

	public void setJobDataTable(HtmlDataTable jobDataTable) {
		this.jobDataTable = jobDataTable;
	}


	public boolean isRenderDetailsPanel() {
		return renderDetailsPanel;
	}

	public void setRenderDetailsPanel(boolean renderDetailsPanel) {
		this.renderDetailsPanel = renderDetailsPanel;
	}



	public NDJobDetailView getCurrentNDJobDetailView() {
		return currentNDJobDetailView;
	}

	public void setCurrentNDJobDetailView(NDJobDetailView currentNDJobDetailView) {
		this.currentNDJobDetailView = currentNDJobDetailView;
	}

	public Collection<NDJobDetailView> getNdJobDetailViewList() {
		ArrayList<NDJobDetailView> al = new ArrayList<NDJobDetailView>();
		if (dataTableChanged){
			Collection<NDJobDetail> ndJobDetailList;
			try {
				SchedulerDetail schedulerDetail = schedulerService.showJobs(null, null, NDUtils.getCurrentUserId());
				ndJobDetailList = schedulerDetail.getJobDetailList();
				dataTableChanged = false;
			} catch (Exception e){
				throw new NDRuntimeException("Error scheduling...", e);
			}
			if (ndJobDetailList != null){
				for (Iterator iterator = ndJobDetailList.iterator(); iterator
						.hasNext();) {
					NDJobDetail jobDetail = (NDJobDetail) iterator.next();
					NDJobDetailView ndv = createViewFromJobDetail(jobDetail);
					al.add(ndv);
				}
			}
			Collections.sort((List)al); 
			this.ndJobDetailViewList = al;
		} 
		return this.ndJobDetailViewList;
	}



	public void setTrigger(ValueChangeEvent vce){
	     currentTriggerValue = (String)vce.getNewValue();
		
	}

	public void scheduleJob(ActionEvent ae){
		try {
			schedulerService.startJob(currentNDJobDetailView.getCampaignName(), currentNDJobDetailView.getName(), currentNDJobDetailView.getGroup(), currentTriggerValue);
			
			//redraw the datatable
			dataTableChanged = true;

		} catch (Exception e){
			throw new NDRuntimeException("Could not schedule job", e);
		}
	}

	public void stopJob(ActionEvent ae){
		try {
			schedulerService.stopJob( currentNDJobDetailView.getName(), currentNDJobDetailView.getGroup());
			//redraw the datatable
			dataTableChanged = true;
		} catch (Exception e){
			throw new NDRuntimeException("Could not stop job", e);
		}
	}
	
	
	public SchedulerService getSchedulerService() {
		return schedulerService;
	}


	public void setSchedulerService(SchedulerService schedulerService) {
		this.schedulerService = schedulerService;
	}


	public void setNdJobDetailViewList(Collection<NDJobDetailView> ndJobDetailViewList) {
		this.ndJobDetailViewList = ndJobDetailViewList;
	}


	public boolean isDataTableChanged() {
		return dataTableChanged;
	}


	public void setDataTableChanged(boolean dataTableChanged) {
		this.dataTableChanged = dataTableChanged;
	}
	public SelectItem[] getTriggerList() {
		SelectItem[] si = new SelectItem[NDTrigger.values().length];
		int i = 0;
        for (NDTrigger trigger:NDTrigger.values()){
        	String name = trigger.getName();
        	String desc = trigger.getDesc();
        	si[i] = new SelectItem(name, desc);
        	i++;
        }
		return si;
	}
	
	
	public void setTriggerList(SelectItem[] triggerList) {
		this.triggerList = triggerList;
	}
	
	private NDJobDetailView createViewFromJobDetail(NDJobDetail jobDetail){
		NDJobDetailView ndv = new NDJobDetailView();
		ndv.setId(jobDetail.getId());
		ndv.setCampaignName(jobDetail.getCampaignName());
		ndv.setName(jobDetail.getName());
		ndv.setGroup(jobDetail.getGroup());
		ndv.setPrevRunTimeString(jobDetail.getPrevRunTimeString());
		ndv.setNextRunTimeString(jobDetail.getNextRunTimeString());
		return ndv;
	}


}
