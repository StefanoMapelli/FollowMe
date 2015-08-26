package com.followme.activity;

import java.util.ArrayList;
import java.util.List;

import com.followme.adapter.FenceCustomAdapter;

import com.followme.manager.ParseManager;
import com.followme.object.Contact;
import com.followme.object.Fence;
import com.google.android.gms.maps.model.LatLng;

import android.support.v7.app.ActionBarActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class FenceControlActivity extends ActionBarActivity {
	
	private ListView listViewFence;
	private List<Fence> fenceList=new ArrayList();
	private FenceCustomAdapter adapter;
	private CheckFences checkFencesThread;
	private Handler handler;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fence_control_layout);
		
		listViewFence = (ListView) this.findViewById(R.id.fenceControlList);
		handler=new Handler();
		
		//recuero i dati dall'intent
		Object[] objects = (Object[]) getIntent().getSerializableExtra("contactsList");
		int radius  = getIntent().getIntExtra("radius", 0);
		String idFence=getIntent().getStringExtra("fenceId");
		double fenceLatitude=getIntent().getDoubleExtra("fenceLatitude",0);
		double fenceLongitude=getIntent().getDoubleExtra("fenceLongitude",0);
		LatLng position=new LatLng(fenceLatitude,fenceLongitude);
		List<Contact> contactList = new ArrayList<Contact>();
		
		
		//aggiungo i dati alla lista nell view
		for(int i=0; i < objects.length; i++)
		{
			contactList.add((Contact) objects[i]);
			
		}
		
		for(int i=0; i < contactList.size(); i++)
		{
			fenceList.add(new Fence(radius,contactList.get(i),position,idFence, true));			
		}
		
		//set the adapter
		adapter = new FenceCustomAdapter(this, fenceList);
		listViewFence.setAdapter(adapter);
		checkFencesThread=new CheckFences();
		checkFencesThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fence_control, menu);
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
	
	
	
	
	private class CheckFences extends AsyncTask<Void, Integer, String>
    {
		
		@Override
		protected String doInBackground(Void... params) 
		{			
			while(true)
			{	
				Fence fenceObject;
				
				for(int i=0; i<fenceList.size();i++)
				{
					fenceObject = fenceList.get(i);
					if(!ParseManager.isInTheFence(FenceControlActivity.this, fenceObject.getIdFence()))
					{
						fenceObject.setInTheFence(false);
						adapter = new FenceCustomAdapter(FenceControlActivity.this, fenceList);
						
						handler.post(new Runnable() {
							@Override
							public void run() 
							{
								listViewFence.setAdapter(adapter);
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
