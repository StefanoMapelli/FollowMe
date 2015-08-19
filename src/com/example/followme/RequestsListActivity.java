package com.example.followme;


import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class RequestsListActivity extends ActionBarActivity {
	
	private ListView listViewRequests;
	private Request[] requestsItems;
	RequestCustomAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.requests_list_layout);
		
		//requestsList listView setting 
		
		//get listview
		listViewRequests = (ListView) this.findViewById(R.id.requestsList);
	
		Object[] objects = (Object[]) getIntent().getSerializableExtra("incomingRequests");
		requestsItems = new Request[objects.length];
		
		for(int i=0; i < objects.length; i++)
		{
			requestsItems[i] = (Request) objects[i];
		}
		
		//set the adapter
		adapter = new RequestCustomAdapter(this, requestsItems);
		listViewRequests.setAdapter(adapter);
		
		
		
	}
	
	
	//onClickHandler quando viene accettata una richiesta
	public void acceptRequestOnClickHandler(View v) 
	{
		
		//quando viene accettata una richiesta viene mostrata la nuova attivit� a seconda del 
		//tipo di richiesta
		
		Request itemToShow = (Request)v.getTag();
		
		String typeOfRequest = itemToShow.getType();
		
		//valutiamo il tipo di richiesta
		//-condivisione
		//-percorso
		//-recinto
		//-destinazione
		switch(typeOfRequest) 
		{
		
	    case "condivisione":
	    //apro activity condivisione
	    	Log.i("typeOfRequest","condivisione");
	    	return;
	      
	    case "percorso":
	    //apro activity percorso
	    	Log.i("typeOfRequest","percorso");
	    	return;
	      
	    case "recinto":
		//apro activity recinto
	    	Log.i("typeOfRequest","recinto");
	    	return;
		  
	    case "destinazione":
	    //apro activity destinazione
	    	Log.i("typeOfRequest","destinazione");
	    	return;
		}
	    
	  }
	
	//onClickHandler quando viene rifiutata una richiesta
	public void declineRequestOnClickHandler(View v) {
		
		//quando viene rifiutata una richiesta essa viene tolta dalla lista delle richieste mostrate
		Request itemToRemove = (Request)v.getTag();
		//eliminare la richiesta da parse db
		ParseManager.deleteRequest(this, itemToRemove.getId());
		//rimuovere dalla listview
		adapter.remove(itemToRemove);

	}
	
	//onClickHandler quando vengono aperti i dettagli di una richiesta
	public void showDetailsRequestOnClickHandler(View v) 
	{
		//quando apriamo i dettagli di una richiesta si apre la nuova attivit� che mostra i dettagli
		Request itemToShow = (Request)v.getTag();
		
		Intent intent = new Intent(RequestsListActivity.this,ShowDetailsActivity.class);
		intent.putExtra("requestShow", itemToShow);
		startActivity(intent);
		
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.requests_list, menu);
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
