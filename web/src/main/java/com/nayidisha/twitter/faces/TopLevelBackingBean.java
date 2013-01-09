package com.nayidisha.twitter.faces;

import javax.faces.event.AbortProcessingException;

import com.icesoft.faces.component.ext.HtmlPanelGroup;
import com.icesoft.faces.component.paneltabset.TabChangeEvent;
import com.icesoft.faces.component.paneltabset.TabChangeListener;
import com.nayidisha.twitter.faces.utils.FacesUtils;

public abstract class TopLevelBackingBean implements TabChangeListener{

    //protected  boolean getRenderDetailPanel();
	
	public void processTabChange(TabChangeEvent arg0)
			throws AbortProcessingException {
		/* This solution to change the state of a component des not work becaue, altho' it does change the 
		 * state of the component, it does not change the state of the backing bean from true to false.
		 * So as a result, when we want the panel to be set to true again via the UI, the AJAX builder does not 
		 * know that something changed and consequently does not send the true flag to the UI.			

			HtmlPanelGroup htmlPanelGroup1 =  (HtmlPanelGroup)FacesUtils.findComponentInRoot("campaignDetailPanel");
			htmlPanelGroup1.setRendered(false);
			HtmlPanelGroup htmlPanelGroup2 =  (HtmlPanelGroup)FacesUtils.findComponentInRoot("tweetDetailPanel");
			htmlPanelGroup2.setRendered(false);
			HtmlPanelGroup htmlPanelGroup3 =  (HtmlPanelGroup)FacesUtils.findComponentInRoot("schedulerDetailPanel");
			htmlPanelGroup3.setRendered(false);			
		*/			
 	
		CampaignBean cb = (CampaignBean)FacesUtils.getBean("campaignBean");
		cb.setRenderDetailsPanel(false);
		TweetBean tb = (TweetBean)FacesUtils.getBean("tweetBean");
		tb.setRenderDetailsPanel(false);
		SchedulerBean sb = (SchedulerBean)FacesUtils.getBean("schedulerBean");
		sb.setRenderDetailsPanel(false);		
		

		
	}

	
}
