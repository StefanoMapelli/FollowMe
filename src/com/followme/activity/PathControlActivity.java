package com.followme.activity;

import java.util.ArrayList;
import java.util.List;

import com.followme.manager.MapManager;
import com.followme.manager.ParseManager;
import com.followme.manager.Utils;
import com.followme.object.Contact;
import com.followme.object.Position;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseObject;

import android.support.v7.app.ActionBarActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

public class PathControlActivity extends ActionBarActivity {

	private GoogleMap map;
	private List<ParseObject> pathObjects = new ArrayList<ParseObject>();
	private List<ArrayList<Position>> paths = new ArrayList<ArrayList<Position>>(); 
	private List<Integer> counterPositions = new ArrayList<Integer>();
	private List<Contact> contactsList = new ArrayList<Contact>();	
	private Marker marker;
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_path_control);
		handler=new Handler();
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.followPathMap)).getMap();
		
		//recupero dati dall'intent
		Object[] objects = (Object[]) getIntent().getSerializableExtra("contactsList");		
		//aggiungo i dati alla lista nell view
		for(int i=0; i < objects.length; i++)
		{
			contactsList.add((Contact) objects[i]);
			
		}
		String userId = getIntent().getStringExtra("userId");
		int i = 0;
		for(Contact c : contactsList)
		{
			//creazione dei path
			String pathId = ParseManager.insertPath(this);
			ParseObject path = ParseManager.getPathbyId(this, pathId);
			pathObjects.add(path);
			//inizializzazione arraylist
			ArrayList<Position> arrayList =  paths.get(i);
			arrayList = new ArrayList<Position>(); 
			Integer counterPosition = counterPositions.get(i);
			counterPosition = -1; 
			//invio delle richieste
			ParseManager.insertRequest(this, "percorso", userId, contactsList.get(contactsList.indexOf(c)).getId(), pathId, null, null);
			i++;
		}
		new FindNewPositions().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.path_control, menu);
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
	
	private class FindNewPositions extends AsyncTask<Void, Integer, String>
    {

		@Override
		protected String doInBackground(Void... params) 
		{				
			while(true)
			{	
				handler.post(new Runnable() {
					@Override
					public void run() 
					{
						int i = 0;
						for(ParseObject path : pathObjects)
						{
						//ricerco le nuove posizioni
						List<Position> newPositions = ParseManager.getNewPosition(PathControlActivity.this, path, counterPositions.get(i));
						paths.get(i).addAll(newPositions);		
							if(newPositions.size()>0)
							{
								CameraPosition cameraPosition = new CameraPosition.Builder()
								.target(new LatLng(paths.get(i).get(paths.get(i).size()-1).getLatitude(),
										paths.get(i).get(paths.get(i).size()-1).getLongitude()))
								.zoom(18)
								.bearing(0)           
								.tilt(0)             
								.build();
								map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
								int color = Utils.generateColor(i);
								MapManager.drawPolygonPath(color,paths.get(i), map);
								
								Integer counterPosition = counterPositions.get(i);
								counterPosition = paths.get(i).get(paths.get(i).size()-1).getCounter();
								
								//add marker
								if(marker != null)
								{
									marker.remove();
								}
					        	Marker m = map.addMarker(new MarkerOptions()
					            .position(new LatLng(paths.get(i).get(paths.get(i).size()-1).getLatitude(),
					            		paths.get(i).get(paths.get(i).size()-1).getLongitude()))
					            .snippet(contactsList.get(i).getPhoneNumber())
					            .icon(BitmapDescriptorFactory.defaultMarker(Utils.convertColor(color)))
					            .title(contactsList.get(i).getName()));
						        map.getUiSettings().setMapToolbarEnabled(false);
						        marker=m;
							}
							i++;
						}
					}
				});						
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
    }
}
