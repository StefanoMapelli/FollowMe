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
	  private SQLiteDatabase database;
	  private DatabaseCreationManager dbHelper;
	  private String[] allColumns = { DatabaseCreationManager.COLUMN_ID,
	      DatabaseCreationManager.COLUMN_PHONE_NUMBER };

	  
	  public PersonalDataManager(Context context) {
	    dbHelper = new DatabaseCreationManager(context);
	  }
	  
	  public void open() throws SQLException {
		    database = dbHelper.getWritableDatabase();
		  }
	  
	  public void close() {
		    dbHelper.close();
		  }
	  
	  /**
	   * Insertion of a new phone number in the database, in the table of personal data
	   * @param phoneNumber
	   */
	  public void insertPhoneNumber(String phoneNumber) 
	  {
		  ContentValues values = new ContentValues();
		  values.put(DatabaseCreationManager.COLUMN_PHONE_NUMBER, phoneNumber);
		  database.insert(DatabaseCreationManager.TABLE_PERSONAL_DATA, null,values);
	  }
	  
	  /**
	   * Update of the phone number saved in the table personalData
	   * @param newPhoneNumber
	   */
	  public void updatePhoneNumber(String newPhoneNumber)
	  {
		  ContentValues values = new ContentValues();
		  values.put(DatabaseCreationManager.COLUMN_PHONE_NUMBER, newPhoneNumber);
		  database.update(DatabaseCreationManager.TABLE_PERSONAL_DATA, values, null, null);
	  }
}
