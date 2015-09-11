package com.followme.activity;

import java.util.ArrayList;
import java.util.List;

import com.followme.activity.R;
import com.followme.manager.ParseManager;
import com.followme.manager.PersonalDataManager;
import com.followme.object.Request;
import com.followme.object.User;
import com.parse.ParseObject;

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
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity  {
	
	private User user=new User("","");
	private ParseObject userParseObject;
	private List<Request> requestsList=new ArrayList<Request>();
	private Menu menu;
	private Handler handler;
	private Vibrator vibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity_layout);
		
		handler=new Handler();
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		boolean esito;
		try
		{
			new PersonalDataManager(this);
			PersonalDataManager.open();
			esito= PersonalDataManager.userExists();
		}
		catch(Exception e)
		{
			esito=false;
		}
		//nessun numero di telefono salvato nel db
		if(!esito)
		{
			//creo il db
			new PersonalDataManager(this);
			PersonalDataManager.open();
			
			//intent per l'attività del primo utilizzo
			Intent intent = new Intent(this,FirstUseActivity.class);
			startActivityForResult(intent, 1);			
		}
		else
		{
			//carico il phone number presente nel db
			PersonalDataManager.open();
			user.setPhoneNumber(PersonalDataManager.getPhoneNumber());
			//carico l'id presente su parse
			String id = ParseManager.getId(this, PersonalDataManager.getPhoneNumber());
			if(id == null)
			{
				Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
			}
			else
			{
				user.setId(id);
				userParseObject=ParseManager.getUser(this, user.getId());
				if(userParseObject == null)
				{
					Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
				}
			}
		}		
		
		new NetworkActivity().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);		
	}
	
	public void followOnClickHandler(View v)
	{
		if(user.getId().compareTo("")==0)
		{
			//carico l'id presente su parse
			String id = ParseManager.getId(this, PersonalDataManager.getPhoneNumber());
			if(id == null)
			{
				Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
			}
			else
			{
				user.setId(id);
					
				//intent per l'attività di follow					
				Intent intent = new Intent(MainActivity.this,ChooseContactsForFollowActivity.class);					
				intent.putExtra("userId", user.getId());					
				startActivity(intent);
			}
		}
		else
		{
			//intent per l'attività di follow					
			Intent intent = new Intent(MainActivity.this,ChooseContactsForFollowActivity.class);					
			intent.putExtra("userId", user.getId());					
			startActivity(intent);
		}
	}
	
	public void shareOnClickHandler(View v)
	{
		if(user.getId().compareTo("")==0)
		{
			//carico l'id presente su parse
			String id = ParseManager.getId(this, PersonalDataManager.getPhoneNumber());
			if(id == null)
			{
				Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
			}
			else
			{
				user.setId(id);
				
				//intent per l'attività di share
				Intent intent = new Intent(MainActivity.this,ChooseContactsForSharingActivity.class);
				intent.putExtra("userId", user.getId());
				startActivity(intent);
			}
		}
		else
		{
			//intent per l'attività di share
			Intent intent = new Intent(MainActivity.this,ChooseContactsForSharingActivity.class);
			intent.putExtra("userId", user.getId());
			startActivity(intent);
		}		
	}
	
	public void loadPathsOnClickHandler(View v)
	{
		//intent per l'attività di load path
		Intent intent = new Intent(MainActivity.this,SavedPathListActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		//sta ritornando l'attività first use
		if(requestCode==1)
		{
			if(resultCode==RESULT_OK)
			{
				//carico in user l'id presente su parse ottenuto dall'activity first use
				user.setId(data.getStringExtra("id"));
				//carico il numero di telefono salvato sul db
				user.setPhoneNumber(PersonalDataManager.getPhoneNumber());
				userParseObject=ParseManager.getUser(this,user.getId());
				if(userParseObject == null)
				{
					Toast.makeText(this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
				}
			}
		}
	}
	
	@Override
	public void onBackPressed()
	{
		new AlertDialog.Builder(this)
	    .setTitle("Quit")
	    .setMessage("Are you sure you want to quit?")
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue
	        	System.exit(0);
	        	finish();
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
	     })
	    .setIcon(android.R.drawable.ic_dialog_alert)
	    .show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.activity_main_actions, menu);

		this.menu = menu;
		this.menu.getItem(0).setVisible(false);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.openRequestNotificationList) 
		{
			//cancello le notifiche
			String notificationService = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager)
					getSystemService(notificationService);
			mNotificationManager.cancel(1);
			
			//apro la lista delle richieste
			Intent intent = new Intent(this,RequestsListActivity.class);
			intent.putExtra("incomingRequests", requestsList.toArray());
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	private class NetworkActivity extends AsyncTask<Void, Integer, String>
	{
		@Override
		protected String doInBackground(Void... params) 
		{	
			int oldRequestNumber=0;
			while(true)
			{	
				if(!(user.getPhoneNumber().compareTo("")==0))
				{					
					if(user.getId().compareTo("")==0)
					{
						//carico l'id presente su parse
						String id = ParseManager.getId(MainActivity.this, PersonalDataManager.getPhoneNumber());
						if(id == null)
						{
							
							handler.post(new Runnable() {
								@Override
								public void run() 
								{								
									Toast.makeText(MainActivity.this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();
								}
							});
						}
						else
						{
							user.setId(id);
						}
					}
					
					oldRequestNumber=requestsList.size();
					//roba da fare per chiedere a parse se c'è roba per me
					if(userParseObject==null)
					{
						userParseObject=ParseManager.getUser(MainActivity.this, user.getId());

						if(userParseObject==null)
						{
							handler.post(new Runnable() {
								@Override
								public void run() 
								{
									Toast.makeText(MainActivity.this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();										
								}
							});
						}
						else
						{
							List<Request> notVisualizedList = ParseManager.checkRequests(MainActivity.this, userParseObject, "non visualizzata");
							List<Request> acceptedList = ParseManager.checkRequests(MainActivity.this, userParseObject, "accettata");
							List<Request> declinedList = ParseManager.checkRequests(MainActivity.this, userParseObject, "rifiutata");
							
							if(notVisualizedList == null || acceptedList == null || declinedList==null)
							{
								handler.post(new Runnable() {
									@Override
									public void run() 
									{
										Toast.makeText(MainActivity.this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();											
									}
								});
							}
							else
							{
								requestsList.addAll(notVisualizedList);
								requestsList.removeAll(acceptedList);
								requestsList.removeAll(declinedList);
								ParseManager.visualizeRquests(MainActivity.this, notVisualizedList);		

								for(Request r : declinedList)
								{	
									if(!ParseManager.deleteRequest(MainActivity.this, r.getId()))
									{
										handler.post(new Runnable() {
											@Override
											public void run() 
											{
												Toast.makeText(MainActivity.this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();									
											}
										});
									}
								}
								
								int requestNumber=requestsList.size();

								if(requestNumber>oldRequestNumber)
								{
									handler.post(new Runnable() {
										@Override
										public void run() 
										{
											vibrator.vibrate(250);

											String notificationService = Context.NOTIFICATION_SERVICE;
											NotificationManager mNotificationManager = (NotificationManager)
													getSystemService(notificationService);

											Intent resultIntent = new Intent(MainActivity.this,MainActivity.class);
											resultIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

											PendingIntent resultPendingIntent = PendingIntent.getActivity(MainActivity.this, 0, resultIntent, 0);

											//notifica
											NotificationCompat.Builder mBuilder =
													new NotificationCompat.Builder(MainActivity.this);
											mBuilder.setSmallIcon(R.drawable.follow);
											mBuilder.setContentTitle("Follow Me");
											mBuilder.setContentText("There's a request for you.");
											mBuilder.setAutoCancel(true);
											mBuilder.setContentIntent(resultPendingIntent);	

											// the next two lines initialize the Notification, using the configurations
											// above
											Notification notification = mBuilder.build();										

											final int HELLO_ID = 1;
											mNotificationManager.notify(HELLO_ID, notification);
										}
									});
								}

								if(requestNumber==0)
								{
									handler.post(new Runnable() {
										@Override
										public void run() 
										{
											menu.getItem(0).setVisible(false);
										}
									});

								}
								else if(requestNumber==1)
								{
									handler.post(new Runnable() {
										@Override
										public void run() 
										{
											menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.request_1));
											menu.getItem(0).setVisible(true);
										}
									});
								}
								else if(requestNumber==2)
								{
									handler.post(new Runnable() {
										@Override
										public void run() 
										{
											menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.request_2));
											menu.getItem(0).setVisible(true);
										}
									});
								}
								else if(requestNumber==3)
								{
									handler.post(new Runnable() {
										@Override
										public void run() 
										{
											menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.request_3));
											menu.getItem(0).setVisible(true);
										}
									});
								}
								else if(requestNumber>=4)
								{
									handler.post(new Runnable() {
										@Override
										public void run() 
										{
											menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.request_4));
											menu.getItem(0).setVisible(true);
										}
									});
								}																
							}												
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}						
					}
					else
					{
						List<Request> notVisualizedList = ParseManager.checkRequests(MainActivity.this, userParseObject, "non visualizzata");
						List<Request> acceptedList = ParseManager.checkRequests(MainActivity.this, userParseObject, "accettata");
						List<Request> declinedList = ParseManager.checkRequests(MainActivity.this, userParseObject, "rifiutata");
						
						if(notVisualizedList == null || acceptedList == null || declinedList==null)
						{
							handler.post(new Runnable() {
								@Override
								public void run() 
								{
									Toast.makeText(MainActivity.this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();				
						
								}
							});
						}
						else
						{
							requestsList.addAll(notVisualizedList);
							requestsList.removeAll(acceptedList);
							requestsList.removeAll(declinedList);
							ParseManager.visualizeRquests(MainActivity.this, notVisualizedList);		

							for(Request r : declinedList)
							{	
								if(!ParseManager.deleteRequest(MainActivity.this, r.getId()))
								{
									handler.post(new Runnable() {
										@Override
										public void run() 
										{
											Toast.makeText(MainActivity.this, "Make sure your internet connection is enabled!", Toast.LENGTH_LONG).show();									
										}
									});
								}
							}
							
							int requestNumber=requestsList.size();

							if(requestNumber>oldRequestNumber)
							{
								handler.post(new Runnable() {
									@Override
									public void run() 
									{
										vibrator.vibrate(200);

										String notificationService = Context.NOTIFICATION_SERVICE;
										NotificationManager mNotificationManager = (NotificationManager)
												getSystemService(notificationService);

										Intent resultIntent = new Intent(MainActivity.this,MainActivity.class);
										resultIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

										PendingIntent resultPendingIntent = PendingIntent.getActivity(MainActivity.this, 0, resultIntent, 0);

										//notifica
										NotificationCompat.Builder mBuilder =
												new NotificationCompat.Builder(MainActivity.this);
										mBuilder.setSmallIcon(R.drawable.follow);
										mBuilder.setContentTitle("Follow Me");
										mBuilder.setContentText("There's a request for you.");
										mBuilder.setAutoCancel(true);
										mBuilder.setContentIntent(resultPendingIntent);								

										// the next two lines initialize the Notification, using the configurations
										// above
										Notification notification = mBuilder.build();

										final int HELLO_ID = 1;
										mNotificationManager.notify(HELLO_ID, notification);
									}
								});
							}

							if(requestNumber==0)
							{
								handler.post(new Runnable() {
									@Override
									public void run() 
									{
										menu.getItem(0).setVisible(false);
									}
								});

							}
							else if(requestNumber==1)
							{
								handler.post(new Runnable() {
									@Override
									public void run() 
									{
										menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.request_1));
										menu.getItem(0).setVisible(true);
									}
								});
							}
							else if(requestNumber==2)
							{
								handler.post(new Runnable() {
									@Override
									public void run() 
									{
										menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.request_2));
										menu.getItem(0).setVisible(true);
									}
								});
							}
							else if(requestNumber==3)
							{
								handler.post(new Runnable() {
									@Override
									public void run() 
									{
										menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.request_3));
										menu.getItem(0).setVisible(true);
									}
								});
							}
							else if(requestNumber>=4)
							{
								handler.post(new Runnable() {
									@Override
									public void run() 
									{
										menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.request_4));
										menu.getItem(0).setVisible(true);
									}
								});
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
	}
}
