package com.followme.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DestinationSettingActivity extends ActionBarSuperClassActivity {
	
	private GoogleMap map;
	private LocationManager locationManager=null;  
	private LocationListener locationListener=null;  
	private Location myLocation = null;
	private LatLng destinationPosition;
	private Circle destinationCircle=null;
	private int radius=60;
	private EditText radiusLabel;
	private Contact[] contactsList;
	private String userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.destination_setting_layout);
		
		FragmentManager fm = getSupportFragmentManager();
		map = ((SupportMapFragment) fm.findFragmentById(R.id.mapDestinationSetting)).getMap();
		
		radiusLabel=(EditText) findViewById(R.id.radiusDestinationLabel);
		
		//recupero i contatti selezionati per la follow e lo user
		Object[] objects = (Object[]) getIntent().getSerializableExtra("selectedContacts");
		contactsList = new Contact[objects.length];
		//cast from object to contact
		for(int i=0; i < objects.length; i++)
		{
			contactsList[i] = (Contact) objects[i];
		}
		userId = getIntent().getStringExtra("userId");
		
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
		//della nostra destinazione

		map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng point) {

				if(destinationCircle!=null)
				{
					//elimino il precedente circle disegnato sulla mappa
					destinationCircle.remove();
				}
				destinationPosition=point;
				destinationCircle=MapManager.drawDestinationCircle(destinationPosition, radius, map);
				radiusLabel.setText(radius+"");

			}
		});
		
		Toast.makeText(this, "Hold tap to create your destination on the map",Toast.LENGTH_LONG).show();
		
		
		radiusLabel.addTextChangedListener(new TextWatcher(){
	        public void afterTextChanged(Editable s) {
	            
	        	if(s.toString().compareTo("")!=0)
	        	{
	        		
	        		radius=Integer.parseInt(s.toString());
	        		
		        	if(destinationCircle!=null && (radius<1000000 && radius>25))
		    		{
		        		destinationCircle.setRadius(radius);
		    		}
	        	}
	        	else
	        	{
	        		radius=30;
		        	if(destinationCircle!=null)
		    		{
		        		destinationCircle.setRadius(radius);
		    		}
	        	}
	        	
	        }


	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}

		}); 
	}

	//evento click per decrementare il raggio del fence
	public void decRadiusDestinationOnClickHandler(View v) 
	{

		if(destinationCircle!=null)
		{
			if(radius>30)
			{
				radius--;
			}
			destinationCircle.setRadius(radius);
			radiusLabel.setText(radius+"");
		}
	}

	//evento click per inrementare il raggio del fence
	public void incRadiusDestinationOnClickHandler(View v)
	{

		if(destinationCircle!=null)
		{
			if(radius<1000000)
			{
				radius++;
			}
			destinationCircle.setRadius(radius);
			radiusLabel.setText(radius+"");
		}
	}
	
	//evento per mandare una richiesta una volta settato il fence
	public void sendDestinationRequestOnClickHandler(View v)
	{
		if(destinationCircle!=null)
		{
			String[] destinationIdList=new String[contactsList.length];
			String[] requestIdList=new String[contactsList.length];
			
			for(int i=0; i<contactsList.length; i++)
			{
				String destId =ParseManager.insertDestination(this, radius, destinationPosition);
				if(destId == null)
				{
					Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
				}
				else
				{
					destinationIdList[i] = destId;
					String outcomeId = ParseManager.insertRequest(this, "destinazione", userId, contactsList[i].getId(), null, destinationIdList[i], null);
					if(outcomeId == null)
					{
						Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
					}
					else
					{
						requestIdList[i] = outcomeId;
					}
				}				
			}

			Intent intent = new Intent(DestinationSettingActivity.this,DestinationControlActivity.class);
			intent.putExtra("destinationLatitude", destinationPosition.latitude);
			intent.putExtra("destinationLongitude", destinationPosition.longitude);
			intent.putExtra("radius", radius);
			intent.putExtra("contactsList", contactsList);
			intent.putExtra("destinationIdList", destinationIdList);
			intent.putExtra("requestIdList", requestIdList);
			startActivity(intent);
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.destination_setting, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
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
