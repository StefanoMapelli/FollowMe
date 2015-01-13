package com.example.followme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MainActivity extends Activity {
	
	private String phoneNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.main_activity_layout);
		boolean esito;
		try
		{
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
			phoneNumber = PersonalDataManager.getPhoneNumber();
		}
		PersonalDataManager.close();
		
		Parse.initialize(this,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		new NetworkActivity().execute();
	}
	
	private class NetworkActivity extends AsyncTask<Void, Integer, String>
    {

		@Override
		protected String doInBackground(Void... params) 
		{
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			String ipAddress = "initialization";
			
			while(true)
			{
				if(ConnectionManager.connectionPresent(connMgr) && ipAddress.compareTo(ConnectionManager.getIpAddress())!=0)
				{			
					ipAddress = ConnectionManager.getIpAddress();					
					ParseManager.updateIpAddress(phoneNumber, ipAddress);					
				}
			}
		}
    }
}
