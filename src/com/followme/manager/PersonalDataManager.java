package com.followme.manager;

import java.util.Iterator;
import java.util.List;

import com.followme.object.Media;
import com.followme.object.Position;
import com.google.android.gms.maps.model.LatLng;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Class for management of db table: PERSONAL_DATA
 * Insert and update method are implemented
 * @author Stefano
 *
 */
public class PersonalDataManager {
	
	// Database fields
	  private static SQLiteDatabase database;
	  private static DatabaseCreationManager dbHelper;
	  private static String[] allColumns = { DatabaseCreationManager.COLUMN_ID,
		  DatabaseCreationManager.COLUMN_USER_ID,
	      DatabaseCreationManager.COLUMN_PHONE_NUMBER };

	  
	  public PersonalDataManager(Context context) {
	    dbHelper = new DatabaseCreationManager(context);
	  }
	  
	  public static void open() throws SQLException {
		    database = dbHelper.getWritableDatabase();
		  }
	  
	  public static void close() {
		    dbHelper.close();
		  }
	  
	  /**
	   * Insertion of a new phone number in the database, in the table of personal data
	   * @param phoneNumber
	   */
	  public static void insertUser(String phoneNumber, String userId) 
	  {
		  ContentValues values = new ContentValues();
		  values.put(DatabaseCreationManager.COLUMN_PHONE_NUMBER, phoneNumber);
		  values.put(DatabaseCreationManager.COLUMN_USER_ID, userId);
		  database.insert(DatabaseCreationManager.TABLE_PERSONAL_DATA, null,values);
	  }
	  
	  /**
	   * Insertion of a new path in the database, in the table of path. Return the id of the path
	   * inserted yet 
	   * @param path
	   * @return the id of the inserted path
	   */
	  public static int insertPath(String title, String ownerUser) 
	  {
		  ContentValues values = new ContentValues();
		  values.put(DatabaseCreationManager.COLUMN_TITLE, title);
		  values.put(DatabaseCreationManager.COLUMN_OWNER, ownerUser);
		  long pathId=database.insert(DatabaseCreationManager.TABLE_PATH, null,values);
		  return (int) pathId;
	  }
	  
	  /**
	   * Insertion of a list of position in the database
	   * @param poistionList list of position to be inserted
	   * @param path of the list of position
	   */
	  public static void insertPositionList(List<Position> positionList, int path) 
	  {		  
		  Iterator<Position> i = positionList.iterator();
		  Position posItem=null;
			
		  while(i.hasNext())
		  {
			  ContentValues values = new ContentValues();
			  posItem = i.next();
			  values.put(DatabaseCreationManager.COLUMN_LATITUDE_POSITION, posItem.getLatitude());
			  values.put(DatabaseCreationManager.COLUMN_LONGITUDE_POSITION, posItem.getLongitude());
			  values.put(DatabaseCreationManager.COLUMN_COUNTER, posItem.getCounter());
			  values.put(DatabaseCreationManager.COLUMN_PATH, path);
			  database.insert(DatabaseCreationManager.TABLE_POSITION, null,values);
		  }
	  }
	  
	  /**
	   * Insertion of a list of photos in the database
	   * @param list of photo objects
	   */
	  public static void insertPhotoList(List<Media> photoList) 
	  {
		  
		  
		  Iterator<Media> i = photoList.iterator();
		  Media photoItem=null;
			
		  while(i.hasNext())
		  {
			  ContentValues values = new ContentValues();
			  photoItem = i.next();
			  //values.put(DatabaseCreationManager.COLUMN_FILE, photoItem.getMediaString());TODO
			  values.put(DatabaseCreationManager.COLUMN_TITLE, photoItem.getTitle());
			 
			  //ricerco la posizione della foto nel database e ne prendo l'id per l'inserimento
			  String[] columns=new String[1];
			  columns[0]=DatabaseCreationManager.COLUMN_POSITION_ID;
			  Cursor cursor =database.query(DatabaseCreationManager.TABLE_POSITION, columns, DatabaseCreationManager.COLUMN_LATITUDE_POSITION+"="+photoItem.getPosition().getLatitude()+" AND "+DatabaseCreationManager.COLUMN_LONGITUDE_POSITION+"="+photoItem.getPosition().getLongitude()+" AND "+DatabaseCreationManager.COLUMN_COUNTER+"="+photoItem.getPosition().getCounter(), null, null, null, null);
			  cursor.moveToFirst();
			  String positionId=cursor.getString(0);
			  			  
			  values.put(DatabaseCreationManager.COLUMN_POSITION, positionId);
			  //inserisco la foto nel db
			  database.insert(DatabaseCreationManager.TABLE_PHOTO, null, values);
		  }
	  }
	  
	  /**
	   * Insertion of a list of videos in the database
	   * @param list of video objects
	   */
	  public static void insertVideoList(List<Media> videoList) 
	  {
		  
		  
		  Iterator<Media> i = videoList.iterator();
		  Media videoItem=null;
			
		  while(i.hasNext())
		  {
			  ContentValues values = new ContentValues();
			  videoItem = i.next();
			  //values.put(DatabaseCreationManager.COLUMN_FILE, videoItem.getMediaString());TODO
			  values.put(DatabaseCreationManager.COLUMN_TITLE, videoItem.getTitle());
			 
			  //ricerco la posizione della foto nel database e ne prendo l'id per l'inserimento
			  String[] columns=new String[1];
			  columns[0]=DatabaseCreationManager.COLUMN_POSITION_ID;
			  Cursor cursor =database.query(DatabaseCreationManager.TABLE_POSITION, columns, DatabaseCreationManager.COLUMN_LATITUDE_POSITION+"="+videoItem.getPosition().getLatitude()+" AND "+DatabaseCreationManager.COLUMN_LONGITUDE_POSITION+"="+videoItem.getPosition().getLongitude()+" AND "+DatabaseCreationManager.COLUMN_COUNTER+"="+videoItem.getPosition().getCounter(), null, null, null, null);
			  cursor.moveToFirst();
			  String positionId=cursor.getString(0);
			  			  
			  values.put(DatabaseCreationManager.COLUMN_POSITION, positionId);
			  //inserisco la foto nel db
			  database.insert(DatabaseCreationManager.TABLE_VIDEO, null, values);
		  }
	  }
	  
	  
	  /**
	   * Insertion of a list of audios in the database
	   * @param list of video objects
	   */
	  public static void insertAudioList(List<Media> audioList) 
	  {
		  
		  
		  Iterator<Media> i = audioList.iterator();
		  Media audioItem=null;
			
		  while(i.hasNext())
		  {
			  ContentValues values = new ContentValues();
			  audioItem = i.next();
			  //values.put(DatabaseCreationManager.COLUMN_FILE, audioItem.getMediaString());/TODO
			  values.put(DatabaseCreationManager.COLUMN_TITLE, audioItem.getTitle());
			 
			  //ricerco la posizione della foto nel database e ne prendo l'id per l'inserimento
			  String[] columns=new String[1];
			  columns[0]=DatabaseCreationManager.COLUMN_POSITION_ID;
			  Cursor cursor =database.query(DatabaseCreationManager.TABLE_POSITION, columns, DatabaseCreationManager.COLUMN_LATITUDE_POSITION+"="+audioItem.getPosition().getLatitude()+" AND "+DatabaseCreationManager.COLUMN_LONGITUDE_POSITION+"="+audioItem.getPosition().getLongitude()+" AND "+DatabaseCreationManager.COLUMN_COUNTER+"="+audioItem.getPosition().getCounter(), null, null, null, null);
			  cursor.moveToFirst();
			  String positionId=cursor.getString(0);
			  			  
			  values.put(DatabaseCreationManager.COLUMN_POSITION, positionId);
			  //inserisco la foto nel db
			  database.insert(DatabaseCreationManager.TABLE_AUDIO, null, values);
		  }
	  }
	  
	  
	  /**
	   * Update of the phone number saved in the table personalData
	   * @param newPhoneNumber
	   */
	  public static void updatePhoneNumber(String newPhoneNumber)
	  {
		  ContentValues values = new ContentValues();
		  values.put(DatabaseCreationManager.COLUMN_PHONE_NUMBER, newPhoneNumber);
		  database.update(DatabaseCreationManager.TABLE_PERSONAL_DATA, values, null, null);
	  }
	  
	  public static boolean userExists()
	  {
		  if(database.query(DatabaseCreationManager.TABLE_PERSONAL_DATA, allColumns, null, null, null, null, null).getCount()>0)
		  {
			  return true;
		  }
		  else
		  {
			  return false;
		  }				  
	  }
	  
	  /**
	   * Getter del numero di telefono del db, nell tabella dati personali
	   * 
	   * @return numero di telefono salvato nel db
	   */
	  public static String getPhoneNumber()
	  {
		  if(userExists())
		  {
			  Cursor cursor =database.query(DatabaseCreationManager.TABLE_PERSONAL_DATA, allColumns, null, null, null, null, null);
		  
			  cursor.moveToFirst();
		  
			  String phoneNumber=cursor.getString(2);
		  
			  return phoneNumber;
		  }
		  else
		  {
			  return "";
		  }
	  }
	  
	  public static String getUserId()
	  {
		  if(userExists())
		  {
			  Cursor cursor =database.query(DatabaseCreationManager.TABLE_PERSONAL_DATA, allColumns, null, null, null, null, null);
		  
			  cursor.moveToFirst();
		  
			  String userId=cursor.getString(1);
		  
			  return userId;
		  }
		  else
		  {
			  return "";
		  }
	  }
}
