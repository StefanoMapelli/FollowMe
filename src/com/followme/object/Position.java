package com.followme.object;

import java.io.Serializable;

public class Position implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -657863294709893140L;
	private String id;
	private String path;
	private double latitude;
	private double longitude;
	private int counter;
	
	public Position(double lat, double lon, int con)
	{
		latitude=lat;
		longitude=lon;
		counter=con;
	}
	
	
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}
	
	
	

}
