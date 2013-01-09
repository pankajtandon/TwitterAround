package com.nayidisha.twitter.domain;

public class GenericLineItem implements java.io.Serializable{

	String id;
	String name;
	String description;
	String status;
	
	public GenericLineItem(){}
	
	public GenericLineItem(String id, String name){
		this.id = id;
		this.name = name;
	}
	
	
	
	public boolean equals(GenericLineItem that){
		boolean boo = false; 
		if (that != null){
			if (that instanceof GenericLineItem){
				GenericLineItem gli = (GenericLineItem)that;
				if (gli.id != null && gli.id.equals(this.id)){
					boo =true;
				}
			}
		}
		return boo;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
