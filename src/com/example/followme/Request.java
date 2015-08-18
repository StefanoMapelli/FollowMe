package com.example.followme;

import java.io.Serializable;

public class Request implements Serializable
{
	private static final long serialVersionUID = 3618865785363227401L;
	
	private String id;
	private String type;
	private String state;
	private User sender;
	private User receiver;
	
	public Request(String id, String type, String state, User sender,User reciever) 
	{
		super();
		this.id = id;
		this.type = type;
		this.state = state;
		this.sender = sender;
		this.receiver = reciever;
	}
	
	public User getSender() {
		return sender;
	}
	public void setSender(User sender) {
		this.sender = sender;
	}
	public User getReceiver() {
		return receiver;
	}
	public void setReceiver(User reciever) {
		this.receiver = reciever;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
