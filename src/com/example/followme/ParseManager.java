package com.example.followme;

import java.util.List;

import android.content.Context;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ParseManager {
	
	/**
	 * Method that inserts or updates the phone number in parse database.
	 * @param phoneNumber : phone number to update or insert
	 */
	public static void insertPhoneNumber(Context context, String phoneNumber)
	{
		final String pNumber=phoneNumber;
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		ParseObject address = new ParseObject("Utente");
		address.put("numero", pNumber);
		address.saveInBackground();											
	}
	
	//PERCHè QUESTO METODO?
	/*public static void getRequest(Context context, String phoneNumber)
	{
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		List<ParseObject> objects = null;
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Richiesta");
	}*/
	
	//PERCHè L'INDIRIZZO IP?!
	/**
	 * Method that search on the table address on parse the tuple with the given phone number
	 * and return...
	 * @param phoneNumber : numero di telefono dell'utente di cui si vuole conoscere l'ip.
	 * @return : il campo indirizzo ip della tupla identificata da numero di telefono pari a phoneNumber.
	 */
	/*
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
	}*/

}
