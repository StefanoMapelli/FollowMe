package com.example.followme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import android.os.AsyncTask;
import android.os.Bundle;

public class MainActivity extends Activity {
	
	private String phoneNumber="";

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
			phoneNumber = PersonalDataManager.getPhoneNumber();
		}
		
		//Parse.initialize(this,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		new NetworkActivity().execute();
	}
	
	private class NetworkActivity extends AsyncTask<Void, Integer, String>
    {

		@Override
		protected String doInBackground(Void... params) 
		{
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
			String ipAddress = "initialization";
			PersonalDataManager.open();
			
			while(true)
			{
				phoneNumber=PersonalDataManager.getPhoneNumber();
				if(phoneNumber.compareTo("")!=0 && ConnectionManager.connectionPresent(connMgr) && ipAddress.compareTo(ConnectionManager.getIpAddress(connMgr,wifiMgr))!=0)
				{			
					ipAddress = ConnectionManager.getIpAddress(connMgr,wifiMgr);					
					ParseManager.updateIpAddress(MainActivity.this, phoneNumber, ipAddress);					
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
    }
}
