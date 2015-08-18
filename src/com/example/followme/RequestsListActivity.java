package com.example.followme;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class RequestsListActivity extends ActionBarActivity {
	
	private ListView listViewRequests;
	private Request[] requestsItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.requests_list_layout);
		
		//requestsList listView setting 
		
		//get listview
		listViewRequests = (ListView) this.findViewById(R.id.requestsList);
	
		ArrayList<Request> requests = new ArrayList<Request>();
		
		Iterator<Request> iter = requests.iterator();
		Request r;
		int j=0;
		requestsItems = new Request[requests.size()];
		
		while(iter.hasNext())
		{
			r = iter.next();
			
			requestsItems[j] = r;
			j++;
		}
		
		//set the adapter
		RequestCustomAdapter adapter = new RequestCustomAdapter(this, requestsItems);
		listViewRequests.setAdapter(adapter);
		
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