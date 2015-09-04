package com.followme.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.followme.adapter.DestinationCustomAdapter;
import com.followme.manager.ParseManager;
import com.followme.manager.PersonalDataManager;
import com.followme.object.Contact;
import com.followme.object.Destination;
import com.google.android.gms.maps.model.LatLng;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class DestinationControlActivity extends ActionBarActivity {
	
	private ListView listViewDestination;
	private List<Destination> destinationList=new ArrayList<Destination>();
	private DestinationCustomAdapter adapter;
	private CheckDestinations checkDestinationsThread;
	private Handler handler;
	private int finishMode=1;
	private ArrayList<String> requestIdList=new ArrayList<String>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.destination_control_layout);
		
		listViewDestination = (ListView) this.findViewById(R.id.destinationControlList);
		handler=new Handler();
		
		//recupero i dati dall'intent
		Object[] objects = (Object[]) getIntent().getSerializableExtra("contactsList");
		Object[] requestIdArray=(Object[]) getIntent().getSerializableExtra("requestIdList");

		//aggiungo i dati alla lista nell view
		for(int i=0; i < requestIdArray.length; i++)
		{
			requestIdList.add((String) requestIdArray[i]);
		}

		int radius  = getIntent().getIntExtra("radius", 0);
		Object[] destinationIdList =(Object[])getIntent().getSerializableExtra("destinationIdList");
		double destinationLatitude=getIntent().getDoubleExtra("destinationLatitude",0);
		double destinationLongitude=getIntent().getDoubleExtra("destinationLongitude",0);
		LatLng position=new LatLng(destinationLatitude,destinationLongitude);
		List<Contact> contactList = new ArrayList<Contact>();
		
		//aggiungo i dati alla lista nell view
		for(int i=0; i < objects.length; i++)
		{
			contactList.add((Contact) objects[i]);
		}

		for(int i=0; i < contactList.size(); i++)
		{
			destinationList.add(new Destination(radius,contactList.get(i),position,(String)destinationIdList[i], false));			
		}

		//set the adapter
		adapter = new DestinationCustomAdapter(this, destinationList);
		listViewDestination.setAdapter(adapter);
		checkDestinationsThread=new CheckDestinations();
		checkDestinationsThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);	
	}

	
	@Override
	public void onBackPressed()
	{
		checkDestinationsThread.cancel(true);
		new AlertDialog.Builder(this)
	    .setTitle("Attention")
	    .setMessage("Are you sure you want to destroy the destination?")
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which)
	        { 
	        	Toast.makeText(DestinationControlActivity.this, "Destination destroyed",Toast.LENGTH_LONG).show();
	            finishMode=2;
	        	finish();
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) 
	        { 
	        	checkDestinationsThread=new CheckDestinations();
	        	checkDestinationsThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
				ParseManager.deleteRequestAndDestination(
						this,
						(String) requestIdList.get(i),
						ParseManager.getDestinationbyId(this,
								destinationList.get(i).getIdDestination()));
			}
		}
		else
		{
			for(int i=0; i<requestIdList.size();i++)
			{
				ParseManager.updateRequestStatusById(this, (String)requestIdList.get(i), "chiusa");
			}
			
		}
		Intent intent = new Intent(DestinationControlActivity.this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
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
				if(isCancelled())
					return null;
				
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
				
				Iterator<String> iterator = requestIdList.iterator();
				
				while(iterator.hasNext())
				{
					String idReq=iterator.next();
					boolean isActive=ParseManager.isRequestActive(DestinationControlActivity.this, idReq);
					
					if(!isActive)
					{
						final int j=requestIdList.indexOf(idReq);
						//notificare all'utente che l'activity è stata chiusa dallo user
						handler.post(new Runnable() {
							@Override
							public void run() 
							{
								Toast.makeText(DestinationControlActivity.this, PersonalDataManager.getNameOfContact(destinationList.get(j).getUser().getPhoneNumber())+" close the destination",Toast.LENGTH_LONG).show();
							}
						});
						
						//cancello dalle liste ogni volta che una richiesta viene chiusa
						ParseManager.deleteRequestAndDestination(DestinationControlActivity.this, idReq, ParseManager.getDestinationbyId(DestinationControlActivity.this,destinationList.get(j).getIdDestination()));
						destinationList.remove(j);
						iterator.remove();
						
						//aggiorno l'adapter
						adapter = new DestinationCustomAdapter(DestinationControlActivity.this, destinationList);

						handler.post(new Runnable() {
							@Override
							public void run() 
							{
								listViewDestination.setAdapter(adapter);
							}
						});
						
						//se non ci sono più destination chiudo l'activity
						if(requestIdList.isEmpty())
						{
							finishMode=1;
							finish();
						}
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
