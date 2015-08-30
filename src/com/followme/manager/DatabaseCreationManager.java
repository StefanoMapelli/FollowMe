package com.followme.manager;

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
	public static final String COLUMN_USER_ID = "user_id";
	 
	public static final String TABLE_PATH = "path";
	public static final String COLUMN_OWNER = "owner";
	public static final String COLUMN_PATH_ID = "id_path";
	
	public static final String TABLE_CONTACT = "contact";
	public static final String COLUMN_CONTACT_ID="id_contact";
	public static final String COLUMN_NUMBER = "number";
	public static final String COLUMN_NAME = "name";
	
	public static final String TABLE_POSITION = "position";
	public static final String COLUMN_POSITION_ID = "id_position";
	public static final String COLUMN_PATH = "id_path";
	public static final String COLUMN_COUNTER = "counter";
	public static final String COLUMN_LATITUDE_POSITION = "latitude";
	public static final String COLUMN_LONGITUDE_POSITION = "longitude";
	
	public static final String TABLE_PHOTO = "photo";
	public static final String COLUMN_PHOTO_ID = "id_photo";
	public static final String COLUMN_POSITION = "position";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_FILE = "file";
	
	public static final String TABLE_VIDEO = "video";
	public static final String COLUMN_VIDEO_ID = "id_video";
	
	public static final String TABLE_AUDIO = "audio";
	public static final String COLUMN_AUDIO_ID = "id_audio";
	
	

	private static final String DATABASE_NAME = "followMeDatabase.db";
	private static final int DATABASE_VERSION = 1;

	  // Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
	      + TABLE_PERSONAL_DATA + "(" + COLUMN_ID
	      + " INTEGER primary key autoincrement, " + COLUMN_USER_ID
	      + " TEXT not null, "+ COLUMN_PHONE_NUMBER
	      + " TEXT not null);";
	
	private static final String PATH_CREATE= "create table "
		      + TABLE_PATH + "(" + COLUMN_PATH_ID
		      + " INTEGER primary key autoincrement, " 
		      + COLUMN_OWNER + " TEXT not null, "
		      + COLUMN_TITLE + " TEXT not null);";
	
	private static final String CONTACT_CREATE= "create table "
		      + TABLE_CONTACT + "(" + COLUMN_CONTACT_ID
		      + " INTEGER primary key, " 
		      + COLUMN_NUMBER + " TEXT not null, "
		      + COLUMN_NAME + " TEXT not null);";
	
	private static final String POSITION_CREATE = "create table "
		      + TABLE_POSITION 
		      + "(" + COLUMN_POSITION_ID + " INTEGER primary key autoincrement, " 
		      + COLUMN_PATH + " INTEGER not null, "
		      + COLUMN_COUNTER + " INTEGER not null, "
		      + COLUMN_LATITUDE_POSITION + " REAL not null, "
		      + COLUMN_LONGITUDE_POSITION + " REAL not null);";
	
	private static final String PHOTO_CREATE  = "create table "
		      + TABLE_PHOTO 
		      + "(" + COLUMN_PHOTO_ID + " INTEGER primary key autoincrement, " 
		      + COLUMN_POSITION + " INTEGER not null, "
		      + COLUMN_TITLE + " TEXT not null, "
		      + COLUMN_FILE + " BLOB not null);";
	
	private static final String VIDEO_CREATE  = "create table "
		      + TABLE_VIDEO 
		      + "(" + COLUMN_VIDEO_ID + " INTEGER primary key autoincrement, " 
		      + COLUMN_POSITION + " INTEGER not null, "
		      + COLUMN_TITLE + " TEXT not null, "
		      + COLUMN_FILE + " BLOB not null);";
	
	private static final String AUDIO_CREATE  = "create table "
		      + TABLE_AUDIO 
		      + "(" + COLUMN_AUDIO_ID + " INTEGER primary key autoincrement, " 
		      + COLUMN_POSITION + " INTEGER not null, "
		      + COLUMN_TITLE + " TEXT not null, "
		      + COLUMN_FILE + " BLOB not null);";

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
		db.execSQL(POSITION_CREATE);
		db.execSQL(PATH_CREATE);
		db.execSQL(PHOTO_CREATE);
		db.execSQL(VIDEO_CREATE);
		db.execSQL(AUDIO_CREATE);
		db.execSQL(CONTACT_CREATE);

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
