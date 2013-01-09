package com.nayidisha.twitter.faces;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

import nl.captcha.Captcha;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icesoft.faces.context.DisposableBean;
import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Highlight;
import com.icesoft.faces.context.effects.Shake;
import com.icesoft.faces.context.effects.Shrink;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.nayidisha.twitter.domain.TwitterUser;
import com.nayidisha.twitter.service.TwitterService;
import com.nayidisha.utils.NDStringUtils;

public class TwitterBean implements DisposableBean{

	private static Log log = LogFactory.getLog(TwitterBean.class);
		
	private TwitterService twitterService;

	private PersistentFacesState state;
	
	private String result = "";
	private String twitterUserIdToSearch;
	private String captchaValue;
	private Effect highlightEffect ;
	private Effect funEffect;


	private int imageTickler = 0;



	public TwitterBean(){
		state = PersistentFacesState.getInstance();
		createCaptcha();
		createFunEffect();
	}

	public String restrictedArea(){
		return "restrictedArea";
	}
	
	public void processEnteredValue(ActionEvent event){
		
		if (NDStringUtils.hasSomeMeaningfulValue(this.getTwitterUserIdToSearch())) {
			//if (NDStringUtils.hasSomeMeaningfulValue(this.getCaptchaValue())){
				Captcha captcha = getCaptchaFromSession();
				if (captcha == null){
					captcha = createCaptcha();
				}
				if (captcha.isCorrect(this.getCaptchaValue())){
	
					TwitterUser tu = twitterService.getTwitterUser(this.getTwitterUserIdToSearch());
					if (tu != null){
						result = "<b>" + this.getTwitterUserIdToSearch() + "</b> has been tweeting since: " + ((tu.getTwitteringSince()==null)?"NOT_AVAILABLE":(tu.getTwitteringSince().toString()))
						         + (tu.getDaysTweeting() != 0?(" (<b>" + tu.getDaysTweeting() + " days</b>)"):"")
						         + "<BR/>"
						         + "<b>Friends:</b> " + tu.getFriendsCount()
						         + "<BR/>"
						         + "<b>Followers:</b> " + tu.getFollowersCount();
						refreshCaptcha();
					} else {
						result = "Ths user <b>does not exist!</b> Please try a different user.";
						refreshCaptcha();
					}
				} else {
					result = "Answer did not match the text on the image. You may consider using another image by clicking link above?";
				}
			//}
		} else {
			result = "No Twitter user id entered! Please enter a user id and try again.";
			refreshCaptcha();
		}

		createHighlightEffect();
	}
	
	private Captcha createCaptcha(){
		HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		Captcha captcha = new Captcha.Builder(200, 50)
    	.addText()
    	//.gimp()
    	.addBorder()
        .addNoise()
        .addBackground()
        .build();

		session.setAttribute(Captcha.NAME, captcha);
		return captcha;
	}
	
	private Captcha getCaptchaFromSession(){
		HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		return (Captcha) session.getAttribute(Captcha.NAME);
	}
	
	private void createFunEffect(){
		funEffect =  new Shake();
		funEffect.setDuration(5);
		funEffect.setFired(false);	
	}
	
	private void createHighlightEffect(){
		highlightEffect =  new Highlight("#FFCC0B");
		highlightEffect.setDuration(5);
		highlightEffect.setFired(false);	
	}
	private void refreshCaptcha(){
		createCaptcha();
		imageTickler++;
	}
	public void refreshCaptcha(ActionEvent e){
		refreshCaptcha();
	}
	
	
	public String getImageUrl(){
		return "stickyImg" + "?x=" + imageTickler; //will cause the image to refresh
	}
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public PersistentFacesState getState() {
		return state;
	}

	public void dispose() throws Exception {
        if (log.isInfoEnabled()) {
            log.info("Dispose TwitterBean - cleaning up");
        }
    }
 
	public Effect getHighlightEffect() {
		return highlightEffect;
	}


	public void setHighlightEffect(Effect effect) {
		this.highlightEffect = (Highlight)effect;
	}
	
	public TwitterService getTwitterService() {
		return twitterService;
	}

	public void setTwitterService(TwitterService twitterService) {
		this.twitterService = twitterService;
	}

	public String getTwitterUserIdToSearch() {
		return twitterUserIdToSearch;
	}

	public void setTwitterUserIdToSearch(String twitterUserIdToSearch) {
		this.twitterUserIdToSearch = twitterUserIdToSearch;
	}
	public String getCaptchaValue() {
		return captchaValue;
	}

	public void setCaptchaValue(String captchaValue) {
		this.captchaValue = captchaValue;
	}
	public Effect getFunEffect() {
		return funEffect;
	}


	public void setFunEffect(Effect funEffect) {
		this.funEffect = funEffect;
	}
}
