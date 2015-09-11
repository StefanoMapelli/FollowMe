package com.followme.activity;

import com.followme.activity.R;
import com.followme.manager.MapManager;
import com.followme.manager.ParseManager;
import com.followme.object.Destination;
import com.followme.object.Fence;
import com.followme.object.Request;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class ShowDetailsActivity extends ActionBarActivity {
	
	private Request request;
	private ParseObject fenceParseObject;
	private ParseObject destinationParseObject;
	private Destination destination;
	private Fence fence;
	private int radius;
	private LatLng center;
	private GoogleMap map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_details_layout);
		FragmentManager fm = getSupportFragmentManager();
		map = ((SupportMapFragment) fm.findFragmentById(R.id.detailsMap)).getMap();		
		request = (Request) getIntent().getSerializableExtra("requestShow");	
		String typeOfRequest = request.getType();
		
		//valutiamo il tipo di richiesta
		//-condivisione
		//-percorso
		//-recinto
		//-destinazione
		switch(typeOfRequest) 
		{	      	      
	    case "Fence":
		//apro activity recinto
	    	fenceParseObject=ParseManager.getFenceOfRequest(this, request);
	    	if(fenceParseObject == null)
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
				
				//posiziono la camera nel luogo dove mi trovo sulla mappa
				CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(center)
				.zoom(17)
				.bearing(0)           
				.tilt(0)             
				.build();
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				//disegno il recinto sulla mappa
				MapManager.drawFenceCircle(center, radius, map);
	    	}

	    	break;

	    case "Destination":
	    	//apro activity destinazione
	    	destinationParseObject=ParseManager.getDestinationOfRequest(this, request);

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

	    		//posiziono la camera nel luogo dove si trova la destinazione sulla mappa
	    		CameraPosition cameraPosition1 = new CameraPosition.Builder()
	    		.target(center)
	    		.zoom(17)
	    		.bearing(0)           
	    		.tilt(0)             
	    		.build();
	    		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
	    		//disegno la destinazione sulla mappa
	    		MapManager.drawDestinationCircle(center, radius, map);
	    	}
			break;
		}	    
	}

	//onClickHandler quando viene accettata una richiesta
	public void acceptRequestOnClickHandler(View v) 
	{
		String typeOfRequest = request.getType();
		Intent intent;
		//valutiamo il tipo di richiesta
		switch(typeOfRequest) 
		{	      	      
		case "Fence":
		//apro activity recinto
		    Log.i("typeOfRequest","recinto");
		    intent = new Intent(ShowDetailsActivity.this,FenceReceiverActivity.class);
			intent.putExtra("acceptedRequest", request);
			startActivity(intent);
		    break;
			  
		case "Destination":
		//apro activity destinazione
		    Log.i("typeOfRequest","destinazione");
		    intent = new Intent(ShowDetailsActivity.this,DestinationReceiverActivity.class);
			intent.putExtra("acceptedRequest", request);
			startActivity(intent);
		    break;
		}
	}
		
	//onClickHandler quando viene rifiutata una richiesta
	public void declineRequestOnClickHandler(View v) 
	{				
		//eliminare la richiesta da parse db
		if(!ParseManager.deleteRequest(this, request.getId()))
		{
			Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
		}
		else
		{
			Intent intent = new Intent();
			intent.putExtra("requestToRemove", request);
			setResult(RESULT_OK, intent);
			finish();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		return super.onOptionsItemSelected(item);
	}
}
