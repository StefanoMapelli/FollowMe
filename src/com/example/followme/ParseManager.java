package com.example.followme;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ParseManager {
	
	/**
	 * Method that inserts the phone number in parse database.
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
	
	/**
	 * Method that insert a new request in the parse database.
	 * @param type
	 * @param sender
	 * @param receiver
	 */
	public static void insertRequest(Context context, String type, User sender, User receiver)
	{
		List<ParseObject> objects = null;	
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Utente");
		query.whereEqualTo("objectId", sender.getId());
		ParseObject senderPo=null;
		
		try {
			objects=query.find();
			senderPo=objects.get(0);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		query = ParseQuery.getQuery("Utente");
		query.whereEqualTo("objectId", receiver.getId());
		ParseObject receiverPo=null;
		
		try {
			objects=query.find();
			receiverPo=objects.get(0);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
	
		ParseObject newReq = new ParseObject("Richiesta");
		newReq.put("tipoRichiesta", type);
		newReq.put("idMittente", senderPo);
		newReq.put("idDestinatario", receiverPo);
		newReq.put("accettata", false);
		newReq.saveInBackground();
	}
	
	/**
	 * Method that retrieves the id of a user from parse database having the phone number
	 * @param phoneNumber 
	 * @return : the id of the user with that phone number
	 */
	public static String getId(Context context, String phoneNumber)
	{
		List<ParseObject> objects = null;
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Utente");
		query.whereEqualTo("numero", phoneNumber);
		
		try {
			objects = query.find();
			return objects.get(0).getObjectId();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Method that get the ParseObject user with given id from the parse database.
	 * @param id
	 * @return
	 */
	public static ParseObject getUser(Context context, String id)
	{
		List<ParseObject> objects = null;
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Utente");
		query.whereEqualTo("objectId", id);
		
		try {
			objects=query.find();
			return objects.get(0);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;		
	}
	
	/**
	 * Method that checks if phoneNumber already exists in parse database
	 * @param phoneNumber : phoneNumber to check
	 * @return : true it phoneNumber already exists
	 */
	public static boolean phoneNumberExists(Context context, String phoneNumber)
	{
		List<ParseObject> objects = null;
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Utente");
		query.whereEqualTo("numero", phoneNumber);
		
		try {
			objects = query.find();
			
			if(objects.size()>0)
			{
				return true;
			}
			else
			{
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Method that checks if there is some requests directed to the user with id id 
	 * and return the list of requests for that user.
	 * @param id : id of the user receiver of the requests 
	 * @return a list of requests for the user with id id
	 */
	public static List<Request> checkRequests(Context context, String id)
	{
		List<ParseObject> objects = null;
		List<Request> requests = new ArrayList<Request>();
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		ParseObject user=getUser(context, id);
	
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Richiesta");
		query.whereEqualTo("idDestinatario", user);
		
		try {
			objects=query.find();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Iterator<ParseObject> i = objects.iterator();
		ParseObject po;
		String senderId;
		
		while(i.hasNext())
		{
			po=i.next();
			senderId=po.getParseObject("idMittente").getObjectId();
			
			requests.add(new Request(po.getObjectId(), po.getString("tipoRichiesta"), po.getBoolean("accettata"), new User(getUser(context, senderId)), new User(user)));
		}
		
		return requests;
	}
	
	public static List<Contact> selectContacts(Context context, List<Contact> inputList)
	{
		List<Contact> outputList = new ArrayList<Contact>();
		
		Iterator<Contact> i = inputList.iterator();
		Contact c;
		
		while(i.hasNext())
		{
			c=i.next();
			if(phoneNumberExists(context,c.getPhoneNumber()))
			{
				outputList.add(c);
			}
		}
		
		return outputList;
	}
	
}
