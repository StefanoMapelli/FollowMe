package com.followme.activity;

import com.followme.manager.MapManager;
import com.followme.manager.ParseManager;
import com.followme.manager.Utils;
import com.followme.object.Request;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;

import android.support.v7.app.ActionBarActivity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class PathReceiverActivity extends ActionBarActivity {

	private ParseObject path;
	private Request pathRequest;
	private int positionCounter = 0;
	private Location location=null;
	private LocationManager locationManager=null;  
	private LocationListener locationListener=null;  
	private GoogleMap map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_path_receiver);
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.followPathReveiverMap)).getMap();
		pathRequest=(Request) getIntent().getSerializableExtra("acceptedRequest");
		path = ParseManager.getPathOfRequest(this, pathRequest);
		
		if (Utils.displayGpsStatus(this)) 
		{
			locationListener = new MyLocationListener(); 
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager  
		    .GPS_PROVIDER, 5000, 10,locationListener); 
			
			location = MapManager.getLastKnownLocation(this, locationManager);
			
			if(location != null)
			{
				Log.i("GPS", "FIRST LOCATION");
				ParseManager.insertPosition(PathReceiverActivity.this, path, location.getLatitude(), location.getLongitude(), positionCounter);						 
				positionCounter++;
				 
				CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(location.getLatitude(),location.getLongitude()))
				.zoom(18)
				.bearing(0)           
				.tilt(0)             
				.build();
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			}
			
			map.setMyLocationEnabled(true);
		} 
		else
		{
			Log.i("GPS", "gps not enabled");
		}
	}

	/*----------Listener class to get coordinates ------------- */  
	 private class MyLocationListener implements LocationListener 
	 {  
		 @Override  
	     public void onLocationChanged(Location loc) 	     
		 {  
			 if(location == null)
				{
					Log.i("GPS", "FIRST LOCATION");
				 	location = loc;
					ParseManager.insertPosition(PathReceiverActivity.this, path, loc.getLatitude(), loc.getLongitude(), positionCounter);					 					 
					positionCounter++;
					 
					CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(loc.getLatitude(),loc.getLongitude()))
					.zoom(18)
					.bearing(0)           
					.tilt(0)             
					.build();
					map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				}
			 else
			 if((Math.abs(location.getLongitude() - loc.getLongitude()) > 0.00001 ||
				  Math.abs(location.getLatitude() - loc.getLatitude()) > 0.00001))
				 {
					Toast.makeText(PathReceiverActivity.this, "speed: "+loc.getSpeed()+" accuracy: "+loc.getAccuracy(), Toast.LENGTH_LONG).show();
					Log.i("GPS", "LOCATION FOUND");
					    
					ParseManager.insertPosition(PathReceiverActivity.this, path, loc.getLatitude(), loc.getLongitude(), positionCounter);					    				    
					positionCounter++;
					    
					CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(loc.getLatitude(),loc.getLongitude()))
					.zoom(18)
					.bearing(0)           
					.tilt(0)             
					.build();
					map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
						
					MapManager.drawPrimaryLinePath(
							new LatLng(location.getLatitude(),location.getLongitude()) ,
							new LatLng(loc.getLatitude(),loc.getLongitude()),
							map);
						
					location = loc;
				} 					 
	     }
		 
		 @Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}
	 }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.path_receiver, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
