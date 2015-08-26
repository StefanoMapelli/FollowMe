package com.followme.object;

import com.google.android.gms.maps.model.LatLng;

public class Fence {
	
	private String idFence;
	private int radius;
	private Contact user;
	private LatLng center;
	private boolean inTheFence;
	
	
	public Fence(int radius, Contact user, LatLng center, String idFence, boolean inTheFence) {
		super();
		this.radius = radius;
		this.user = user;
		this.center = center;
		this.idFence=idFence;
		this.setInTheFence(inTheFence);
	}
	
	public Fence() {
		super();
	}
	
	
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
	public LatLng getCenter() {
		return center;
	}
	public void setCenter(LatLng center) {
		this.center = center;
	}
	public Contact getUser() {
		return user;
	}
	public void setUser(Contact user) {
		this.user = user;
	}

	public String getIdFence() {
		return idFence;
	}

	public void setIdFence(String idFence) {
		this.idFence = idFence;
	}

	public boolean isInTheFence() {
		return inTheFence;
	}

	public void setInTheFence(boolean inTheFence) {
		this.inTheFence = inTheFence;
	}

}
