package com.followme.activity;


import com.followme.manager.MapManager;
import com.followme.manager.ParseManager;
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
	private GoogleMap map;
	private Circle fenceCircle;
	private CheckFenceStatus checkFenceThread;
	private Handler handler;
	private int finishMode=1; //1 destroyed by follower, 2 destroyed by receiver
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fence_receiver_layout);
		
		handler=new Handler();
		fenceRequest=(Request) getIntent().getSerializableExtra("acceptedRequest");
		fenceParseObject=ParseManager.getFenceOfRequest(this, fenceRequest);
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
		} 
		else
		{
			Log.i("GPS", "gps not enabled");
		}
		
		
		checkFenceThread = new CheckFenceStatus();
		checkFenceThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
		
	}
	
	
	@Override
	public void onBackPressed()
	{
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
			checkFenceThread.cancel(true);
			ParseManager.deleteRequestAndFence(this, fenceRequest.getId(), fenceParseObject);
		}
		else
		{
			checkFenceThread.cancel(true);
			ParseManager.updateRequestStatusById(this, fenceRequest.getId(), "chiusa");
		}
		
		Intent intent = new Intent(FenceReceiverActivity.this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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
	
	
	private class CheckFenceStatus extends AsyncTask<Void, Integer, String>
    {

		@Override
		protected String doInBackground(Void... params) 
		{							
			while(true)
			{	
				if(isCancelled())
					return null;
				
				float[] results=new float[1];
				myLocation=MapManager.getLastKnownLocation(FenceReceiverActivity.this, locationManager);
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
				
				//controllo se la richiesta � stata chiusa dal follower

				boolean isActive=ParseManager.isRequestActive(FenceReceiverActivity.this, fenceRequest.getId());
				
				if(!isActive)
				{
					//notificare all'utente che l'activity � stata chiusa dal follower
					Toast.makeText(FenceReceiverActivity.this, "You are free!!! The follower destroy the fence",Toast.LENGTH_LONG).show();
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
