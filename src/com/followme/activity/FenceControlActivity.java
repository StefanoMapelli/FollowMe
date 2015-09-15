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
import com.parse.ParseObject;

import android.support.v4.app.NotificationCompat;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class FenceControlActivity extends ActionBarSuperClassActivity {
	
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
				
		PersonalDataManager.insertOrUpdateCurrentActivity("FenceControl");
		
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
				if(!ParseManager.deleteRequestAndFence(
						this,
						(String) requestIdList.get(i),
						ParseManager.getFencebyId(this,
								fenceList.get(i).getIdFence())))
				{
					Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
				}
			}
		}
		else
		{
			for(int i=0; i<requestIdList.size();i++)
			{
				if(!ParseManager.updateRequestStatusById(this, (String)requestIdList.get(i), "chiusa"))
				{
					Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
				}
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
		if (id == R.id.fenceDetailsButton) {
			Fence itemToShow = fenceList.get(0);
			
			Intent intent = new Intent(FenceControlActivity.this, ShowFenceOrDestinationActivity.class);
			intent.putExtra("circle", itemToShow);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
		
	private class CheckFences extends AsyncTask<Void, Integer, String>
    {
		
		@Override
		protected String doInBackground(Void... params) 
		{			
			Boolean isNotificationShown=false;
			while(true)
			{	
				if(isCancelled())
					return null;
				
				Fence fenceObject;
				
				for(int i=0; i<fenceList.size();i++)
				{
					fenceObject = fenceList.get(i);
					Boolean isInTheFence = ParseManager.isInTheFence(FenceControlActivity.this, fenceObject.getIdFence());
					
					if(isInTheFence== null)
					{
						handler.post(new Runnable() {
							@Override
							public void run() 
							{
								Toast.makeText(FenceControlActivity.this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
							}
						});
					}
					else
					{
						if(!isInTheFence)
						{
							fenceObject.setInTheFence(false);
							adapter = new FenceCustomAdapter(FenceControlActivity.this, fenceList);
							final String userName=fenceObject.getUser().getName();
							if(!isNotificationShown)
							{
								isNotificationShown=true;
								handler.post(new Runnable() {
									@Override
									public void run() 
									{

										String notificationService = Context.NOTIFICATION_SERVICE;
										NotificationManager mNotificationManager = (NotificationManager)
												getSystemService(notificationService);

										Intent resultIntent = new Intent(FenceControlActivity.this,FenceControlActivity.class);
										resultIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

										PendingIntent resultPendingIntent = PendingIntent.getActivity(FenceControlActivity.this, 0, resultIntent, 0);

										//notifica
										NotificationCompat.Builder mBuilder =
												new NotificationCompat.Builder(FenceControlActivity.this);
										mBuilder.setSmallIcon(R.drawable.follow);
										mBuilder.setContentTitle("Follow Me");
										mBuilder.setContentText(userName+" runs away!");
										mBuilder.setAutoCancel(true);
										mBuilder.setContentIntent(resultPendingIntent);								

										// the next two lines initialize the Notification, using the configurations
										// above
										Notification notification = mBuilder.build();

										final int notification_id = 2;
										mNotificationManager.notify(notification_id, notification);
									}
								});
								handler.post(new Runnable() {
									@Override
									public void run() 
									{
										listViewFence.setAdapter(adapter);
									}
								});
							}	
						}
						else
						{
							isNotificationShown=false;
							fenceObject.setInTheFence(true);
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
				}
				
				Iterator<String> iterator = requestIdList.iterator();
				
				while(iterator.hasNext())
				{
					String idReq=iterator.next();
					Boolean isActive=ParseManager.isRequestActive(FenceControlActivity.this, idReq);
					
					if(isActive==null)
					{
						handler.post(new Runnable() {
							@Override
							public void run() 
							{
								Toast.makeText(FenceControlActivity.this, "Make sure your internet connection is enabled!",Toast.LENGTH_LONG).show();
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
									Toast.makeText(FenceControlActivity.this, "The user "+PersonalDataManager.getNameOfContact(fenceList.get(j).getUser().getPhoneNumber())+" destroys the fence",Toast.LENGTH_LONG).show();
								}
							});

							ParseObject fence = ParseManager.getFencebyId(FenceControlActivity.this,fenceList.get(j).getIdFence());
							if(fence == null)
							{
								handler.post(new Runnable() {
									@Override
									public void run() 
									{
										Toast.makeText(FenceControlActivity.this, "Make sure your internet connection is enabled!",Toast.LENGTH_LONG).show();
									}
								});
							}
							else
							{
								//cancello dalle liste ogni volta che una richiesta viene chiusa
								if(!ParseManager.deleteRequestAndFence(FenceControlActivity.this, idReq, fence))
								{
									handler.post(new Runnable() {
										@Override
										public void run() 
										{
											Toast.makeText(FenceControlActivity.this, "Make sure your internet connection is enabled!",Toast.LENGTH_LONG).show();
										}
									});
								}
								else
								{
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
}
