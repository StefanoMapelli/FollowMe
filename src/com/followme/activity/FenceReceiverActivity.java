package com.followme.activity;


import com.followme.manager.MapManager;
import com.followme.manager.ParseManager;
import com.followme.manager.Utils;
import com.followme.object.Fence;
import com.followme.object.Position;
import com.followme.object.Request;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;

import android.support.v7.app.ActionBarActivity;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class FenceReceiverActivity extends ActionBarActivity {
	
	private Request fenceRequest;
	private ParseObject fenceParseObject;
	private Fence fence;
	private int radius;
	private LatLng center;
	private LocationManager locationManager=null;  
	private LocationListener locationListener=null;  
	private Location myLocation = null;
	private Position lastPosition = null;
	private GoogleMap map;
	private Circle fenceCircle;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fence_receiver_layout);
		
		fenceRequest=(Request) getIntent().getSerializableExtra("acceptedRequest");
		fenceParseObject=ParseManager.getFenceOfRequest(this, fenceRequest);
		fence=new Fence(
				(int) fenceParseObject.getDouble("raggio"),
				null,
				new LatLng(fenceParseObject.getParseGeoPoint("posizione").getLatitude(),
						fenceParseObject.getParseGeoPoint("posizione").getLongitude()),
				fenceParseObject.getString("objectId"),
				true);
		radius=fence.getRadius();
		center=fence.getCenter();
		
		if (Utils.displayGpsStatus(this)) 
		{
			locationListener = new MyLocationListener(); 
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager  
		    .GPS_PROVIDER, 5000, 10,locationListener); 
			map.setMyLocationEnabled(true);
			
			//posiziono la camera nel luogo dove mi trovo sulla mappa
			CameraPosition cameraPosition = new CameraPosition.Builder()
			.target(center)
			.zoom(17)
			.bearing(0)           
			.tilt(0)             
			.build();
			map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


			//disegno il recinto sulla mappa
			fenceCircle=MapManager.drawCircle(center, radius, map);
		} 
		else
		{
			Log.i("GPS", "gps not enabled");
		}
		
		Toast.makeText(this, "Hold tap to create your fence on the map",Toast.LENGTH_LONG).show();
		
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fence_receiver, menu);
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
	
	
	
	private class MyLocationListener implements LocationListener 
	 {  
		 @Override  
	     public void onLocationChanged(Location loc) 	     
		 {  
			 
	     }
	          
	     @Override  
	     public void onProviderDisabled(String provider) {  
	                      
	     }  
	  
	     @Override  
	     public void onProviderEnabled(String provider) {  
	                    
	     }  
	  
	     @Override  
	     public void onStatusChanged(String provider,   
	     		int status, Bundle extras) {  
	                     
	     }  
	 }
	
	
	private class checkFenceStatus extends AsyncTask<Void, Integer, String>
    {

		@Override
		protected String doInBackground(Void... params) 
		{				
			
			while(true)
			{	
				float[] results=null;
				myLocation=MapManager.getLastKnownLocation(FenceReceiverActivity.this, locationManager);
				Location.distanceBetween(myLocation.getLatitude(), myLocation.getLongitude(), center.latitude, center.longitude, results);
				
				if(results[0]>radius+1 && fence.isInTheFence())
				{
					ParseManager.updateFenceStatus(FenceReceiverActivity.this, fenceParseObject, true);
					fence.setInTheFence(false);
					fenceCircle.setFillColor(Color.RED);
					Toast.makeText(FenceReceiverActivity.this, "Where are you going? Pay attention to the fence!!!",Toast.LENGTH_LONG).show();
				}
				else if(results[0]<radius && !fence.isInTheFence())
				{
					ParseManager.updateFenceStatus(FenceReceiverActivity.this, fenceParseObject, false);
					fence.setInTheFence(true);
					fenceCircle.setFillColor(Color.BLUE);
				}
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
    }
}
