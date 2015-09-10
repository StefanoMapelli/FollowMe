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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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
	private int finishMode=1;//1 destroyed by follower, 2 destroyed by receiver
	private boolean pausedForGPS=false;
	private boolean dialogShow=false;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.destination_receiver_layout);
		
		handler=new Handler();
		destinationRequest=(Request) getIntent().getSerializableExtra("acceptedRequest");
		destinationParseObject=ParseManager.getDestinationOfRequest(this, destinationRequest);
		
		if(destinationParseObject == null)
		{
			Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
		}
		else
		{
			
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
			checkDestinationThread = new CheckDestinationStatus();
			
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
				
				checkDestinationThread = new CheckDestinationStatus();
				checkDestinationThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} 
			else
			{
				new AlertDialog.Builder(this)
				.setTitle("Attention")
				.setMessage("Your GPS is not enabled. Please enable it now!")
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which)
					{ 
						pausedForGPS=true;
						DestinationReceiverActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));				    
					}
				})
				.setIcon(android.R.drawable.ic_dialog_alert)
				.show();
				dialogShow=true;
			}	
		}	
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		if(pausedForGPS)
		{
			if(Utils.displayGpsStatus(this))
			{
				Toast.makeText(DestinationReceiverActivity.this, "GPS enabled",Toast.LENGTH_LONG).show();
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
				
				checkDestinationThread = new CheckDestinationStatus();
				checkDestinationThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				
				pausedForGPS=false;
			}
			else
			{
				Toast.makeText(this, "GPS not enabled",Toast.LENGTH_LONG).show();

				checkDestinationThread.cancel(true);
				finishMode=2;
				finish();
			}
		}
	}

	@Override
	public void onBackPressed()
	{
		checkDestinationThread.cancel(true);
		new AlertDialog.Builder(this)
	    .setTitle("Attention")
	    .setMessage("Are you sure you want to close the destination?")
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which)
	        { 
	        	Toast.makeText(DestinationReceiverActivity.this, "Your destination is the world",Toast.LENGTH_LONG).show();
	            finishMode=2;
	        	finish();
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) 
	        { 
				checkDestinationThread=new CheckDestinationStatus();
				checkDestinationThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	        }
	     })
	    .setIcon(android.R.drawable.ic_dialog_alert)
	    .show();
		
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if(finishMode==1)
		{			
			if(!ParseManager.deleteRequestAndDestination(this, destinationRequest.getId(), destinationParseObject))
			{
				Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
			}			
		}
		else
		{
			if(!ParseManager.updateRequestStatusById(this, destinationRequest.getId(), "chiusa"))
			{
				Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
			}
		}
		
		locationManager.removeUpdates(locationListener);
		
		Intent intent = new Intent(DestinationReceiverActivity.this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.destination_receiver, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

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
				if(isCancelled())
					return null;
				
				if(!Utils.displayGpsStatus(DestinationReceiverActivity.this)  && !dialogShow)
				{
					handler.post(new Runnable() {
						@Override
						public void run() 
						{
							dialogShow=true;
							new AlertDialog.Builder(DestinationReceiverActivity.this)
							.setTitle("Attention")
							.setMessage("Your GPS is not enabled. Please enable it now!")
							.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which)
								{ 
									pausedForGPS=true;
									dialogShow=false;
									DestinationReceiverActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));				    
								}
							})
							.setIcon(android.R.drawable.ic_dialog_alert)
							.show();
						}
					});
				}
				
				float[] results=new float[1];
				myLocation=MapManager.getLastKnownLocation(DestinationReceiverActivity.this, locationManager);

				if(myLocation != null)
				{
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

				}

				//controllo se la richiesta è stata chiusa dal follower

				Boolean isActive=ParseManager.isRequestActive(DestinationReceiverActivity.this, destinationRequest.getId());

				if(isActive == null)
				{
					handler.post(new Runnable() {
						@Override
						public void run() 
						{
							Toast.makeText(DestinationReceiverActivity.this, "Make sure your internet connection is enabled!",Toast.LENGTH_LONG).show();
						}
					});
				}
				else
				{

					if(!isActive)
					{
						handler.post(new Runnable() {
							@Override
							public void run() 
							{
								//notificare all'utente che l'activity è stata chiusa dal follower
								Toast.makeText(DestinationReceiverActivity.this, "You haven't a destination!!! The follower closes it ",Toast.LENGTH_LONG).show();
							}
						});

						finishMode=1;
						finish();
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
}
