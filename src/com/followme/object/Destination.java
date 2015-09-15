package com.followme.object;

import java.io.Serializable;

import com.google.android.gms.maps.model.LatLng;

public class Destination implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5599732300461906164L;
	private String idDestination;
	private int radius;
	private Contact user;
	private double centerLatitude;
	private double centerLongitude;
	private boolean inTheDestination;
	private Boolean statusAccepted;
	
	
	public Destination(int radius, Contact user, LatLng center, String idDestination, boolean inTheDestination, Boolean statusAccepted) {
		super();
		this.radius = radius;
		this.user = user;
		this.centerLatitude = center.latitude;
		this.centerLongitude = center.longitude;
		this.idDestination=idDestination;
		this.setInTheDestination(inTheDestination);
		this.statusAccepted = statusAccepted;
	}
	
	public Destination() {
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

	public String getIdDestination() {
		return idDestination;
	}

	public void setIdDestination(String idDestination) {
		this.idDestination = idDestination;
	}

	public boolean isInTheDestination() {
		return inTheDestination;
	}

	public void setInTheDestination(boolean inTheDestination) {
		this.inTheDestination = inTheDestination;
	}

	public Boolean getStatusAccepted() {
		return statusAccepted;
	}

	public void setStatusAccepted(Boolean statusAccepted) {
		this.statusAccepted = statusAccepted;
	}

}
