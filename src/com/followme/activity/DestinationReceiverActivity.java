package com.followme.activity;

import com.followme.manager.MapManager;
import com.followme.manager.ParseManager;
import com.followme.manager.Utils;
import com.followme.object.Destination;
import com.followme.object.Request;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class DestinationReceiverActivity extends ActionBarActivity {
	
	private Request destinationRequest;
	private ParseObject destinationParseObject;
	private Destination destination;
	private int radius;
	private LatLng center;
	private LocationManager locationManager=null;  
	private LocationListener locationListener=null;  
	private Location myLocation = null;
	private GoogleMap map;
	private Circle destinationCircle;
	private CheckDestinationStatus checkDestinationThread;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.destination_receiver_layout);
		
		handler=new Handler();
		destinationRequest=(Request) getIntent().getSerializableExtra("acceptedRequest");
		destinationParseObject=ParseManager.getDestinationOfRequest(this, destinationRequest);
		destination=new Destination(
				(int) destinationParseObject.getDouble("raggio"),
				null,
				new LatLng(destinationParseObject.getParseGeoPoint("posizione").getLatitude(),
						destinationParseObject.getParseGeoPoint("posizione").getLongitude()),
				destinationParseObject.getObjectId(),
				true);
		radius=destination.getRadius();
		center=destination.getCenter();
		
		FragmentManager fm = getSupportFragmentManager();
		map = ((SupportMapFragment) fm.findFragmentById(R.id.mapDestinationReceiver)).getMap();
		
		if (Utils.displayGpsStatus(this)) 
		{
			locationListener = new MyLocationListener(); 
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager  
		    .GPS_PROVIDER, 5000, 10,locationListener); 
			map.setMyLocationEnabled(true);
			
			//posiziono la camera nel luogo dove si trova la destinazione sulla mappa
			CameraPosition cameraPosition = new CameraPosition.Builder()
			.target(center)
			.zoom(17)
			.bearing(0)           
			.tilt(0)             
			.build();
			map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


			//disegno la destinazione sulla mappa
			destinationCircle=MapManager.drawDestinationCircle(center, radius, map);
		} 
		else
		{
			Log.i("GPS", "gps not enabled");
		}
		
		checkDestinationThread = new CheckDestinationStatus();
		checkDestinationThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.destination_receiver, menu);
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
	
	
	private class CheckDestinationStatus extends AsyncTask<Void, Integer, String>
    {

		@Override
		protected String doInBackground(Void... params) 
		{				
			
			while(true)
			{	
				float[] results=new float[1];
				myLocation=MapManager.getLastKnownLocation(DestinationReceiverActivity.this, locationManager);
				Location.distanceBetween(myLocation.getLatitude(), myLocation.getLongitude(), center.latitude, center.longitude, results);
				
				if(results[0]<radius+1 && !destination.isInTheDestination())
				{
					ParseManager.updateDestinationStatus(DestinationReceiverActivity.this, destinationParseObject, true);
					destination.setInTheDestination(true);
					handler.post(new Runnable() {
						@Override
						public void run() 
						{
							destinationCircle.setFillColor(Color.RED);
							Toast.makeText(DestinationReceiverActivity.this, "You are arrived!!!",Toast.LENGTH_LONG).show();
						
						}
					});
				}
				else if(results[0]>radius+1 && destination.isInTheDestination())
				{
					ParseManager.updateDestinationStatus(DestinationReceiverActivity.this, destinationParseObject, false);
					handler.post(new Runnable() {
						@Override
						public void run() 
						{
							destination.setInTheDestination(false);
							destinationCircle.setFillColor(Color.GREEN);
						}
					});
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
