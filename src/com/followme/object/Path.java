package com.followme.object;

import java.io.Serializable;

public class Path implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3881754330246868018L;
	private String id;
	private String title;
	private String owner;
	private String date;
	
	public Path()
	{
		
	}
	
	public Path(String id, String title, String owner) {
		super();
		this.id = id;
		this.title = title;
		this.owner = owner;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	

}
