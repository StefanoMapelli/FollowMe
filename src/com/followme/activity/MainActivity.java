package com.followme.activity;

import java.util.List;

import com.followme.activity.R;
import com.followme.fragment.RequestDialogFragment;
import com.followme.manager.ParseManager;
import com.followme.manager.PersonalDataManager;
import com.followme.object.Request;
import com.followme.object.User;
import com.parse.ParseObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends ActionBarActivity  {
	
	private User user=new User("","");
	private ParseObject userParseObject;
	
	private Button shareButton;
	private Button followButton;
	private Button loadButton;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity_layout);
		shareButton = (Button) findViewById(R.id.shareButton);
		followButton = (Button) findViewById(R.id.followButton);
		loadButton = (Button) findViewById(R.id.loadButton);
		
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
			user.setId(ParseManager.getId(this, PersonalDataManager.getPhoneNumber()));
			userParseObject=ParseManager.getUser(this, user.getId());
		}
		
		
		new NetworkActivity().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
		shareButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				//intent per l'attività di share
				Intent intent = new Intent(MainActivity.this,ChooseContactsForSharingActivity.class);
				intent.putExtra("userId", user.getId());
				startActivity(intent);
			}
			
		});
		
		followButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				//intent per l'attività di follow
				Intent intent = new Intent(MainActivity.this,ChooseContactsForFollowActivity.class);
				intent.putExtra("userId", user.getId());
				startActivity(intent);
			}
			
		});
		
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
	 
	        return super.onCreateOptionsMenu(menu);
	    }
	
	private class NetworkActivity extends AsyncTask<Void, Integer, String>
    {
		
		

		@Override
		protected String doInBackground(Void... params) 
		{	
			List<Request> requestsList;
			
			while(true)
			{	
				if(!(user.getPhoneNumber().compareTo("")==0))
				{
					//roba da fare per chiedere a parse se c'è roba per me
					requestsList=ParseManager.checkRequests(MainActivity.this, userParseObject);
					ParseManager.visualizeRquests(MainActivity.this, requestsList);
					
					if(!requestsList.isEmpty())
					{
						//notificare l'utente della presenza di richieste
						RequestDialogFragment rdf = new RequestDialogFragment(requestsList);
						rdf.show(getFragmentManager(), null);
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
