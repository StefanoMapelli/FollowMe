package com.example.followme;

public class Contact {
	
	private String name;
	private String phoneNumber;
	
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
