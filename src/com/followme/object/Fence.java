package com.followme.object;

import java.io.Serializable;

import com.google.android.gms.maps.model.LatLng;

public class Fence implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 428402105120732091L;
	private String idFence;
	private int radius;
	private Contact user;
	private double centerLatitude;
	private double centerLongitude;
	private boolean inTheFence;
	private Boolean statusAccepted;
	
	
	public Fence(int radius, Contact user, LatLng center, String idFence, boolean inTheFence, Boolean statusAccepted) {
		super();
		this.radius = radius;
		this.user = user;
		this.centerLatitude = center.latitude;
		this.centerLongitude = center.longitude;
		this.idFence=idFence;
		this.setInTheFence(inTheFence);
		this.statusAccepted = statusAccepted;
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
		return new LatLng(centerLatitude, centerLongitude);
	}
	public void setCenter(LatLng center) {
		this.centerLatitude = center.latitude;
		this.centerLongitude = center.longitude;
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

	public Boolean getStatusAccepted() {
		return statusAccepted;
	}

	public void setStatusAccepted(Boolean statusAccepted) {
		this.statusAccepted = statusAccepted;
	}

}
