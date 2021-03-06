package com.followme.activity;

import com.followme.manager.MapManager;
import com.followme.manager.ParseManager;
import com.followme.manager.PersonalDataManager;
import com.followme.manager.Utils;
import com.followme.object.Request;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class PathReceiverActivity extends ActionBarSuperClassActivity {

	private ParseObject path;
	private Request pathRequest;
	private int positionCounter = 0;
	private Location location=null;
	private LocationManager locationManager=null;  
	private LocationListener locationListener=null;  
	private GoogleMap map;
	private Handler handler;
	private int finishMode=1;//1 destroyed by follower, 2 destroyed by receiver
	private CheckRequestPathStatus checkRequestThread;
	private boolean pausedForGPS=false;
	private boolean dialogShow=false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_path_receiver);
		
		PersonalDataManager.insertOrUpdateCurrentActivity("PathReceiver");
		
		handler=new Handler();
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.followPathReveiverMap)).getMap();
		pathRequest=(Request) getIntent().getSerializableExtra("acceptedRequest");
		path = ParseManager.getPathOfRequest(this, pathRequest);
		
		if(path == null)
		{
			Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
		}
		else
		{

			checkRequestThread=new CheckRequestPathStatus();
			
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
					String posId = ParseManager.insertPosition(PathReceiverActivity.this, path, location.getLatitude(), location.getLongitude(), positionCounter);						 
					
					if(posId == null)
					{
						Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
					}
					else
					{
						positionCounter++;
						 
						CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(new LatLng(location.getLatitude(),location.getLongitude()))
						.zoom(18)
						.bearing(0)           
						.tilt(0)             
						.build();
						map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
					}
				}
				else
				{
					Toast.makeText(PathReceiverActivity.this, "Searching for a valid location...",Toast.LENGTH_LONG).show();
				}
				
				map.setMyLocationEnabled(true);
				checkRequestThread=new CheckRequestPathStatus();
				checkRequestThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
						PathReceiverActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));			    
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
				Toast.makeText(PathReceiverActivity.this, "GPS enabled",Toast.LENGTH_LONG).show();
			    locationListener = new MyLocationListener(); 
				locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
				locationManager.requestLocationUpdates(LocationManager  
			    .GPS_PROVIDER, 5000, 10,locationListener); 
				
				location = MapManager.getLastKnownLocation(PathReceiverActivity.this, locationManager);
				
				if(location != null)
				{
					Log.i("GPS", "FIRST LOCATION");
					String id = ParseManager.insertPosition(PathReceiverActivity.this, path, location.getLatitude(), location.getLongitude(), positionCounter);		
					if(id == null)
					{
						Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
					}
					else
					{
						positionCounter++;
						 
						CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(new LatLng(location.getLatitude(),location.getLongitude()))
						.zoom(18)
						.bearing(0)           
						.tilt(0)             
						.build();
						map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
					}					
				}
				else
				{
					Toast.makeText(PathReceiverActivity.this, "Searching for a valid location...",Toast.LENGTH_LONG).show();
				}
				
				map.setMyLocationEnabled(true);
				
				checkRequestThread=new CheckRequestPathStatus();
				checkRequestThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				pausedForGPS=false;
			}
			else
			{
				Toast.makeText(this, "GPS not enabled",Toast.LENGTH_LONG).show();

				checkRequestThread.cancel(true);
				finishMode=2;
				finish();
			}
		}
	}
	
	@Override
	public void onBackPressed()
	{
		checkRequestThread.cancel(true);
		new AlertDialog.Builder(this)
	    .setTitle("Attention")
	    .setMessage("Are you sure you want to stop the activity?")
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which)
	        { 
	        	Toast.makeText(PathReceiverActivity.this, "No one follows you",Toast.LENGTH_LONG).show();
	            finishMode=2;
	        	finish();
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) 
	        { 
	        	checkRequestThread=new CheckRequestPathStatus();
	    		checkRequestThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
			if(!ParseManager.deleteRequestAndFollowPath(this, pathRequest.getId(), path))
			{
				Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
			}
		}
		else
		{
			ParseManager.updateRequestStatusById(this, pathRequest.getId(), "chiusa");
		}
		
		locationManager.removeUpdates(locationListener);
		
		Intent intent = new Intent(PathReceiverActivity.this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
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
					String id = ParseManager.insertPosition(PathReceiverActivity.this, path, loc.getLatitude(), loc.getLongitude(), positionCounter);	
					if(id == null)
					{
						Toast.makeText(PathReceiverActivity.this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
					}
					else
					{
						positionCounter++;
						 
						CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(new LatLng(loc.getLatitude(),loc.getLongitude()))
						.zoom(18)
						.bearing(0)           
						.tilt(0)             
						.build();
						map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
					}
				}
			 else
			 if((Math.abs(location.getLongitude() - loc.getLongitude()) > 0.00001 ||
				  Math.abs(location.getLatitude() - loc.getLatitude()) > 0.00001))
				 {
					Log.i("GPS", "LOCATION FOUND");
					    
					String id = ParseManager.insertPosition(PathReceiverActivity.this, path, loc.getLatitude(), loc.getLongitude(), positionCounter);	
					if(id == null)
					{
						Toast.makeText(PathReceiverActivity.this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
					}
					else
					{
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
		return super.onOptionsItemSelected(item);
	}
	
	
	
	
	private class CheckRequestPathStatus extends AsyncTask<Void, Integer, String>
    {

		@Override
		protected String doInBackground(Void... params) 
		{							
			while(true)
			{		
				if(isCancelled())
					return null;
				//controllo se la richiesta � stata chiusa dal follower

				if(!Utils.displayGpsStatus(PathReceiverActivity.this) && !dialogShow)
				{
					handler.post(new Runnable() {
						@Override
						public void run() 
						{
							dialogShow=true;
							new AlertDialog.Builder(PathReceiverActivity.this)
							.setTitle("Attention")
							.setMessage("Your GPS is not enabled. Please enable it now!")
							.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which)
								{ 
									pausedForGPS=true;
									dialogShow=false;
									PathReceiverActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));				    
								}
							})
							.setIcon(android.R.drawable.ic_dialog_alert)
							.show();
						}
					});
				}
				
				Boolean isActive=ParseManager.isRequestActive(PathReceiverActivity.this, pathRequest.getId());
				
				if(isActive==null)
				{
					handler.post(new Runnable() {
						@Override
						public void run() 
						{
							Toast.makeText(PathReceiverActivity.this, "Make sure your internet connection is enabled!",Toast.LENGTH_LONG).show();
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
								//notificare all'utente che l'activity � stata chiusa dal follower
								Toast.makeText(PathReceiverActivity.this, "You are free!!! No one follows you",Toast.LENGTH_LONG).show();
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
