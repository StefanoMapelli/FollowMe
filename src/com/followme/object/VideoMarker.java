package com.followme.object;

import android.net.Uri;

import com.google.android.gms.maps.model.Marker;

public class VideoMarker {

	private Uri videoUri;
	private Marker marker;

	public VideoMarker(Uri videoUri, Marker marker) {
		super();
		this.videoUri = videoUri;
		this.marker = marker;
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

}
