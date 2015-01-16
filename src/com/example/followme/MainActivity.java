package com.example.followme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import android.os.AsyncTask;
import android.os.Bundle;

public class MainActivity extends Activity {
	
	private Utente user=new Utente("","");

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.main_activity_layout);
		boolean esito;
		try
		{
			new PersonalDataManager(this);
			PersonalDataManager.open();
			esito= PersonalDataManager.phoneNumberExists();
		}
		catch(Exception e)
		{
			esito=false;
		}
		if(!esito)
		{
			//creo il db
			new PersonalDataManager(this);
			PersonalDataManager.open();
			
			//intent per l'attività del primo utilizzo
			Intent intent = new Intent(this,FirstUseActivity.class);
			startActivityForResult(intent, 0);
			
		}
		else
		{
			//carico il phone number presente nel db
			PersonalDataManager.open();
			user.setNumero(PersonalDataManager.getPhoneNumber());
		}
		
		new NetworkActivity().execute();
	}
	
	private class NetworkActivity extends AsyncTask<Void, Integer, String>
    {

		@Override
		protected String doInBackground(Void... params) 
		{			
			while(true)
			{	
				if(!(user.getNumero().compareTo("")==0))
				{
					//roba da fare per chiedere a parse se c'è roba per me	
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
