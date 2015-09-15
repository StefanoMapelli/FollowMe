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

public class DestinationControlActivity extends ActionBarSuperClassActivity {
	
	private ListView listViewDestination;
	private List<Destination> destinationList=new ArrayList<Destination>();
	private DestinationCustomAdapter adapter;
	private CheckDestinations checkDestinationsThread;
	private Handler handler;
	private int finishMode=1;
	private List<Contact> contactList;
	private ArrayList<String> requestIdList=new ArrayList<String>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.destination_control_layout);
		
		PersonalDataManager.insertOrUpdateCurrentActivity("DestinationControl");
		
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
		contactList = new ArrayList<Contact>();
		
		//aggiungo i dati alla lista nell view
		for(int i=0; i < objects.length; i++)
		{
			contactList.add((Contact) objects[i]);
		}

		for(int i=0; i < contactList.size(); i++)
		{
			destinationList.add(new Destination(radius,contactList.get(i),position,(String)destinationIdList[i], false, null));			
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
				if(!ParseManager.deleteRequestAndDestination(
						this,
						(String) requestIdList.get(i),
						ParseManager.getDestinationbyId(this,
								destinationList.get(i).getIdDestination())))
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
		if (id == R.id.destinationDetailsButton) {
			
			Destination itemToShow = destinationList.get(0);
			
			Intent intent = new Intent(DestinationControlActivity.this, ShowFenceOrDestinationActivity.class);
			intent.putExtra("circle", itemToShow);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class CheckDestinations extends AsyncTask<Void, Integer, String>
    {
		
		@Override
		protected String doInBackground(Void... params) 
		{		
			Boolean isNotificationShown=false;
			final Boolean[]  statusShown = new Boolean[requestIdList.size()];
			
			for(int i=0; i<statusShown.length; i++)
			{
				statusShown[i] = false;
			}
			
			while(true)
			{	
				if(isCancelled())
					return null;

				handler.post(new Runnable() {
					@Override
					public void run() 
					{
						int k=0;
						Destination destinationObject = null;
						for(String reqId : requestIdList)
						{
							destinationObject = destinationList.get(k);
							if(!statusShown[k])
							{
								String status = ParseManager.getRequestStatus(DestinationControlActivity.this, reqId);

								if(status==null)
								{
									Toast.makeText(DestinationControlActivity.this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
								}
								else
								{
									if(status.compareTo("accettata")==0)
									{
										destinationObject.setStatusAccepted(true);
										adapter = new DestinationCustomAdapter(DestinationControlActivity.this, destinationList);
										statusShown[k] = true;
									}
									else if(status.compareTo("rifiutata")==0)
									{
										destinationObject.setStatusAccepted(false);
										adapter = new DestinationCustomAdapter(DestinationControlActivity.this, destinationList);
										statusShown[k] = true;
									}
								}
							}
							k++;
						}
					}
				});
				
				
				Destination destinationObject;

				for(int i=0; i<destinationList.size();i++)
				{
					destinationObject = destinationList.get(i);
					Boolean isInDestination = ParseManager.isInTheDestination(DestinationControlActivity.this, destinationObject.getIdDestination());
					
					if(isInDestination== null)
					{
						handler.post(new Runnable() {
							@Override
							public void run() 
							{
								Toast.makeText(DestinationControlActivity.this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
							}
						});
					}
					else
					{
						if(isInDestination)
						{
							destinationObject.setInTheDestination(true);
							adapter = new DestinationCustomAdapter(DestinationControlActivity.this, destinationList);
							final String userName=destinationObject.getUser().getName();
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

										Intent resultIntent = new Intent(DestinationControlActivity.this,DestinationControlActivity.class);
										resultIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

										PendingIntent resultPendingIntent = PendingIntent.getActivity(DestinationControlActivity.this, 0, resultIntent, 0);

										//notifica
										NotificationCompat.Builder mBuilder =
												new NotificationCompat.Builder(DestinationControlActivity.this);
										mBuilder.setSmallIcon(R.drawable.follow);
										mBuilder.setContentTitle("Follow Me");
										mBuilder.setContentText("The user "+ userName+" arrives in the destination zone!");
										mBuilder.setAutoCancel(true);
										mBuilder.setContentIntent(resultPendingIntent);								

										// the next two lines initialize the Notification, using the configurations
										// above
										Notification notification = mBuilder.build();

										final int notification_id = 3;
										mNotificationManager.notify(notification_id, notification);
										
									}
								});
							}
							
							handler.post(new Runnable() {
								@Override
								public void run() 
								{
									listViewDestination.setAdapter(adapter);
								}
							});
						}
						else
						{
							isNotificationShown=false;
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
				}
				
				Iterator<String> iterator = requestIdList.iterator();
				
				while(iterator.hasNext())
				{
					String idReq=iterator.next();
					Boolean isActive=ParseManager.isRequestActive(DestinationControlActivity.this, idReq);
					
					if(isActive==null)
					{
						handler.post(new Runnable() {
							@Override
							public void run() 
							{
								Toast.makeText(DestinationControlActivity.this, "Make sure your internet connection is enabled!",Toast.LENGTH_LONG).show();
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
									Toast.makeText(DestinationControlActivity.this, PersonalDataManager.getNameOfContact(destinationList.get(j).getUser().getPhoneNumber())+" close the destination",Toast.LENGTH_LONG).show();
								}
							});

							ParseObject po = ParseManager.getDestinationbyId(DestinationControlActivity.this,destinationList.get(j).getIdDestination());

							if(po == null)
							{
								handler.post(new Runnable() {
									@Override
									public void run() 
									{
										Toast.makeText(DestinationControlActivity.this, "Make sure your internet connection is enabled!",Toast.LENGTH_LONG).show();
									}
								});
							}
							else
							{
								//cancello dalle liste ogni volta che una richiesta viene chiusa
								if(!ParseManager.deleteRequestAndDestination(DestinationControlActivity.this, idReq, po))
								{
									handler.post(new Runnable() {
										@Override
										public void run() 
										{
											Toast.makeText(DestinationControlActivity.this, "Make sure your internet connection is enabled!",Toast.LENGTH_LONG).show();
										}
									});
								}
								else
								{
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
