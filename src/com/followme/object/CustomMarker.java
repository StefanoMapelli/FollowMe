package com.followme.object;

import java.io.Serializable;

public class CustomMarker implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8603825592241188120L;
	private String title;
	private String snippet;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSnippet() {
		return snippet;
	}
	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}
	public CustomMarker(String title, String snippet) {
		super();
		this.title = title;
		this.snippet = snippet;
	}
}
