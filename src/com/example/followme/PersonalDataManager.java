package com.example.followme;

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
	  public static void insertPhoneNumber(String phoneNumber) 
	  {
		  ContentValues values = new ContentValues();
		  values.put(DatabaseCreationManager.COLUMN_PHONE_NUMBER, phoneNumber);
		  database.insert(DatabaseCreationManager.TABLE_PERSONAL_DATA, null,values);
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
	  
	  public static boolean phoneNumberExists()
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
		  if(phoneNumberExists())
		  {
			  Cursor cursor =database.query(DatabaseCreationManager.TABLE_PERSONAL_DATA, allColumns, null, null, null, null, null);
		  
			  cursor.moveToFirst();
		  
			  String phoneNumber=cursor.getString(1);
		  
			  return phoneNumber;
		  }
		  else
		  {
			  return "";
		  }
	  }
}
