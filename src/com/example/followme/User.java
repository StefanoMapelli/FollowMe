package com.example.followme;

import com.parse.ParseObject;

public class User {
	
	private String id;
	private String phoneNumber;
	
	public User(String id, String numero)
	{
		this.id=id;
		this.phoneNumber=numero;
	}
	
	public User(ParseObject user)
	{
		this.id=user.getObjectId();
		this.phoneNumber=user.getString("numero");
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String numero) {
		this.phoneNumber = numero;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
