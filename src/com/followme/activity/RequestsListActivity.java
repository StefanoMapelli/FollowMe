package com.followme.activity;


import java.util.ArrayList;
import java.util.List;

import com.followme.activity.R;
import com.followme.adapter.RequestCustomAdapter;
import com.followme.manager.ParseManager;
import com.followme.object.Request;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class RequestsListActivity extends ActionBarActivity {
	
	private ListView listViewRequests;
	private List<Request> requestsItems;
	RequestCustomAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.requests_list_layout);
		
		//requestsList listView setting 
		
		//get listview
		listViewRequests = (ListView) this.findViewById(R.id.requestsList);
	
		Object[] objects = (Object[]) getIntent().getSerializableExtra("incomingRequests");
		requestsItems = new ArrayList<Request>();
		
		for(int i=0; i < objects.length; i++)
		{
			requestsItems.add((Request) objects[i]);
		}
		
		//set the adapter
		adapter = new RequestCustomAdapter(this, requestsItems);
		listViewRequests.setAdapter(adapter);		
	}
	
	
	//onClickHandler quando viene accettata una richiesta
	public void acceptRequestOnClickHandler(View v) 
	{
		
		//quando viene accettata una richiesta viene mostrata la nuova attività a seconda del 
		//tipo di richiesta
		
		Intent intent;
		View parentRow = (View) v.getParent();
		int position = listViewRequests.getPositionForView(parentRow);
		Request itemToAccept = requestsItems.get(position);
		
		String typeOfRequest = itemToAccept.getType();
		
		//rimuovere dalla listview
				adapter.remove(itemToAccept);		
		
		//valutiamo il tipo di richiesta
		//-condivisione
		//-percorso
		//-recinto
		//-destinazione
		switch(typeOfRequest) 
		{
		
	    case "condivisione":
	    //apro sharingReceiverActivity
	    	Log.i("typeOfRequest","condivisione");
	    	intent = new Intent(RequestsListActivity.this,SharingReceiverActivity.class);
			intent.putExtra("acceptedRequest", itemToAccept);
			startActivity(intent);
	    	break;
	      
	    case "percorso":
	    //apro activity percorso
	    	Log.i("typeOfRequest","percorso");
	    	intent = new Intent(RequestsListActivity.this,PathReceiverActivity.class);
			intent.putExtra("acceptedRequest", itemToAccept);
			startActivity(intent);
	    	break;
	      
	    case "recinto":
		//apro activity recinto
	    	Log.i("typeOfRequest","recinto");
	    	intent = new Intent(RequestsListActivity.this,FenceReceiverActivity.class);
			intent.putExtra("acceptedRequest", itemToAccept);
			startActivity(intent);
	    	break;
		  
	    case "destinazione":
	    //apro activity destinazione
	    	Log.i("typeOfRequest","destinazione");
	    	intent = new Intent(RequestsListActivity.this,DestinationReceiverActivity.class);
			intent.putExtra("acceptedRequest", itemToAccept);
			startActivity(intent);
	    	break;
		}
	    
	  }
	
	//onClickHandler quando viene rifiutata una richiesta
	public void declineRequestOnClickHandler(View v) {
		
		//quando viene rifiutata una richiesta essa viene tolta dalla lista delle richieste mostrate
		
		View parentRow = (View) v.getParent();
		int position = listViewRequests.getPositionForView(parentRow);
		Request itemToRemove = requestsItems.get(position);
		
		//eliminare la richiesta da parse db
		if(!ParseManager.deleteRequest(this, itemToRemove.getId()))
		{
			Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
		}
		else
		{
			//reset the arraylist
			int i = 0;
			for (Request r : requestsItems)
			{
				if(r.getId().compareTo(itemToRemove.getId())==0)
				{
					requestsItems.remove(i);
					break;
				}
				i++;
			}
			if(requestsItems.isEmpty())
			{
				finish();
			}
			//rimuovere dalla listview
			adapter.remove(itemToRemove);
		}		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		//sta ritornando l'attività first use
		if(requestCode==0)
		{
			if(resultCode==RESULT_OK)
			{
				Request itemToRemove = (Request)data.getSerializableExtra("requestToRemove");
				//rimuovere dalla listview
				//reset the adapter
				int i = 0;
				for (Request r : requestsItems)
				{
					if(r.getId().compareTo(itemToRemove.getId())==0)
					{
						requestsItems.remove(i);
						break;
					}
					i++;
				}
				adapter = new RequestCustomAdapter(this, requestsItems);
				listViewRequests.setAdapter(adapter);	
				
				if(requestsItems.isEmpty())
				{
					finish();
				}
			}
		}
	}
	
	//onClickHandler quando vengono aperti i dettagli di una richiesta
	public void showDetailsRequestOnClickHandler(View v) 
	{
		//quando apriamo i dettagli di una richiesta si apre la nuova attività che mostra i dettagli
		View parentRow = (View) v.getParent();
		int position = listViewRequests.getPositionForView(parentRow);
		Request itemToShow = requestsItems.get(position);
		
		Intent intent = new Intent(RequestsListActivity.this,ShowDetailsActivity.class);
		intent.putExtra("requestShow", itemToShow);
		startActivityForResult(intent,0);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.requests_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return super.onOptionsItemSelected(item);
	}		
}
