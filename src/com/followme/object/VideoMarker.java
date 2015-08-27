package com.followme.object;

public class VideoMarker extends CustomMarker{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8461794177077148085L;
	private String videoUriString;
	private String thumbnailPath;
	
	public VideoMarker(String title, String snippet, String videoUriString,String thumbnailPath) {
		super(title, snippet);
		this.videoUriString = videoUriString;
		this.thumbnailPath = thumbnailPath;
	}
	public String getVideoUriString() {
		return videoUriString;
	}
	public void setVideoUriString(String videoUriString) {
		this.videoUriString = videoUriString;
	}
	public String getThumbnailPath() {
		return thumbnailPath;
	}
	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}
}
