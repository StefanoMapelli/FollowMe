package com.followme.object;

import java.io.Serializable;

public class Media implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2027177842932353913L;
	private byte[] media;
	private String title;
	private Position position;
	
	public Media()
	{
		
	}
	
	public Media(byte[] media, String title, Position position) {
		super();
		this.media = media;
		this.title = title;
		this.position = position;
	}
	public byte[] getMedia() {
		return media;
	}
	public void setMedia(byte[] photoString) {
		this.media = photoString;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}
}
