package com.followme.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.followme.manager.MapManager;
import com.followme.manager.ParseManager;
import com.followme.manager.PersonalDataManager;
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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class PathControlActivity extends ActionBarSuperClassActivity {

	private GoogleMap map;
	private List<ParseObject> pathObjects = new ArrayList<ParseObject>();
	private List<ArrayList<Position>> paths = new ArrayList<ArrayList<Position>>(); 
	private List<Integer> counterPositions = new ArrayList<Integer>();
	private List<Contact> contactsList = new ArrayList<Contact>();	
	private List<Marker> markers = new ArrayList<Marker>();
	private boolean autofocus = true;
	private int focus = -1;
	private Handler handler;
	private FindNewPositions findNewPositionsThread;
	private Menu optionsMenu;
	private ArrayList<String> requestIdList=new ArrayList<String>();
	private int finishMode=1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_path_control);
		
		PersonalDataManager.insertOrUpdateCurrentActivity("PathControl");
		
		handler=new Handler();
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.followPathMap)).getMap();
		
		//recupero dati dall'intent
		Object[] objects = (Object[]) getIntent().getSerializableExtra("selectedContacts");		
		//aggiungo i dati alla lista nell view
		for(int i=0; i < objects.length; i++)
		{
			contactsList.add((Contact) objects[i]);
			
		}
		String userId = getIntent().getStringExtra("userId");

		for(Contact c : contactsList)
		{
			//creazione dei path
			String pathId = ParseManager.insertPath(this);
			if(pathId == null)
			{
				Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
			}
			else
			{
				ParseObject path = ParseManager.getPathbyId(this, pathId);
				if(path == null)
				{
					Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
				}
				else
				{
					pathObjects.add(path);
					//inizializzazione arraylist
					paths.add(new ArrayList<Position>());
					counterPositions.add(-1);
					//invio delle richieste e salvataggio nell'array list
					String outcomeId = ParseManager.insertRequest(this, "percorso", userId, contactsList.get(contactsList.indexOf(c)).getId(), pathId, null, null);
					if(outcomeId == null)
					{
						Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
					}
					else
					{
						requestIdList.add(outcomeId);
					}
				}
			}
		}
		findNewPositionsThread = new FindNewPositions();
		findNewPositionsThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.path_control, menu);
		optionsMenu = menu;
		
		Menu focusMenu = menu.addSubMenu("Change focus");
		int i = 0;
		for(Contact c : contactsList)
		{
			focusMenu.add(1, i, i, "Focus on "+c.getName());
			i++;
		}
		return true;
	}
	
	
	
	@Override
	public void onBackPressed()
	{
		findNewPositionsThread.cancel(true);
		new AlertDialog.Builder(this)
	    .setTitle("Attention")
	    .setMessage("Are you sure you want to stop follow the users?")
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which)
	        { 
	        	Toast.makeText(PathControlActivity.this, "You stop follow!",Toast.LENGTH_LONG).show();
	            finishMode=2;
	        	finish();
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) 
	        { 
	        	findNewPositionsThread=new FindNewPositions();
	        	findNewPositionsThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
			for(int i=0; i<requestIdList.size();i++)
			{
				if(!ParseManager.deleteRequestAndFollowPath(
						this,
						(String) requestIdList.get(i), pathObjects.get(i)))
				{
					Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
				}
			}
		}
		else
		{
			for(int i=0; i<requestIdList.size();i++)
			{
				ParseManager.updateRequestStatusById(this, (String)requestIdList.get(i), "chiusa");
			}
			
		}
		Intent intent = new Intent(PathControlActivity.this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId())
		{
		case R.id.action_autoFocus:
		{
			if(!autofocus)
			{
				autofocus = true;
				item.setTitle("Disable autofocus");
				Toast.makeText(this, "Autofocus enabled!",Toast.LENGTH_SHORT).show();
			}
			else
			{
				autofocus = false;
				item.setTitle("Enable autofocus");
				Toast.makeText(this, "Autofocus disabled!",Toast.LENGTH_SHORT).show();
			}
			break;
		}
		}
		
		if(item.getGroupId() == 1)
		{
			autofocus = false;
			optionsMenu.getItem(1).setTitle("Enable autofocus");
			Toast.makeText(this, "Autofocus disabled!",Toast.LENGTH_SHORT).show();
			focus = item.getItemId();			
			if(paths.get(focus).size() > 0)
			{
				CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(paths.get(focus).get(paths.get(focus).size()-1).getLatitude(),
						paths.get(focus).get(paths.get(focus).size()-1).getLongitude()))
				.zoom(18)
				.bearing(0)           
				.tilt(0)             
				.build();
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			}			
			Toast.makeText(this, "Focus changed! Now is on "+contactsList.get(focus).getName(),Toast.LENGTH_SHORT).show();
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
				if(isCancelled())
					return null;
				
				handler.post(new Runnable() {
					@Override
					public void run() 
					{
						int i = 0;
						for(ParseObject path : pathObjects)
						{
						//ricerco le nuove posizioni
						List<Position> newPositions = ParseManager.getNewPosition(PathControlActivity.this, path, counterPositions.get(i));
						
						if(newPositions == null)
						{
							Toast.makeText(PathControlActivity.this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
						}
						else
						{

							paths.get(i).addAll(newPositions);		
								if(newPositions.size()>0)
								{
									if(autofocus)
									{
										CameraPosition cameraPosition = new CameraPosition.Builder()
										.target(new LatLng(paths.get(i).get(paths.get(i).size()-1).getLatitude(),
												paths.get(i).get(paths.get(i).size()-1).getLongitude()))
										.zoom(18)
										.bearing(0)           
										.tilt(0)             
										.build();
										map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
									}
									else
									{
										if(i == focus)
										{
											CameraPosition cameraPosition = new CameraPosition.Builder()
											.target(new LatLng(paths.get(i).get(paths.get(i).size()-1).getLatitude(),
													paths.get(i).get(paths.get(i).size()-1).getLongitude()))
											.zoom(18)
											.bearing(0)           
											.tilt(0)             
											.build();
											map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
										}
									}
									
									int color = Utils.generateColor(i);
									MapManager.drawPolygonPath(color,paths.get(i), map);
									
									counterPositions.set(i, paths.get(i).get(paths.get(i).size()-1).getCounter());
									
									//add marker
									if(markers.size() > i)
									{
										markers.get(i).remove();
									}
						        	Marker m = map.addMarker(new MarkerOptions()
						            .position(new LatLng(paths.get(i).get(paths.get(i).size()-1).getLatitude(),
						            		paths.get(i).get(paths.get(i).size()-1).getLongitude()))
						            .snippet(contactsList.get(i).getPhoneNumber())
						            .icon(BitmapDescriptorFactory.defaultMarker(Utils.convertColor(color)))
						            .title(contactsList.get(i).getName()));
							        map.getUiSettings().setMapToolbarEnabled(false);
							        
							        if(markers.size() > i)
									{
							        	markers.set(i, m);
									}
							        else
							        {
							        	markers.add(m);
							        }
								}
								i++;
							}
						}						
					}
				});	
				
				Iterator<String> iterator = requestIdList.iterator();
				
				while(iterator.hasNext())
				{
					String idReq=iterator.next();
					Boolean isActive=ParseManager.isRequestActive(PathControlActivity.this, idReq);

					if(isActive == null)					
					{
						handler.post(new Runnable() {
							@Override
							public void run() 
							{
								Toast.makeText(PathControlActivity.this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
							}
						});
					}
					else
					{
						if(!isActive)
						{
							final int j=requestIdList.indexOf(idReq);
							//notificare all'utente che l'activity è stata chiusa dallo user
							handler.post(new Runnable() {
								@Override
								public void run() 
								{
									Toast.makeText(PathControlActivity.this, contactsList.get(j).getName() +" stops the follow activity",Toast.LENGTH_LONG).show();
								}
							});

							//cancello dalle liste ogni volta che una richiesta viene chiusa
							if(!ParseManager.deleteRequestAndFollowPath(PathControlActivity.this, idReq, pathObjects.get(j)))
							{
								handler.post(new Runnable() {
									@Override
									public void run() 
									{
										Toast.makeText(PathControlActivity.this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
									}
								});
							}
							else
							{
								pathObjects.remove(j);
								iterator.remove();

								//se non ci sono più follow chiudo l'activity
								if(requestIdList.isEmpty())
								{
									finishMode=1;
									finish();
								}
							}
						}
					}
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
