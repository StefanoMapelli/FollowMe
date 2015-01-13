package com.example.followme;

import android.app.Activity;
import android.content.Context;
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
	
	private EditText phoneNumberText;
	private Button okButton;
	private String phoneNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		
		Parse.enableLocalDatastore(this);
		Parse.initialize(this,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		
		ParseQuery<ParseObject> personalQuery = ParseQuery.getQuery("PersonalData");
		personalQuery.fromLocalDatastore();
		int numberOfData=-1;
		try {
			numberOfData=personalQuery.count();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(numberOfData<=0)
		{
			//chiedere numero all'utente perchè primo utilizzo
			super.setContentView(R.layout.first_use_layout);
			phoneNumberText = (EditText) findViewById(R.id.phoneNumberText);
			okButton = (Button) findViewById(R.id.okButton);
			
			okButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) 
				{
					phoneNumber = phoneNumberText.getText().toString();
					ParseObject personalData = new ParseObject("PersonalData");
					personalData.put("phone", phoneNumber);	
					personalData.saveInBackground();
				} 
			});
			super.setContentView(R.layout.main_activity_layout);	
		}
		else
		{
			super.setContentView(R.layout.main_activity_layout);
		}
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
				if(connectionPresent(connMgr) && ipAddress.compareTo(getIpAddress())!=0)
				{			
					ipAddress = getIpAddress();					
					ParseManager.updateIpAddress(phoneNumber, ipAddress);					
				}
			}
		}
		
		public boolean connectionPresent(ConnectivityManager cMgr)
		{
		      if (cMgr != null) {
		         NetworkInfo netInfo = cMgr.getActiveNetworkInfo();
		         if ((netInfo != null) && (netInfo.getState() != null)) {
		            return netInfo.getState().equals(State.CONNECTED);
		         } else {
		            return false;
		         }
		      }
		      return false;
		   }
		
		 public String getIpAddress() {
		      try {
		         for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
		            NetworkInterface intf = en.nextElement();
		            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
		               InetAddress inetAddress = enumIpAddr.nextElement();
		               if (!inetAddress.isLoopbackAddress()) {
		                  return inetAddress.getHostAddress().toString();
		               }
		            }
		         }
		      } catch (SocketException e) {
		        // Log.e(Constants.LOG_TAG, e.getMessage(), e);
		      }
		      return null;
		   }
    }
}
