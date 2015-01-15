package com.example.followme;

import java.util.List;

import android.content.Context;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ParseManager {
	
	/**
	 * Metodo che aggiorna l'indirizzo ip salvato su Parse della tupla con numero di telefono
	 * pari a phoneNumber.
	 * @param phoneNumber : numero di telefono dell'utente di cui vogliamo modificare l'ip
	 */
	public static void updateIpAddress(Context context, String phoneNumber, String ipAddress)
	{
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
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
								objects.get(0).saveInBackground();
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
	
	/**
	 * Metodo che cerca sulla tabella Address in Parse la tupla avente il dato numero di telefono
	 * e ritorna il campo indirizzo ip.
	 * @param phoneNumber : numero di telefono dell'utente di cui si vuole conoscere l'ip.
	 * @return : il campo indirizzo ip della tupla identificata da numero di telefono pari a phoneNumber.
	 */
	public static String getIpAddress(Context context, String phoneNumber)
	{
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		List<ParseObject> objects = null;
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Address");
		query.whereEqualTo("phone", phoneNumber);
		try {
			objects = query.find();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		if(!objects.isEmpty())
		{
			return objects.get(0).getString("ipAddress");
		}
		else
		{
			return null;
		}
	}

}
