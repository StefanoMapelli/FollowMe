package com.followme.object;

import com.google.android.gms.maps.model.LatLng;

public class Destination {
	
	private String idDestination;
	private int radius;
	private Contact user;
	private LatLng center;
	private boolean inTheDestination;
	
	
	public Destination(int radius, Contact user, LatLng center, String idDestination, boolean inTheDestination) {
		super();
		this.radius = radius;
		this.user = user;
		this.center = center;
		this.idDestination=idDestination;
		this.setInTheDestination(inTheDestination);
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

}
