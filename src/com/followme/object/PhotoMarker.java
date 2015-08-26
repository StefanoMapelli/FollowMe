package com.followme.object;

import com.google.android.gms.maps.model.Marker;

import android.graphics.Bitmap;

public class PhotoMarker {

	private Bitmap bitmap;
	private String path;
	private Marker marker;
	
	public PhotoMarker(Bitmap bitmap, Marker marker,String path) {
		super();
		this.bitmap = bitmap;
		this.marker = marker;
		this.path = path;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public Marker getMarker() {
		return marker;
	}
	public void setMarker(Marker marker) {
		this.marker = marker;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

}
