package com.followme.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.followme.adapter.FenceCustomAdapter;
import com.followme.manager.ParseManager;
import com.followme.manager.PersonalDataManager;
import com.followme.object.Contact;
import com.followme.object.Fence;
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

public class FenceControlActivity extends ActionBarActivity {
	
	private ListView listViewFence;
	private List<Fence> fenceList=new ArrayList<Fence>();
	private FenceCustomAdapter adapter;
	private CheckFences checkFencesThread;
	private Handler handler;
	private int finishMode=1;
	private ArrayList<String> requestIdList=new ArrayList<String>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fence_control_layout);
		
		listViewFence = (ListView) this.findViewById(R.id.fenceControlList);
		handler=new Handler();
		
		//recupero i dati dall'intent
		Object[] objects = (Object[]) getIntent().getSerializableExtra("contactsList");
		int radius  = getIntent().getIntExtra("radius", 0);
		Object[] fenceIdList=(Object[]) getIntent().getSerializableExtra("fenceIdList");
		Object[] requestIdArray=(Object[]) getIntent().getSerializableExtra("requestIdList");

		//aggiungo i dati alla lista nell view
		for(int i=0; i < requestIdArray.length; i++)
		{
			requestIdList.add((String) requestIdArray[i]);
		}
		
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
			fenceList.add(new Fence(radius,contactList.get(i),position,((String) fenceIdList[i]), true));			
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
	public void onBackPressed()
	{
		checkFencesThread.cancel(true);
		new AlertDialog.Builder(this)
	    .setTitle("Attention")
	    .setMessage("Are you sure you want to destroy the fence?")
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which)
	        { 
	        	Toast.makeText(FenceControlActivity.this, "Fence destroyed",Toast.LENGTH_LONG).show();
	            finishMode=2;
	        	finish();
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) 
	        { 
	        	checkFencesThread=new CheckFences();
	    		checkFencesThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
				ParseManager.deleteRequestAndFence(
						this,
						(String) requestIdList.get(i),
						ParseManager.getFencebyId(this,
								fenceList.get(i).getIdFence()));
			}
		}
		else
		{
			for(int i=0; i<requestIdList.size();i++)
			{
				ParseManager.updateRequestStatusById(this, (String)requestIdList.get(i), "chiusa");
			}
			
		}
		Intent intent = new Intent(FenceControlActivity.this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
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
				if(isCancelled())
					return null;
				
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
				
				Iterator<String> iterator = requestIdList.iterator();
				
				while(iterator.hasNext())
				{
					String idReq=iterator.next();
					boolean isActive=ParseManager.isRequestActive(FenceControlActivity.this, idReq);
					
					if(!isActive)
					{
						final int j=requestIdList.indexOf(idReq);
						//notificare all'utente che l'activity è stata chiusa dallo user
						handler.post(new Runnable() {
							@Override
							public void run() 
							{
								Toast.makeText(FenceControlActivity.this, "The user "+PersonalDataManager.getNameOfContact(fenceList.get(j).getUser().getPhoneNumber())+" destroys the fence",Toast.LENGTH_LONG).show();
							}
						});
						
						//cancello dalle liste ogni volta che una richiesta viene chiusa
						ParseManager.deleteRequestAndFence(FenceControlActivity.this, idReq, ParseManager.getFencebyId(FenceControlActivity.this,fenceList.get(j).getIdFence()));
						fenceList.remove(j);
						iterator.remove();
						
						//aggiorno l'adapter
						adapter = new FenceCustomAdapter(FenceControlActivity.this, fenceList);

						handler.post(new Runnable() {
							@Override
							public void run() 
							{
								listViewFence.setAdapter(adapter);
							}
						});
						
						//se non ci sono più fence chiudo l'activity
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
