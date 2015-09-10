package com.followme.activity;

import com.followme.manager.MapManager;
import com.followme.object.Destination;
import com.followme.object.Fence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ShowFenceOrDestinationActivity extends ActionBarActivity {

	private Destination destination;
	private Fence fence;
	private GoogleMap map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_fence_or_destination_layout);
		FragmentManager fm = getSupportFragmentManager();
		map = ((SupportMapFragment) fm.findFragmentById(R.id.mapShowFenceOrDestination)).getMap();	
		
		Object obj = getIntent().getSerializableExtra("circle");
		
		if(obj instanceof Fence)
		{
			fence = (Fence) obj;
			
			//posiziono la camera nel luogo dove mi trovo sulla mappa
			CameraPosition cameraPosition = new CameraPosition.Builder()
			.target(fence.getCenter())
			.zoom(17)
			.bearing(0)           
			.tilt(0)             
			.build();
			map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


			//disegno il recinto sulla mappa
			MapManager.drawFenceCircle(fence.getCenter(), fence.getRadius(), map);
		}
		else
		{
			destination = (Destination) obj;
			
			//posiziono la camera nel luogo dove si trova la destinazione sulla mappa
			CameraPosition cameraPosition = new CameraPosition.Builder()
			.target(destination.getCenter())
			.zoom(17)
			.bearing(0)           
			.tilt(0)             
			.build();
			map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


			//disegno la destinazione sulla mappa
			MapManager.drawDestinationCircle(destination.getCenter(), destination.getRadius(), map);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_fence_or_destination, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return super.onOptionsItemSelected(item);
	}
}
