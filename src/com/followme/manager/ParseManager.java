package com.followme.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.followme.object.Contact;
import com.followme.object.Media;
import com.followme.object.Position;
import com.followme.object.Request;
import com.followme.object.User;
import com.google.android.gms.maps.model.LatLng;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ProgressCallback;
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
			pathPo = getPathbyId(context, pathId);
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
	 * Method that insert a new fence in the Parse db.
	 * @param context
	 * @return : the objectId of the inserted fence.
	 */
	public static String insertFence(Context context, int radius, LatLng position)
	{
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		ParseObject newFence = new ParseObject("Recinto");
		try {
			ParseGeoPoint center = new ParseGeoPoint(position.latitude, position.longitude);
			newFence.put("posizione", center);
			newFence.put("raggio", radius);
			newFence.put("uscito", false);
			newFence.save();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return newFence.getObjectId();
	}
	
	/**
	 * Method that insert a new destination in the Parse db.
	 * @param context
	 * @return : the objectId of the inserted fence.
	 */
	public static String insertDestination(Context context, int radius, LatLng position)
	{
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		ParseObject newFence = new ParseObject("Destinazione");
		try {
			ParseGeoPoint center = new ParseGeoPoint(position.latitude, position.longitude);
			newFence.put("posizione", center);
			newFence.put("raggio", radius);
			newFence.put("arrivato", false);
			newFence.save();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return newFence.getObjectId();
	}
	
	/**
	 * Insert a position in the parse db using parameter data.
	 * @param context
	 * @param pId : path id .
	 * @param latitude
	 * @param longitude
	 */
	public static String insertPosition(Context context, ParseObject pathPo, double latitude, double longitude, int counter)
	{
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		
		ParseObject newPos = new ParseObject("Posizione");
		ParseGeoPoint position = new ParseGeoPoint(latitude, longitude);	
		newPos.put("idPercorso", pathPo);
		newPos.put("posizione", position);
		newPos.put("contatore", counter);
		try {
			newPos.save();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return newPos.getObjectId();
	}
	
	/**
	 * Insert a new photo in the parse db.
	 * @param context
	 * @param photo
	 */
	public static void insertPhoto(final Context context, Media photo)
	{
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");

		final ParseObject newPhoto = new ParseObject("Foto");
		//get position parse object
		if(photo.getPosition() != null)
		{
			ParseObject po=null;
			po = getPositionbyId(context, photo.getPosition().getId());
			newPhoto.put("idPosizione", po);
		}
		final ParseFile file = new ParseFile("photo.jpg", photo.getMedia());
		final String title = photo.getTitle();
		file.saveInBackground(new SaveCallback() {
			  public void done(ParseException e) {
				  newPhoto.put("file", file);
					newPhoto.put("didascalia", title);
					newPhoto.saveInBackground(new SaveCallback(){
						public void done(ParseException e)
						{
							Toast.makeText(context, "Image uploaded!", Toast.LENGTH_LONG).show();
						}
					});
			  }
			});	
	}
	
	/**
	 * Insert a new video in the parse db.
	 * @param context
	 * @param video
	 */
	public static void insertVideo(final Context context, Media video)
	{
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");

		final ParseObject newPhoto = new ParseObject("Video");
		//get position parse object
		if(video.getPosition() != null)
		{
			ParseObject po=null;
			po = getPositionbyId(context, video.getPosition().getId());
			newPhoto.put("idPosizione", po);
		}
		final ParseFile file = new ParseFile("video.mp4", video.getMedia());
		final String title = video.getTitle();
		Log.i("UPLOAD","ORA INIZIO UPLOAD:"+new Date().toString());
		file.saveInBackground(new SaveCallback() {
			  public void done(ParseException e) {
				  newPhoto.put("file", file);
					newPhoto.put("didascalia", title);
					newPhoto.saveInBackground(new SaveCallback(){
						public void done(ParseException e)
						{
							Log.i("UPLOAD","ORA FINE UPLOAD:"+new Date().toString());
							Toast.makeText(context, "Video uploaded!", Toast.LENGTH_LONG).show();
						}
					});
			  }
		}, new ProgressCallback(){
			public void done(Integer percentDone) {
				Log.i("PROGRESS",percentDone.toString());
			}				 
		});	
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
	public static List<Request> checkRequests(Context context, ParseObject user)
	{
		List<ParseObject> objects = null;
		List<Request> requests = new ArrayList<Request>();
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
	
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
	 * Method that get the path parse object given his id.
	 * @param context
	 * @param pathId
	 * @return
	 */
	public static ParseObject getPathbyId(Context context, String pathId)
	{
		List<ParseObject> objects = null;
		ParseObject pathPo=null;
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Percorso");
		query.whereEqualTo("objectId", pathId);		
		try {
			objects=query.find();
			pathPo=objects.get(0);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return pathPo;
	}
	
	/**
	 * Method that give the fence parse object given his id.
	 * @param context
	 * @param id
	 * @return
	 */
	public static ParseObject getFencebyId(Context context, String id)
	{
		List<ParseObject> objects = null;
		ParseObject pathPo=null;
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Recinto");
		query.whereEqualTo("objectId", id);		
		try {
			objects=query.find();
			pathPo=objects.get(0);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return pathPo;
	}
	
	/**
	 * Method that give the position parse object given his id.
	 * @param context
	 * @param positionId
	 * @return
	 */
	public static ParseObject getPositionbyId(Context context, String positionId)
	{
		List<ParseObject> objects = null;
		ParseObject pathPo=null;
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Posizione");
		query.whereEqualTo("objectId", positionId);		
		try {
			objects=query.find();
			pathPo=objects.get(0);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return pathPo;
	}
	
	/**
	 * Method that gets the path of a relative request, if it exists
	 * @return a list of contacts
	 */
	public static ParseObject getPathOfRequest(Context context, Request request)
	{
		ParseObject path=null;
		List<ParseObject> objects = null;
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Richiesta");
		
		query.whereEqualTo("objectId", request.getId());
		try {
			objects=query.find();
			Iterator<ParseObject> i = objects.iterator();
			ParseObject po;			
			if(i.hasNext())
			{
				po = i.next();
				path=po.getParseObject("idPercorso");	
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}	
		return path;
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
	
	
	/**
	 * Method to get the new shared positions of shared path
	 * @return a list of positions
	 */
	public static List<Position> getNewSharedPosition(Context context, ParseObject path, int counter)
	{
		List<Position> newPositions=new ArrayList<Position>();
		List<ParseObject> objects = null;
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Posizione");
		query.whereEqualTo("idPercorso", path);
		query.whereGreaterThan("contatore", counter);
		
		try {
			objects=query.find();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Iterator<ParseObject> i = objects.iterator();
		ParseObject po;
		Position posItem;
		
		while(i.hasNext())
		{
			po = i.next();
			posItem=new Position(po.getParseGeoPoint("posizione").getLatitude(),
					po.getParseGeoPoint("posizione").getLongitude(),
					po.getInt("contatore"));
			posItem.setId(po.getObjectId());
			newPositions.add(posItem);
		}
		
		return newPositions;
	}
	/**
	 * Method that return a list of photo taken from the given position.
	 * @param context
	 * @param position
	 * @return
	 */
	public static List<Media> getPhotosFromPosition(Context context, Position position)
	{
		List<Media> newPhotos = new ArrayList<Media>();
		List<ParseObject> objects = null;
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		
		ParseObject positionPo = getPositionbyId(context, position.getId());
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Foto");
		query.whereEqualTo("idPosizione", positionPo);
		
		try {
			objects=query.find();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Iterator<ParseObject> i = objects.iterator();

		ParseObject po;
		Media mediaItem=null;
		
		while(i.hasNext())
		{
			po = i.next();
			try {
				mediaItem = new Media(po.getParseFile("file").getData(),
									  po.getString("didascalia"),
									  position);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			newPhotos.add(mediaItem);
		}
		return newPhotos;
	}

	 /**
	 * Method that return a list of video taken from the given position. 
	  * @param context
	  * @param position
	  * @return
	  */
	public static List<Media> getVideosFromPosition(Context context, Position position)
	{
		List<Media> newVides = new ArrayList<Media>();
		List<ParseObject> objects = null;
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		
		ParseObject positionPo = getPositionbyId(context, position.getId());
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Video");
		query.whereEqualTo("idPosizione", positionPo);
		
		try {
			objects=query.find();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Iterator<ParseObject> i = objects.iterator();

		ParseObject po;
		Media mediaItem=null;
		
		while(i.hasNext())
		{
			po = i.next();
			try {
				mediaItem = new Media(po.getParseFile("file").getData(),
									  po.getString("didascalia"),
									  position);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			newVides.add(mediaItem);
		}
		return newVides;
	}

	public static boolean isInTheFence(Context context, String idFence) {
		
		List<ParseObject> objects = null;
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
	
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Recinto");
		query.whereEqualTo("objectId", idFence);
		query.whereEqualTo("uscito", true);
		
		try {
			objects=query.find();
			if(objects.isEmpty())
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
		
		return true;
		
	}

	
	/**
	 * This method gets the fence parseObject of the request
	 * @param context
	 * @param fenceRequest request of the fence
	 */
	public static ParseObject getFenceOfRequest(Context context, Request request) 
	{

		ParseObject fence=null;
		List<ParseObject> objects = null;
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Richiesta");
		
		query.whereEqualTo("objectId", request.getId());
		try {
			objects=query.find();
			Iterator<ParseObject> i = objects.iterator();
			ParseObject po;			
			if(i.hasNext())
			{
				po = i.next();
				fence=po.getParseObject("idRecinto");	
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}	
		
		return getFencebyId(context, fence.getObjectId());
		
	}

	public static void updateFenceStatus(Context context, ParseObject fenceParseObject, boolean isExit) {
				
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		fenceParseObject.put("uscito", isExit);
		fenceParseObject.saveInBackground();
	}

	
	/**
	 * This method return true if the user arrives in the destination's zone
	 * @param destinationControlActivity
	 * @param idDestination
	 * @return
	 */
	public static boolean isInTheDestination(
			Context context,
			String idDestination) {
		
		List<ParseObject> objects = null;
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
	
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Destinazione");
		query.whereEqualTo("objectId", idDestination);
		query.whereEqualTo("arrivato", true);
		
		try {
			objects=query.find();
			if(objects.isEmpty())
			{
				return false;
			}
			else
			{
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	
	/**
	 * This method returns a destination parse object of the request
	 * @param context
	 * @param request
	 * @return
	 */
	public static ParseObject getDestinationOfRequest(
			Context context,
			Request request) 
	{
		
		ParseObject destination=null;
		List<ParseObject> objects = null;
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Richiesta");
		
		query.whereEqualTo("objectId", request.getId());
		try {
			objects=query.find();
			Iterator<ParseObject> i = objects.iterator();
			ParseObject po;			
			if(i.hasNext())
			{
				po = i.next();
				destination=po.getParseObject("idDestinazione");	
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}	
		
		return getDestinationbyId(context, destination.getObjectId());
	}

	
	
	private static ParseObject getDestinationbyId(Context context,
			String id) {
		
		List<ParseObject> objects = null;
		ParseObject destPo=null;
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Destinazione");
		query.whereEqualTo("objectId", id);		
		try {
			objects=query.find();
			destPo=objects.get(0);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return destPo;
		
	}

	public static void updateDestinationStatus(
			Context context,
			ParseObject destinationParseObject, boolean isArrived) {
		
		Parse.initialize(context,"x9hwNnRfTCCYGXPVJNKaR7zYTIMOdKeLkerRQJT2" ,"hi7GT6rUlp9uTfw6XQzdEjnTqwgPnRPoikPehgVf");
		destinationParseObject.put("arrivato", isArrived);
		destinationParseObject.saveInBackground();
		
	}
	
}
