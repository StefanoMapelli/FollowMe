package com.followme.activity;

import java.util.List;

import com.followme.manager.MapManager;
import com.followme.manager.ParseManager;
import com.followme.object.Position;
import com.followme.object.Request;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class SharingReceiverActivity extends ActionBarActivity {
	
	private Request request;
	private ParseObject path;
	private int counterPosition;
	private GoogleMap map;
	private List<Position> positionList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sharing_receiver_layout);
		FragmentManager fm = getSupportFragmentManager();
		map = ((SupportMapFragment) fm.findFragmentById(R.id.mapSharingReceiver)).getMap();
		
		request = (Request) getIntent().getSerializableExtra("acceptedRequest");
		counterPosition=-1;
		
		//recupero l'id del percorso relativo alla richiesta
		path=ParseManager.getPathOfRequest(this, request);
		
		//verifico che ci siano posizioni relative alla mia request su parse
		if(path!=null)
		{
			//parte il thread per il controllo delle nuove posizioni nel db
			new FindNewPositions().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else
		{
			finish();
		}
		
	}
	
	
	
	private class FindNewPositions extends AsyncTask<Void, Integer, String>
    {

		@Override
		protected String doInBackground(Void... params) 
		{				
			while(true)
			{	
				//ricerco le nuove posizioni
				positionList=ParseManager.getNewSharedPosition(SharingReceiverActivity.this, path, counterPosition);				
				publishProgress(0);
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress) 
		{
			if(positionList.size()>0)
			{
				CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(positionList.get(positionList.size()-1).getLatitude(),
									positionList.get(positionList.size()-1).getLongitude()))
				.zoom(18)
				.bearing(0)           
				.tilt(0)             
				.build();
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				
				MapManager.drawPolygonPath(positionList, map);
				counterPosition=positionList.get(positionList.size()-1).getCounter();
			}
	    }
    }
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sharing_receiver, menu);
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
}
