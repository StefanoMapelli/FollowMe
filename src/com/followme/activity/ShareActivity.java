package com.followme.activity;

import com.followme.activity.R;
import com.followme.manager.MapManager;
import com.followme.manager.ParseManager;
import com.followme.object.Contact;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.parse.ParseObject;

import android.support.v7.app.ActionBarActivity;
import android.content.ContentResolver;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem; 

public class ShareActivity extends ActionBarActivity {

	private Contact[] contacts = null;
	private LocationManager locationManager=null;  
	private LocationListener locationListener=null;  
	private Location location = null;
	private ParseObject path;
	private int counter;
	private GoogleMap map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_layout);
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1)).getMap();
		counter = 0;
		Object[] objects = (Object[]) getIntent().getSerializableExtra("selectedContacts");
		contacts = new Contact[objects.length];
		
		//cast from object to contact
		for(int i=0; i < objects.length; i++)
		{
			contacts[i] = (Contact) objects[i];
		}
		
		//create a path and insert requests for the users receivers for that path
		String userId = getIntent().getStringExtra("userId");
		String pathId = ParseManager.insertPath(this);
		path = ParseManager.getPathbyId(this, pathId);
		for(int i=0; i<contacts.length; i++)
		{
			ParseManager.insertRequest(this, "condivisione", userId, contacts[i].getId(), pathId, null, null);
		}
		
		if (displayGpsStatus()) 
		{
			locationListener = new MyLocationListener(); 
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager  
		    .GPS_PROVIDER, 5000, 10,locationListener); 
			
			map.setMyLocationEnabled(true);
		} 
		else
		{
			Log.i("GPS", "gps not enabled");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.share, menu);
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
	
	
	/*----Method to Check GPS is enable or disable ----- */  
	 private Boolean displayGpsStatus() {  
	  ContentResolver contentResolver = getBaseContext()  
	  .getContentResolver();  
	  boolean gpsStatus = Settings.Secure  
	  .isLocationProviderEnabled(contentResolver,   
	  LocationManager.GPS_PROVIDER);  
	  if (gpsStatus) {  
	   return true;  
	  
	  } else {  
	   return false;  
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
				 ParseManager.insertPosition(ShareActivity.this, path, location.getLatitude(), location.getLongitude(), counter);
				 counter++;
			 }
			 else
			 {
				 if(Math.abs(location.getLongitude() - loc.getLongitude()) > 0.00001 ||
				    Math.abs(location.getLatitude() - loc.getLatitude()) > 0.00001)
				 	{
					 	Log.i("GPS", "LOCATION FOUND");
					    ParseManager.insertPosition(ShareActivity.this, path, loc.getLatitude(), loc.getLongitude(), counter);
					    counter++;
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
	     }
	          
	     @Override  
	     public void onProviderDisabled(String provider) {  
	            // TODO Auto-generated method stub           
	     }  
	  
	     @Override  
	     public void onProviderEnabled(String provider) {  
	            // TODO Auto-generated method stub           
	     }  
	  
	     @Override  
	     public void onStatusChanged(String provider,   
	     		int status, Bundle extras) {  
	            // TODO Auto-generated method stub           
	     }  
	 }
}
