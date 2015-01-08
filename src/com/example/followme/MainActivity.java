package com.example.followme;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Parse.initialize(this,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		new NetworkActivity().execute();
	}
	
	private class NetworkActivity extends AsyncTask<Void, Integer, String>
    {

		@Override
		protected String doInBackground(Void... params) 
		{
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			String ipAddress = null;
			String phoneNumber = ((TelephonyManager)getSystemService(TELEPHONY_SERVICE)).getLine1Number();
			
			if(connectionPresent(connMgr))
			{
				ipAddress = getIpAddress();
			}
				
			while(true)
			{
				if(connectionPresent(connMgr) && ipAddress!=getIpAddress())
				{			
					ipAddress = getIpAddress();
					final String ip = ipAddress;
					final String pNumber = phoneNumber;
					ParseQuery<ParseObject> query = ParseQuery.getQuery("Address");
					query.whereEqualTo("phone", phoneNumber);
					query.findInBackground(new FindCallback<ParseObject>() 
							{
								@Override
								public void done(List<ParseObject> objects,ParseException e) 
								{
									if(e == null)
									{
										if(!objects.isEmpty())
										{
											objects.get(0).put("ipAddress",ip);
										}
										else
										{
											ParseObject address = new ParseObject("Address");
											address.put("phone", pNumber);
											address.put("ipAddress", ip);
											address.saveInBackground();											
										}
									}
								}
							}
					);
					
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
