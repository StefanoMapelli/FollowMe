package com.followme.activity;

import com.followme.activity.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class FollowActivity extends ActionBarSuperClassActivity
{
	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.follow_activity_layout);
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFollowActivity)).getMap();
		
		map.getUiSettings().setMapToolbarEnabled(false);
		
		map.getUiSettings().setAllGesturesEnabled(false);
		
		CameraPosition cameraPosition = new CameraPosition.Builder()
		.target(new LatLng(45.4656326,9.1862617))
		.zoom(15)
		.bearing(0)           
		.tilt(0)             
		.build();
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	
		
		//inserimento marker
		map.addMarker(new MarkerOptions()
		.position(new LatLng(45.4644191,9.1819114))
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.fence_marker))
		.title("Fence"));
		
		map.addMarker(new MarkerOptions()
		.position(new LatLng(45.4667066,9.1892231))
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_marker))
		.title("Destination"));
		
		map.addMarker(new MarkerOptions()
		.position(new LatLng(45.4604215,9.1896362))
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.path_marker))
		.title("Path"));
		
		//info window setting
		map.setInfoWindowAdapter(new InfoWindowAdapter() {
			@Override
			public View getInfoWindow(Marker marker) {
				return null;
			}

			@Override
			public View getInfoContents(Marker marker) 
			{
				if(marker.getTitle().compareTo("Fence")==0)
				{
					startFenceOnClickHandler(null);
				}
				else if(marker.getTitle().compareTo("Destination")==0)
				{
					startDestinationOnClickHandler(null);
				}
				else
				{
					startPathOnClickHandler(null);
				}	
				
				return null;
			}
		});	
	}

	//onClickHandler quando viene selezionata la modalità fence
	public void startFenceOnClickHandler(View v) 
	{
		Intent intent = new Intent(FollowActivity.this,FenceSettingActivity.class);
		intent.putExtra("selectedContacts", getIntent().getSerializableExtra("selectedContacts"));
		intent.putExtra("userId", getIntent().getStringExtra("userId"));
		startActivity(intent);
		finish();
	}


	//onClickHandler quando viene selezionata la modalità di destination
	public void startDestinationOnClickHandler(View v) 
	{
		Intent intent = new Intent(FollowActivity.this,DestinationSettingActivity.class);
		intent.putExtra("selectedContacts", getIntent().getSerializableExtra("selectedContacts"));
		intent.putExtra("userId", getIntent().getStringExtra("userId"));
		startActivity(intent);
		finish();
	}


	//onClickHandler quando viene selezionata la modalità di follow path
	public void startPathOnClickHandler(View v) 
	{
		//Intent che manda alla ricezione del percorso del followato
		Intent intent = new Intent(FollowActivity.this,PathControlActivity.class);
		intent.putExtra("selectedContacts", getIntent().getSerializableExtra("selectedContacts"));
		intent.putExtra("userId", getIntent().getStringExtra("userId"));
		startActivity(intent);
		finish();
	}


}