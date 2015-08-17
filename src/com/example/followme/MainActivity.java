package com.example.followme;

import java.util.List;

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
	
	private Button shareButton;
	private Button followButton;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity_layout);
		shareButton = (Button) findViewById(R.id.shareButton);
		followButton = (Button) findViewById(R.id.followButton);
		
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
		}
		
		new NetworkActivity().execute();
		
		shareButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				//intent per l'attività di share
				Intent intent = new Intent(MainActivity.this,ChooseContactsForSharingActivity.class);
				startActivity(intent);
			}
			
		});
		
		followButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				//intent per l'attività di follow
				Intent intent = new Intent(MainActivity.this,FollowActivity.class);
				startActivity(intent);
			}
			
		});
		
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
			}
		}
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
					requestsList=ParseManager.checkRequests(MainActivity.this, user.getId());
					
					if(!requestsList.isEmpty())
					{
						//notificare l'utente della presenza di richieste
						RequestDialogFragment rdf = new RequestDialogFragment(requestsList);
						rdf.show(getFragmentManager(), null);
					}
				}
								
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
    }
}
