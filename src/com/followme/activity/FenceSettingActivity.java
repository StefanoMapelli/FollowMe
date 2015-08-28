package com.followme.activity;


import com.followme.manager.MapManager;
import com.followme.manager.ParseManager;
import com.followme.object.Contact;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.content.ContentResolver;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class FenceSettingActivity extends ActionBarActivity {
	
	private GoogleMap map;
	private LocationManager locationManager=null;  
	private LocationListener locationListener=null;  
	private Location myLocation = null;
	private LatLng fencePosition;
	private Circle fenceCircle=null;
	private int radius=60;
	private EditText radiusLabel;
	private Contact[] contactsList;
	private String userId;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fence_setting_layout);
		FragmentManager fm = getSupportFragmentManager();
		map = ((SupportMapFragment) fm.findFragmentById(R.id.mapFenceSetting)).getMap();
		
		radiusLabel=(EditText) findViewById(R.id.radiusLabel);			
		
		
		//recupero i contatti selezionati per la follow e lo user
		Object[] objects = (Object[]) getIntent().getSerializableExtra("selectedContacts");
		contactsList = new Contact[objects.length];
		//cast from object to contact
		for(int i=0; i < objects.length; i++)
		{
			contactsList[i] = (Contact) objects[i];
		}
		userId = getIntent().getStringExtra("userId");
		
		if (displayGpsStatus()) 
		{
			locationListener = new MyLocationListener(); 
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager  
		    .GPS_PROVIDER, 5000, 10,locationListener); 
			myLocation=MapManager.getLastKnownLocation(this, locationManager);
			if(myLocation!=null)
			{
				//posiziono la camera nel luogo dove mi trovo sulla mappa
				CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()))
				.zoom(17)
				.bearing(0)           
				.tilt(0)             
				.build();
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));	
			}
			
			
			//quando un utente clicca in modo prolungato sulla mappa la posizione diventa centro 
			//del nostro recinto
			map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

		        @Override
		        public void onMapLongClick(LatLng point) {
		        	
		        	if(fenceCircle!=null)
		        	{
		        		//elimino il precedente circle disegnato sulla mappa
		        		fenceCircle.remove();
		        	}
		        	fencePosition=point;
		        	fenceCircle=MapManager.drawFenceCircle(fencePosition, radius, map);
		        	radiusLabel.setText(radius+"");
		          
		        }
		});
		} 
		else
		{
			Log.i("GPS", "gps not enabled");
		}
		
		Toast.makeText(this, "Hold tap to create your fence on the map",Toast.LENGTH_LONG).show();
		
		
		radiusLabel.addTextChangedListener(new TextWatcher(){
	        public void afterTextChanged(Editable s) {
	            
	        	if(s.toString().compareTo("")!=0)
	        	{
	        		
	        		radius=Integer.parseInt(s.toString());
	        		
		        	if(fenceCircle!=null && (radius<1000000 && radius>25))
		    		{
		    			fenceCircle.setRadius(radius);
		    		}
	        	}
	        	else
	        	{
	        		radius=30;
		        	if(fenceCircle!=null)
		    		{
		    			fenceCircle.setRadius(radius);
		    		}
	        	}
	        	
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}
	    }); 
	}
	
	
	
	
	//evento click per decrementare il raggio del fence
	public void decRadiusFenceOnClickHandler(View v) 
	{
		
		if(fenceCircle!=null)
		{
			if(radius>30)
			{
				radius--;
			}
			fenceCircle.setRadius(radius);
			radiusLabel.setText(radius+"");
		}
	}
	
	
	//evento click per inrementare il raggio del fence
	public void incRadiusFenceOnClickHandler(View v)
	{
		
		if(fenceCircle!=null)
		{
			if(radius<1000000)
			{
				radius++;
			}
			fenceCircle.setRadius(radius);
			radiusLabel.setText(radius+"");
		}
	}
	
	
	//evento per mandare una richiesta una volta settato il fence
	public void sendFenceRequestOnClickHandler(View v)
	{
		if(fenceCircle!=null)
		{
			String fenceId = ParseManager.insertFence(this, radius, fencePosition);
			for(int i=0; i<contactsList.length; i++)
			{
				ParseManager.insertRequest(this, "recinto", userId, contactsList[i].getId(), null, null, fenceId);
			}
			
			Intent intent = new Intent(FenceSettingActivity.this,FenceControlActivity.class);
			intent.putExtra("fenceLatitude", fencePosition.latitude);
			intent.putExtra("fenceLongitude", fencePosition.longitude);
			intent.putExtra("radius", radius);
			intent.putExtra("contactsList", contactsList);
			intent.putExtra("fenceId", fenceId);
			startActivity(intent);
		}
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fence_setting, menu);
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
	
	
	
	/*----------Listener class to get coordinates ------------- */  
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
}
