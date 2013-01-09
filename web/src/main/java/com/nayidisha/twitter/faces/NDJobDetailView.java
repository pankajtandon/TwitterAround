package com.nayidisha.twitter.faces;



/**
 * This class is needed because Faces is not able to serialize NDJobDetail even after making the 
 * Scheduler member variable transient.
 * Consequently it hangs if NDJobDetal is used directly in a datatable
 * @author 721479
 *
 */
public class NDJobDetailView implements Comparable{
	private int id;
	private String campaignName;
	private String name;
	private String group;

	private String prevRunTimeString;
	private String nextRunTimeString;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCampaignName() {
		return campaignName;
	}
	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getPrevRunTimeString() {
		return prevRunTimeString;
	}
	public void setPrevRunTimeString(String prevRunTimeString) {
		this.prevRunTimeString = prevRunTimeString;
	}
	public String getNextRunTimeString() {
		return nextRunTimeString;
	}
	public void setNextRunTimeString(String nextRunTimeString) {
		this.nextRunTimeString = nextRunTimeString;
	}
	public int compareTo(Object o) {
	    return ((this.getId() -  (((NDJobDetailView)o).getId())));
	}
}
