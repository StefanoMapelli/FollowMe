package com.followme.activity;

import com.followme.manager.MapManager;
import com.followme.manager.ParseManager;
import com.followme.manager.PersonalDataManager;
import com.followme.manager.Utils;
import com.followme.object.Fence;
import com.followme.object.Request;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;

import android.support.v4.app.FragmentManager;
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

public class FenceReceiverActivity extends ActionBarSuperClassActivity {
	
	private Request fenceRequest;
	private ParseObject fenceParseObject;
	private Fence fence;
	private int radius;
	private LatLng center;
	private LocationManager locationManager=null;  
	private LocationListener locationListener=null;  
	private Location myLocation = null;
	private GoogleMap map;
	private Circle fenceCircle;
	private CheckFenceStatus checkFenceThread;
	private Handler handler;
	private int finishMode=1; //1 destroyed by follower, 2 destroyed by receiver
	private boolean pausedForGPS=false;
	private boolean dialogShow=false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fence_receiver_layout);
		
		PersonalDataManager.insertOrUpdateCurrentActivity("FenceReceiver");
		
		handler=new Handler();
		fenceRequest=(Request) getIntent().getSerializableExtra("acceptedRequest");
		fenceParseObject=ParseManager.getFenceOfRequest(this, fenceRequest);
		if(fenceParseObject==null)
		{
			Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
		}
		else
		{
			fence=new Fence(
					(int) fenceParseObject.getDouble("raggio"),
					null,
					new LatLng(fenceParseObject.getParseGeoPoint("posizione").getLatitude(),
							fenceParseObject.getParseGeoPoint("posizione").getLongitude()),
					fenceParseObject.getObjectId(),
					true);
			radius=fence.getRadius();
			center=fence.getCenter();
			
			FragmentManager fm = getSupportFragmentManager();
			map = ((SupportMapFragment) fm.findFragmentById(R.id.mapFenceReceiver)).getMap();
			checkFenceThread = new CheckFenceStatus();
			
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
				fenceCircle=MapManager.drawFenceCircle(center, radius, map);
				
				checkFenceThread = new CheckFenceStatus();
				checkFenceThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
						FenceReceiverActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));				    
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
				Toast.makeText(FenceReceiverActivity.this, "GPS enabled",Toast.LENGTH_LONG).show();
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
				fenceCircle=MapManager.drawFenceCircle(center, radius, map);
				
				checkFenceThread = new CheckFenceStatus();
				checkFenceThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				
				pausedForGPS=false;
			}
			else
			{
				Toast.makeText(this, "GPS not enabled",Toast.LENGTH_LONG).show();

				checkFenceThread.cancel(true);
				finishMode=2;
				finish();
			}
		}		
	}
	
	@Override
	public void onBackPressed()
	{
		checkFenceThread.cancel(true);
		new AlertDialog.Builder(this)
	    .setTitle("Attention")
	    .setMessage("Are you sure you want to destroy the fence?")
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which)
	        { 
	        	Toast.makeText(FenceReceiverActivity.this, "You are free!!! Freedom",Toast.LENGTH_LONG).show();
	            finishMode=2;
	        	finish();
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) 
	        { 
	        	checkFenceThread = new CheckFenceStatus();
	    		checkFenceThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
			if(!ParseManager.deleteRequestAndFence(this, fenceRequest.getId(), fenceParseObject))
			{
				Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
			}
		}
		else
		{
			ParseManager.updateRequestStatusById(this, fenceRequest.getId(), "chiusa");
		}
		
		locationManager.removeUpdates(locationListener);
		
		Intent intent = new Intent(FenceReceiverActivity.this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fence_receiver, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(item.getItemId() == R.id.fenceZoomButton)
		{
			CameraPosition cameraPosition = new CameraPosition.Builder()
			.target(center)
			.zoom(17)
			.bearing(0)           
			.tilt(0)             
			.build();
			map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
	
	
	private class CheckFenceStatus extends AsyncTask<Void, Integer, String>
    {

		@Override
		protected String doInBackground(Void... params) 
		{							
			while(true)
			{	
				if(isCancelled())
					return null;
				
				if(!Utils.displayGpsStatus(FenceReceiverActivity.this) && !dialogShow)
				{
					handler.post(new Runnable() {
						@Override
						public void run() 
						{
							dialogShow=true;
							new AlertDialog.Builder(FenceReceiverActivity.this)
							.setTitle("Attention")
							.setMessage("Your GPS is not enabled. Please enable it now!")
							.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which)
								{ 
									pausedForGPS=true;
									dialogShow=false;
									FenceReceiverActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));				    
								}
							})
							.setIcon(android.R.drawable.ic_dialog_alert)
							.show();
						}
					});
				}
				
				float[] results=new float[1];
				myLocation=MapManager.getLastKnownLocation(FenceReceiverActivity.this, locationManager);
				if(myLocation != null)
				{
					Location.distanceBetween(myLocation.getLatitude(), myLocation.getLongitude(), center.latitude, center.longitude, results);

					if(results[0]>radius+1 && fence.isInTheFence())
					{
						ParseManager.updateFenceStatus(FenceReceiverActivity.this, fenceParseObject, true);
						fence.setInTheFence(false);
						handler.post(new Runnable() {
							@Override
							public void run() 
							{
								fenceCircle.setFillColor(Color.RED);
								Toast.makeText(FenceReceiverActivity.this, "Where are you going? Pay attention to the fence!!!",Toast.LENGTH_LONG).show();

							}
						});

					}
					else if(results[0]<radius && !fence.isInTheFence())
					{
						ParseManager.updateFenceStatus(FenceReceiverActivity.this, fenceParseObject, false);
						handler.post(new Runnable() {
							@Override
							public void run() 
							{
								fence.setInTheFence(true);
								fenceCircle.setFillColor(Color.BLUE);
							}
						});

					}
				}
				//controllo se la richiesta è stata chiusa dal follower

				Boolean isActive=ParseManager.isRequestActive(FenceReceiverActivity.this, fenceRequest.getId());

				if(isActive == null)
				{
					handler.post(new Runnable() {
						@Override
						public void run() 
						{
							Toast.makeText(FenceReceiverActivity.this, "Make sure your internet connection is enabled!",Toast.LENGTH_LONG).show();
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
								Toast.makeText(FenceReceiverActivity.this, "You are free!!! The follower destroy the fence",Toast.LENGTH_LONG).show();
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
