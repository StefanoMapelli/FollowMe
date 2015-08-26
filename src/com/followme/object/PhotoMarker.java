package com.followme.object;

import com.google.android.gms.maps.model.Marker;

import android.graphics.Bitmap;

public class PhotoMarker {

	private Bitmap bitmap;
	private Marker marker;
	
	public PhotoMarker(Bitmap bitmap, Marker marker) {
		super();
		this.bitmap = bitmap;
		this.marker = marker;
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

}
