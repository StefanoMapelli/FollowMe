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
	 * Metodo che aggiorna o inserisce il numero di telefono salvato su Parse
	 * @param phoneNumber : numero di telefono dell'utente da updatare o inserire
	 */
	public static void insertPhoneNumber(Context context, String phoneNumber)
	{
		final String pNumber=phoneNumber;
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		ParseObject address = new ParseObject("Utente");
		address.put("numero", pNumber);
		address.saveInBackground();											
	}
	
	public static void getRequest(Context context, String phoneNumber)
	{
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		List<ParseObject> objects = null;
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Richiesta");
	}
	
	/**
	 * Metodo che cerca sulla tabella Address in Parse la tupla avente il dato numero di telefono
	 * e ritorna il campo indirizzo ip.
	 * @param phoneNumber : numero di telefono dell'utente di cui si vuole conoscere l'ip.
	 * @return : il campo indirizzo ip della tupla identificata da numero di telefono pari a phoneNumber.
	 */
	public static String getPhoneNumber(Context context, String phoneNumber)
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
