package com.nayidisha.twitter.faces;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.springframework.security.web.authentication.AbstractProcessingFilter;



public class LoginBean extends TopLevelBackingBean {
    // properties
    private String userId;

    private String password;


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
 
    @SuppressWarnings("deprecation")
	public void login(ActionEvent e) throws java.io.IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("/twitterAround/j_spring_security_check?j_username=" + userId + "&j_password=" + password);
        
        Exception ex = (Exception) FacesContext
        .getCurrentInstance()
        .getExternalContext()
        .getSessionMap()
        .get(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);

		if (ex != null)
		    FacesContext.getCurrentInstance().addMessage(
		            null,
		            new FacesMessage(FacesMessage.SEVERITY_ERROR, ex
		                    .getMessage(), ex.getMessage()));
		}

}
