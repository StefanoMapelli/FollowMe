package com.example.followme;

public class Request {
	
	private String id;
	private String type;
	private boolean accepted;
	private User sender;
	private User receiver;
	
	public Request(String id, String type, boolean accepted, User sender,User reciever) 
	{
		super();
		this.id = id;
		this.type = type;
		this.accepted = accepted;
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
	public boolean getAccepted() {
		return accepted;
	}
	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}
}
