package com.way2sms;

import java.io.Serializable;


public class Contact implements Serializable {

	private static final long serialVersionUID = 8263744218104663887L;

	private Boolean selected = false;

	private String name = null;

	private String mobileNo = null;

	private String group = null;

	
	public Contact(Boolean selected, String name, String mobileNo, String group) {
		super();
		this.selected = selected;
		this.name = name;
		this.mobileNo = mobileNo;
		this.group = group;
	}

	
	public String getGroup() {
		return group;
	}

	
	public void setGroup(String group) {
		this.group = group;
	}

	
	public Contact(boolean selected, String name, String mobileNo, String group) {
		this.selected = selected;
		this.name = name;
		this.mobileNo = mobileNo;
		this.group = group;

	}

	
	public boolean isSelected() {
		return selected;
	}

	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	
	public String getName() {
		return name;
	}

	
	public void setName(String name) {
		this.name = name;
	}

	
	public String getMobileNo() {
		return mobileNo;
	}

	
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

}