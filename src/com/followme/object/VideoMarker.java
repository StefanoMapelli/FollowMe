package com.followme.object;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.maps.model.Marker;

public class VideoMarker {

	private Uri videoUri;
	private Marker marker;
	private Bitmap thumbnail;

	public VideoMarker(Uri videoUri, Marker marker, Bitmap thumb) {
		super();
		this.videoUri = videoUri;
		this.marker = marker;
		this.thumbnail = thumb;
	}
	public Uri getVideoUri() {
		return videoUri;
	}
	public void setVideoUri(Uri videoUri) {
		this.videoUri = videoUri;
	}
	public Marker getMarker() {
		return marker;
	}
	public void setMarker(Marker marker) {
		this.marker = marker;
	}
	public Bitmap getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(Bitmap thumbnail) {
		this.thumbnail = thumbnail;
	}

}
