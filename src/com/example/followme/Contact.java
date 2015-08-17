package com.example.followme;

import java.io.Serializable;

public class Contact implements Serializable {

	private static final long serialVersionUID = 4885357467297891708L;
	
	private String name;
	private String phoneNumber;
	private boolean checked = false;
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public Contact(String n , String pn)
	{
		name = n;
		phoneNumber = pn;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}
