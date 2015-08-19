package com.example.followme;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

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
	public static void insertRequest(Context context, String type, String senderId, String receiverId, String pathId, String destinationId, String fenceId)
	{
		List<ParseObject> objects = null;	
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Utente");
		ParseObject newReq = new ParseObject("Richiesta");
		
		//get sender parse object
		query.whereEqualTo("objectId", senderId);
		ParseObject senderPo=null;		
		try {
			objects=query.find();
			senderPo=objects.get(0);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//get receiver parse object
		query = ParseQuery.getQuery("Utente");
		query.whereEqualTo("objectId", receiverId);
		ParseObject receiverPo=null;		
		try {
			objects=query.find();
			receiverPo=objects.get(0);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//get path parse object
		ParseObject pathPo=null;
		if(pathId != null)
		{
			query = ParseQuery.getQuery("Percorso");
			query.whereEqualTo("objectId", pathId);		
			try {
				objects=query.find();
				pathPo=objects.get(0);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			newReq.put("idPercorso", pathPo);
		}

		//get destination parse object
		ParseObject destinationPo=null;				
		if(destinationId != null)
		{
			query = ParseQuery.getQuery("Destinazione");
			query.whereEqualTo("objectId", destinationId);	
			try {
				objects=query.find();
				destinationPo=objects.get(0);
			} catch (ParseException e) {
				e.printStackTrace();
			}	
			newReq.put("idDestinazione", destinationPo);
		}
		
		//get fence parse object
		ParseObject fencePo=null;	
		if(fenceId != null)
		{
			query = ParseQuery.getQuery("Recinto");
			query.whereEqualTo("objectId", fenceId);	
			try {
				objects=query.find();
				fencePo=objects.get(0);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			newReq.put("idRecinto", fencePo);
		}
				
		newReq.put("tipoRichiesta", type);
		newReq.put("idMittente", senderPo);
		newReq.put("idDestinatario", receiverPo);
		newReq.put("stato", "non visualizzata");
		newReq.saveInBackground();
	}
	
	/**
	 * Method that insert a new path in the Parse db.
	 * @param context
	 * @return : the objectId of the inserted path.
	 */
	public static String insertPath(Context context)
	{
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		ParseObject newPath = new ParseObject("Percorso");
		try {
			newPath.save();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return newPath.getObjectId();
	}
	
	/**
	 * Insert a position in the parse db using parameter data.
	 * @param context
	 * @param pId : path id .
	 * @param latitude
	 * @param longitude
	 */
	public static void insertPosition(Context context, String pId, double latitude, double longitude)
	{
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		
		List<ParseObject> objects = null;	
		ParseObject pathPo=null;
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Percorso");
		query.whereEqualTo("objectId", pId);		
		try {
			objects=query.find();
			pathPo=objects.get(0);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		ParseObject newPos = new ParseObject("Posizione");
		ParseGeoPoint position = new ParseGeoPoint(latitude, longitude);	
		newPos.put("idPercorso", pathPo);
		newPos.put("posizione", position);
		newPos.saveInBackground();
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
		query.whereEqualTo("stato", "non visualizzata");
		
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
			
			requests.add(new Request(po.getObjectId(),
					po.getString("tipoRichiesta"),
					po.getString("stato"),
					new User(getUser(context, senderId)),
					new User(user)));
		}
		
		return requests;
	}
	
	/**
	 * Method that update the state of the requests visualized from non visualizzata to visualizzata.
	 * @param context 
	 * @param requests : list of requests to update
	 */
	public static void visualizeRquests(Context context, List<Request> requests)
	{
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		
		for(Request r : requests)
		{
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Richiesta");
			query.getInBackground(r.getId(), new GetCallback<ParseObject>() {
			  public void done(ParseObject po, ParseException e) {
			    if (e == null) {
			    	po.put("stato", "visualizzata");
			    	po.saveInBackground();
			    }
			  }
			});
		}
	}
	
	/**
	 * Delete the request with the given id.
	 * @param context
	 * @param id : id of the request to delete.
	 */
	public static void deleteRequest(Context context, String id)
	{
		List<ParseObject> objects = null;	
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Richiesta");
		
		//get sender parse object
		query.whereEqualTo("objectId", id);
		ParseObject po=null;		
		try {
			objects=query.find();
			po=objects.get(0);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		po.deleteInBackground();
	}
	
	/**
	 * Method that return the phone numbers of all the users present on parse db
	 * @return a list of contacts
	 */
	public static List<Contact> allContactsOnParse(Context context)
	{
		List<Contact> allNumbers = new ArrayList<Contact>();
		List<ParseObject> objects = null;
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Utente");
		
		try {
			objects=query.find();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Iterator<ParseObject> i = objects.iterator();
		ParseObject po;
		Contact c;
		
		while(i.hasNext())
		{
			po = i.next();
			c=new Contact(po.getObjectId(), null, po.getString("numero"));
			allNumbers.add(c);
		}
		
		return allNumbers;
	}
	
}
