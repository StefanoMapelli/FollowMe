package com.followme.activity;

import java.util.ArrayList;
import java.util.List;

import com.followme.adapter.DestinationCustomAdapter;
import com.followme.adapter.FenceCustomAdapter;
import com.followme.manager.ParseManager;
import com.followme.object.Contact;
import com.followme.object.Destination;
import com.followme.object.Fence;
import com.google.android.gms.maps.model.LatLng;

import android.support.v7.app.ActionBarActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class DestinationControlActivity extends ActionBarActivity {
	
	private ListView listViewDestination;
	private List<Destination> destinationList=new ArrayList();
	private DestinationCustomAdapter adapter;
	private CheckDestinations checkDestinationsThread;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.destination_control_layout);
		
		listViewDestination = (ListView) this.findViewById(R.id.destinationControlList);
		handler=new Handler();
		
		//recupero i dati dall'intent
		Object[] objects = (Object[]) getIntent().getSerializableExtra("contactsList");
		int radius  = getIntent().getIntExtra("radius", 0);
		String idDestination=getIntent().getStringExtra("destinationId");
		double destinationLatitude=getIntent().getDoubleExtra("destinationLatitude",0);
		double destinationLongitude=getIntent().getDoubleExtra("destinationLongitude",0);
		LatLng position=new LatLng(destinationLongitude,destinationLongitude);
		List<Contact> contactList = new ArrayList<Contact>();
		
		//aggiungo i dati alla lista nell view
		for(int i=0; i < objects.length; i++)
		{
			contactList.add((Contact) objects[i]);
		}

		for(int i=0; i < contactList.size(); i++)
		{
			destinationList.add(new Destination(radius,contactList.get(i),position,idDestination, false));			
		}

		//set the adapter
		adapter = new DestinationCustomAdapter(this, destinationList);
		listViewDestination.setAdapter(adapter);
		checkDestinationsThread=new CheckDestinations();
		checkDestinationsThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.destination_control, menu);
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
	
	private class CheckDestinations extends AsyncTask<Void, Integer, String>
    {
		
		@Override
		protected String doInBackground(Void... params) 
		{			
			while(true)
			{	
				Destination destinationObject;

				for(int i=0; i<destinationList.size();i++)
				{
					destinationObject = destinationList.get(i);
					if(ParseManager.isInTheDestination(DestinationControlActivity.this, destinationObject.getIdDestination()))
					{
						destinationObject.setInTheDestination(false);
						adapter = new DestinationCustomAdapter(DestinationControlActivity.this, destinationList);

						handler.post(new Runnable() {
							@Override
							public void run() 
							{
								listViewDestination.setAdapter(adapter);
							}
						});
					}
				}

				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
    }
}
