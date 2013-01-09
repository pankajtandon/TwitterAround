package com.nayidisha.twitter.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.nayidisha.twitter.interceptor.Auditable;


/**
 * Persisted in TA_USER
 * @author 721479
 *
 */
public class TAUser implements Auditable{
	
	private String id;  //maps to username in acegi table (USERS)
	private String password;
	
	private String email;
	private String firstName;
	private String lastName;
	private String middleName;
	private String fullName;
	
	private Date createdDate;
	private Date updatedDate;
	private String createdBy;
	private String updatedBy;

	private boolean enabled;
	
	private Set<Campaign> associatedCampaigns = new HashSet<Campaign>();
	
    public void addCampaignToUser(Campaign campaign){
        if (this.associatedCampaigns==null){
            this.associatedCampaigns = new HashSet<Campaign>();
        }
        this.associatedCampaigns.add(campaign);
        
        //Also set this user to campaign
        campaign.setOwner(this);
    }

    
	//------------------------------------------------------
	public Set<Campaign> getAssociatedCampaigns() {
		return associatedCampaigns;
	}
	public void setAssociatedCampaigns(Set<Campaign> associatedCampaigns) {
		this.associatedCampaigns = associatedCampaigns;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}