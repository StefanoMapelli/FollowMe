package com.example.followme;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Class for the creation of the local db and his relative table.
 * @author Stefano
 *
 */
public class DatabaseCreationManager extends SQLiteOpenHelper {
	
	public static final String TABLE_PERSONAL_DATA = "personal_data";
	  public static final String COLUMN_ID = "_id";
	  public static final String COLUMN_PHONE_NUMBER = "phone_number";

	  private static final String DATABASE_NAME = "followMeDatabase.db";
	  private static final int DATABASE_VERSION = 1;

	  // Database creation sql statement
	  private static final String DATABASE_CREATE = "create table "
	      + TABLE_PERSONAL_DATA + "(" + COLUMN_ID
	      + " integer primary key autoincrement, " + COLUMN_PHONE_NUMBER
	      + " text not null);";

	public DatabaseCreationManager(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public DatabaseCreationManager(Context context) {

		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL(DATABASE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		 Log.w(DatabaseCreationManager.class.getName(),
			        "Upgrading database from version " + oldVersion + " to "
			            + newVersion + ", which will destroy all old data");
			    db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSONAL_DATA);
			    onCreate(db);
	}

}
