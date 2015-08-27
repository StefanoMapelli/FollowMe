package com.followme.object;
public class PhotoMarker extends CustomMarker{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4296118549589550437L;
	private String path;
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public PhotoMarker(String title, String snippet, String path) {
		super(title, snippet);
		this.path = path;
	}	
}
