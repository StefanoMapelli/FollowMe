package com.followme.object;

public class Position {
	
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
	
	
	

}
