package com.followme.object;

public class VideoMarker extends CustomMarker{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8461794177077148085L;
	private String videoPath;
	
	public VideoMarker(String title, String snippet, String videoUriString) {
		super(title, snippet);
		this.videoPath = videoUriString;
	}
	public String getVideoUriString() {
		return videoPath;
	}
	public void setVideoUriString(String videoUriString) {
		this.videoPath = videoUriString;
	}
}
