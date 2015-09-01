package com.followme.manager;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.followme.object.Contact;
import com.followme.object.Media;
import com.followme.object.Path;
import com.followme.object.Position;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.MediaStore.Files;

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
	   * Get all the paths in the database, in the table of path.
	   * @return list of all the path in the db
	   */
	  public static ArrayList<Path> getAllPaths() 
	  {
		  ArrayList<Path> pathList=new ArrayList<Path>();  
		  
		  String[] allPathColumns = new String[3];
		  allPathColumns[0]=DatabaseCreationManager.COLUMN_PATH_ID;
		  allPathColumns[1]=DatabaseCreationManager.COLUMN_OWNER;
		  allPathColumns[2]=DatabaseCreationManager.COLUMN_TITLE;
		  
		  Cursor cursor =database.query(DatabaseCreationManager.TABLE_PATH, allPathColumns, null, null, null, null, null);

		  if(cursor.moveToFirst())
		  {
			  do
			  {
				  Path pathObject=new Path();
				  pathObject.setId(cursor.getInt(0)+"");
				  pathObject.setOwner(cursor.getString(1));
				  pathObject.setTitle(cursor.getString(2));
				  pathList.add(pathObject);
				  cursor.moveToNext();
			  }
			  while(!cursor.isAfterLast());
		  }
		  cursor.close();
		  return pathList;
	  }
	  
	  
	  /**
	   * Insertion of a new contact in the database, in the table of contact. Return the id of the contact
	   * inserted yet 
	   * @param contact
	   * @return the id of the inserted contact
	   */
	  public static int insertContact(Contact contact) 
	  {
		  ContentValues values = new ContentValues();
		  values.put(DatabaseCreationManager.COLUMN_CONTACT_PARSE_ID, contact.getId());
		  values.put(DatabaseCreationManager.COLUMN_NUMBER, contact.getPhoneNumber());
		  values.put(DatabaseCreationManager.COLUMN_NAME, contact.getName());
		  long contactId=database.insert(DatabaseCreationManager.TABLE_CONTACT, null,values);
		  return (int) contactId;
	  }
	  
	  
	  /**
	   * Get all the contacts in the database, in the table of contact.
	   * @return list of all the contacts in the db
	   */
	  public static ArrayList<Contact> getAllContacts() 
	  {
		  ArrayList<Contact> contactList=new ArrayList<Contact>();  
		  
		  String[] allColumnContact = new String[3];
		  allColumnContact[0]=DatabaseCreationManager.COLUMN_CONTACT_PARSE_ID;
		  allColumnContact[1]=DatabaseCreationManager.COLUMN_NUMBER;
		  allColumnContact[2]=DatabaseCreationManager.COLUMN_NAME;

		  Cursor cursor =database.query(DatabaseCreationManager.TABLE_CONTACT, allColumnContact, null, null, null, null, null);

		  if(cursor.moveToFirst())
		  {
			  do
			  {
				  Contact contactObject=new Contact(cursor.getString(0)+"",
						  cursor.getString(2),
						  cursor.getString(1)
						  );
				  contactList.add(contactObject);
				  cursor.moveToNext();
			  }
			  while(!cursor.isAfterLast());
		  }
		  cursor.close();
		  return contactList;
	  }
	  
	  
	  /**
	   * Get the name of a contact in the database with specified 
	   * @return list of all the contacts in the db
	   */
	  public static String getNameOfContact(String number) 
	  {		  
		  String[] allColumnContact = new String[1];
		  allColumnContact[0]=DatabaseCreationManager.COLUMN_NAME;
		  
		  Cursor cursor =database.query(DatabaseCreationManager.TABLE_CONTACT, allColumnContact, DatabaseCreationManager.COLUMN_NUMBER+"="+number, null, null, null, null);

		  if(cursor.moveToFirst())
		  {
			  String result=cursor.getString(0);
			  cursor.close();
			  return result;
		  }
		  cursor.close();
		  return number;
	  }
	  
	  
	  /**
	   * Delete all rows of the table contact
	   */
	  public static void deleteAllContacts() 
	  {		  
		  database.delete(DatabaseCreationManager.TABLE_CONTACT, null, null);
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
			  values.put(DatabaseCreationManager.COLUMN_POSITION_PARSE_ID, posItem.getId());
			  database.insert(DatabaseCreationManager.TABLE_POSITION, null,values);
		  }
	  }
	  
	  
	  /**
	   * Get all the position of a path in the database, in the table of position.
	   * @param idPath id of the searched path
	   * @return list of all the position of the selected path in the db
	   */
	  public static ArrayList<Position> getAllPositionsOfPath(String idPath) 
	  {
		  ArrayList<Position> positionList=new ArrayList<Position>();  
		  
		  String[] allPositionColumns = new String[5];
		  allPositionColumns[0]=DatabaseCreationManager.COLUMN_POSITION_ID;
		  allPositionColumns[1]=DatabaseCreationManager.COLUMN_PATH;
		  allPositionColumns[2]=DatabaseCreationManager.COLUMN_COUNTER;
		  allPositionColumns[3]=DatabaseCreationManager.COLUMN_LATITUDE_POSITION;
		  allPositionColumns[4]=DatabaseCreationManager.COLUMN_LONGITUDE_POSITION;
		  
		  Cursor cursor =database.query(DatabaseCreationManager.TABLE_POSITION, allPositionColumns, "id_path="+idPath, null, null, null, "counter asc");
		  
		  if(cursor.moveToFirst())
		  {

			  do
			  {
				  Position positionObject=new Position(cursor.getDouble(3),cursor.getDouble(4),cursor.getInt(2));
				  positionObject.setId(cursor.getInt(0)+"");
				  positionObject.setId(cursor.getInt(1)+"");
				  positionList.add(positionObject);
				  cursor.moveToNext();
			  }
			  while(!cursor.isAfterLast());
		  }
		  cursor.close();
		  return positionList;
	  }

	  /**
	   * Insertion of a list of photos in the database
	   * @param list of photo objects
	   */
	  public static void insertPhotoList(List<Media> photoList, String idPath) 
	  {
		  
		  ArrayList<Position> posAll=getAllPositionsOfPath(idPath);
		  Iterator<Media> i = photoList.iterator();
		  Media photoItem=null;
			
		  while(i.hasNext())
		  {
			  ContentValues values = new ContentValues();
			  photoItem = i.next();
			  values.put(DatabaseCreationManager.COLUMN_FILE_PATH, photoItem.getFilePath());
			  values.put(DatabaseCreationManager.COLUMN_TITLE, photoItem.getTitle());
			  
			 
			  //ricerco la posizione della foto nel database e ne prendo l'id per l'inserimento
			  String[] columns=new String[1];
			  columns[0]=DatabaseCreationManager.COLUMN_POSITION_ID;
			  Cursor cursor =database.query(DatabaseCreationManager.TABLE_POSITION, columns, DatabaseCreationManager.COLUMN_POSITION_PARSE_ID+"="+photoItem.getPosition().getId(), null, null, null, null);
			  cursor.moveToFirst();
			  String positionId=cursor.getString(0);
			  cursor.close();
			  			  
			  values.put(DatabaseCreationManager.COLUMN_POSITION, positionId);
			  //inserisco la foto nel db
			  database.insert(DatabaseCreationManager.TABLE_PHOTO, null, values);
		  }
	  }
	  
	  
	  /**
	   * Get all the photos of a path in the database, in the table of photo.
	   * @param idPath id of the searched path
	   * @return list of all the photos of the selected path in the db
	   */
	  public static ArrayList<Media> getAllPhotosOfPath(String idPath) 
	  {
		  ArrayList<Media> photoList=new ArrayList<Media>();  

		  Cursor cursor=database.rawQuery("SELECT photo.id_photo, photo.position, photo.title, photo.file_path, position.id_position, position.counter, position.latitude, position.longitude FROM photo, position WHERE photo.position=position.id_position AND position.id_path="+idPath, null);

		  if(cursor.moveToFirst())
		  {
			  do
			  {
				  Media mediaObject=new Media();
				  Position position=new Position(cursor.getDouble(6),cursor.getDouble(7),cursor.getInt(5));
				  position.setId(cursor.getInt(4)+"");
				  mediaObject.setPosition(position);
				  String filePath= cursor.getString(3);
				  
				  //converto pathFile in array di byte
				  File mediaFile=new File(filePath);
				  FileInputStream fileInputStream = null;
			      byte[] bFile = new byte[(int) mediaFile.length()];
			      try
			      {
			         //convert file into array of bytes
			         fileInputStream = new FileInputStream(mediaFile);
			         fileInputStream.read(bFile);
			         fileInputStream.close();
			      }
			      catch (Exception e)
			      {
			         e.printStackTrace();
			      }
				  mediaObject.setMedia(bFile);
				  mediaObject.setTitle(cursor.getString(2));
				  photoList.add(mediaObject);
				  cursor.moveToNext();
			  }
			  while(!cursor.isAfterLast());
		  }
		  cursor.close();
		  return photoList;
	  }
	  
	  /**
	   * Insertion of a list of videos in the database
	 * @param idPath 
	   * @param list of video objects
	   */
	  public static void insertVideoList(List<Media> videoList, String idPath) 
	  {
		  
		  Iterator<Media> i = videoList.iterator();
		  Media videoItem=null;
			
		  while(i.hasNext())
		  {		  
			  ContentValues values = new ContentValues();
			  videoItem = i.next();
			  values.put(DatabaseCreationManager.COLUMN_FILE_PATH, videoItem.getFilePath());
			  values.put(DatabaseCreationManager.COLUMN_TITLE, videoItem.getTitle());
			 
			  //ricerco la posizione della foto nel database e ne prendo l'id per l'inserimento
			  String[] columns=new String[1];
			  columns[0]=DatabaseCreationManager.COLUMN_POSITION_ID;
			  Cursor cursor =database.query(DatabaseCreationManager.TABLE_POSITION, columns, DatabaseCreationManager.COLUMN_POSITION_PARSE_ID+"="+videoItem.getPosition().getId(), null, null, null, null);
			  cursor.moveToFirst();
			  String positionId=cursor.getString(0);

			  values.put(DatabaseCreationManager.COLUMN_POSITION, positionId);
			  //inserisco la foto nel db
			  cursor.close();
			  database.insert(DatabaseCreationManager.TABLE_VIDEO, null, values);
		  }
	  }




	  /**
	   * Get all the videos of a path in the database, in the table of photo.
	   * @param idPath id of the searched path
	   * @return list of all the videos of the selected path in the db
	   */
	  public static ArrayList<Media> getAllVideosOfPath(String idPath) 
	  {
		  ArrayList<Media> videoList=new ArrayList<Media>();  

		  Cursor cursor=database.rawQuery("SELECT video.id_video, video.position, video.title, video.file_path, position.id_position, position.counter, position.latitude, position.longitude FROM video, position WHERE video.position=position.id_position AND position.id_path="+idPath, null);
		  if(cursor.getCount() > 0)
		  {
			  cursor.moveToFirst();
			  do
			  {
				  Media mediaObject=new Media();
				  Position position=new Position(cursor.getDouble(6),
						  						 cursor.getDouble(7),
						  						 cursor.getInt(5));
				  position.setId(cursor.getInt(4)+"");
				  mediaObject.setPosition(position);
				  String filePath= cursor.getString(3);
				  
				  //converto pathFile in array di byte
				  File mediaFile=new File(filePath);
				  FileInputStream fileInputStream = null;
			      byte[] bFile = new byte[(int) mediaFile.length()];
			      try
			      {
			         //convert file into array of bytes
			         fileInputStream = new FileInputStream(mediaFile);
			         fileInputStream.read(bFile);
			         fileInputStream.close();
			      }
			      catch (Exception e)
			      {
			         e.printStackTrace();
			      }
				  mediaObject.setMedia(bFile);
				  mediaObject.setTitle(cursor.getString(2));
				  videoList.add(mediaObject);
				  cursor.moveToNext();
			  }
			  while(!cursor.isAfterLast());
		  }
		  cursor.close();
		  return videoList;
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
			  values.put(DatabaseCreationManager.COLUMN_FILE, audioItem.getMedia());
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
			  cursor.close();
		  }
	  }


	  /**
	   * Get all the audios of a path in the database, in the table of photo.
	   * @param idPath id of the searched path
	   * @return list of all the audios of the selected path in the db
	   */
	  public static ArrayList<Media> getAllAudiosOfPath(String idPath) 
	  {
		  ArrayList<Media> audioList=new ArrayList<Media>();  

		  Cursor cursor=database.rawQuery("SELECT audio.id_audio, audio.position, audio.title, audio.file, position.id_position, position.counter, position.latitude, position.longitude FROM video, position WHERE audio.position=position.id_position AND position.id_path="+idPath, null);

		  cursor.moveToFirst();
		  if(cursor.moveToFirst())
		  {

			  do
			  {
				  Media mediaObject=new Media();
				  Position position=new Position(cursor.getDouble(6),cursor.getDouble(7),cursor.getInt(5));
				  position.setId(cursor.getInt(4)+"");
				  mediaObject.setPosition(position);
				  mediaObject.setMedia(cursor.getBlob(3));
				  mediaObject.setTitle(cursor.getString(2));
				  audioList.add(mediaObject);
				  cursor.moveToNext();
			  }
			  while(!cursor.isAfterLast());
		  }
		  cursor.close();
		  return audioList;
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
		  
			  cursor.close();
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
		  
			  
			  cursor.close();
			  return userId;
		  }
		  else
		  {
			  return "";
		  }
	  }
}
