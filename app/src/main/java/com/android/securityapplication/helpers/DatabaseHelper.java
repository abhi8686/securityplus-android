package com.android.securityapplication.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Database helper class that provides useful methods for
 * interfacing with a SQLite database.
 *
 * @author Jack Hindmarch
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
	private static DatabaseHelper mInstance = null;

	// Logcat tag
	private static final String LOG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 3;

	// Database Name
	public static final String DATABASE_NAME = "securityplus";

	// Table Names
	public static final String TABLE_GLOBAL_KEYS = "global_keys";
	public static final String TABLE_PUBLIC_KEYS = "public_keys";
	public static final String TABLE_MESSAGES = "messages";


	// Global Keys Table - fields
	public static final String KEY_UID = "_id";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PRIVATE_KEY = "private_key";
	public static final String KEY_PUBLIC_KEY = "public_key";

	//Public Keys Table - fields
	public static final String KEY_USERID = "user_id";

	//Public Keys Table - fields
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_SENDER_ID = "sender_id";




	/** 
	 * Table Create Statements 
	 * **/

	// Global Keys table create statement
	private static final String CREATE_TABLE_GLOBAL_KEYS = "CREATE TABLE "
			+ TABLE_GLOBAL_KEYS + "("  + KEY_EMAIL + " TEXT UNIQUE NOT NULL, " + KEY_PRIVATE_KEY + " TEXT," +
			KEY_PUBLIC_KEY + " TEXT)";

	// Public Keys table create statement
	private static final String CREATE_TABLE_PUBLIC_KEYS = "CREATE TABLE "
			+ TABLE_PUBLIC_KEYS + "(" + KEY_USERID + " INTEGER PRIMARY KEY," +
			KEY_PUBLIC_KEY + " TEXT, " + KEY_PRIVATE_KEY + " TEXT)";

	// Message table create statement
	private static final String CREATE_TABLE_MESSAGE = "CREATE TABLE " + TABLE_MESSAGES +
		 "(" + KEY_UID + " INTEGER PRIMARY KEY," + KEY_USERID + " INTEGER," +
			KEY_MESSAGE + " TEXT," + KEY_SENDER_ID + " INTEGER)";

	public static DatabaseHelper getInstance(Context context)
	{
		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		if (mInstance == null)
		{
			mInstance = new DatabaseHelper(context.getApplicationContext());
		}
		return mInstance;
	}

	public DatabaseHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Creates each of the tables if they do not already exist.
	 */
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(CREATE_TABLE_GLOBAL_KEYS);
		db.execSQL(CREATE_TABLE_PUBLIC_KEYS);
		db.execSQL(CREATE_TABLE_MESSAGE);
	}

	/**
	 * If database has been upgraded, delete the current tables and recreate.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GLOBAL_KEYS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PUBLIC_KEYS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
		// create new tables
		onCreate(db);
	}

	@Override
	public void onOpen(SQLiteDatabase db)
	{
		super.onOpen(db);

		// Enables foreign key relations
		db.execSQL("PRAGMA foreign_keys=ON");
	}

	public boolean isOpen()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		return db.isOpen();
	}

	/****************************************************************/

	/* Global keys table operations	 */

	public long createGlobalKeys(String public_key, String private_key, String email)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_PRIVATE_KEY, private_key);
		values.put(KEY_PUBLIC_KEY, public_key);
		values.put(KEY_EMAIL, email);

		db.delete(TABLE_GLOBAL_KEYS, KEY_EMAIL + " = ?", new String[]{String.valueOf(email)});
		// insert row
		return db.insert(TABLE_GLOBAL_KEYS, null, values);
	}

	/* Public keys table operations	 */

	public long createPublicKeys(String user_id, String public_key, String private_key)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_USERID, user_id);
		values.put(KEY_PUBLIC_KEY, public_key);
		values.put(KEY_PRIVATE_KEY, private_key);
		// insert row
		return db.insert(TABLE_PUBLIC_KEYS, null, values);
	}

	public long createMessages(String user_id, String message, String sender_id) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_USERID, user_id);
		values.put(KEY_MESSAGE, message);
		values.put(KEY_SENDER_ID, sender_id);

		// insert row
		return db.insert(TABLE_MESSAGES, null, values);
	}

	public Cursor getMessages(String user_id){
		SQLiteDatabase db = this.getReadableDatabase();

		String query = "SELECT *" + " FROM " + TABLE_MESSAGES + " WHERE " + KEY_USERID  + " = ?";
		Cursor c = db.rawQuery(query, new String[] { String.valueOf(user_id) });

		return c;
	}


	public String getPrivateKey()
	{
		SQLiteDatabase db = this.getReadableDatabase();

		String query = "SELECT " + KEY_PRIVATE_KEY + " FROM " + TABLE_GLOBAL_KEYS + " LIMIT 1";
		Cursor c = db.rawQuery(query, null);

		String keyString = null;
		if(c.moveToFirst())
		{
			keyString = c.getString(0);
		}
		c.close();

		return keyString;
	}
	public String getPrivateKey(String user_id)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		String query = "SELECT " + KEY_PRIVATE_KEY + " FROM " + TABLE_PUBLIC_KEYS + " WHERE " + KEY_USERID + " = ?";
		Cursor c = db.rawQuery(query, new String[] { String.valueOf(user_id) });

		String keyString = null;
		if(c.moveToFirst())
		{
			keyString = c.getString(0);
		}
		c.close();

		return keyString;
	}

	public String getPublicKey()
	{
		SQLiteDatabase db = this.getReadableDatabase();

		String query = "SELECT " + KEY_PUBLIC_KEY + " FROM " + TABLE_GLOBAL_KEYS + "LIMIT 1";
		Cursor c = db.rawQuery(query, null);

		String keyString = null;
		if(c.moveToFirst())
		{
			keyString = c.getString(0);
		}
		c.close();

		return keyString;
	}


	public String getPublicKey(String id) // add error handling in event of no result
	{
		SQLiteDatabase db = this.getReadableDatabase();

		String query = "SELECT * FROM " + TABLE_PUBLIC_KEYS + " WHERE " + KEY_USERID + " = ?";
		Cursor c = db.rawQuery(query, new String[] { String.valueOf(id) });

		String keyString = null;
		if(c.moveToFirst())
		{
			keyString = c.getString(1);
		}
		c.close();

		return keyString;
	}

	public int updatePublicKey(String user_id, String key)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_PUBLIC_KEY, key);
		// updating row
		Log.e("====", "updated database");
		return db.update(TABLE_PUBLIC_KEYS, values, KEY_USERID + " = ?", new String[] { String.valueOf(user_id) });
	}

//	public int createPublicKey(int user_id, String key)
//	{
//		SQLiteDatabase db = this.getWritableDatabase();
//		ContentValues values = new ContentValues();
//		values.put(KEY_PUBLIC_KEY, key);
//		// updating row
//
//		return db.update(TABLE_PUBLIC_KEYS, values, KEY_USERID + " = ?", new String[] { String.valueOf(user_id) });
//	}

	/**
	 * Delete a specified contact.
	 *
	 * @param uid - id of the contact to be deleted.
	 */
	public void deleteContact(long uid)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_GLOBAL_KEYS, KEY_UID + " = ?", new String[]{String.valueOf(uid)});
	}
}