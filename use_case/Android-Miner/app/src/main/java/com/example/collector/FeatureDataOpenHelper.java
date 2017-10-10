package com.example.collector;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FeatureDataOpenHelper extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "FeaturesData.db";
    private static final String TABLE_FEATURES = "Features";
	
	private static final String KEY_ID = "ID";
	private static final String KEY_FOREGROUND_APP = "Foreground_App";
	private static final String KEY_NUMBER_APPS_RUNNING = "Number_of_Apps_Running";
	private static final String KEY_APPS_RUNNING = "Apps_Running";
	private static final String KEY_ORIENTATION = "Orientation";
	private static final String KEY_LATITUDE = "Latitude";
	private static final String KEY_LONGITUDE = "Longitude";
	private static final String KEY_ADDRESS = "Adress";
	private static final String KEY_AVAILABLE_MEMORY = "Available_Memory";
	private static final String KEY_CHARGER = "Charger";
	private static final String KEY_BATTERY = "Battery_Life";
	private static final String KEY_NETWORKS = "Networks";
	private static final String KEY_CONNECTIONS = "Network_Connection_Status";
	private static final String KEY_BYTES_RECEIVED = "Bytes_Received";
	private static final String KEY_BYTES_TRANSMITTED = "Bytes_Transmitted";
	
	
    private static final String FEATURE_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_FEATURES + "(" +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " + 
                KEY_FOREGROUND_APP + " TEXT, " +
                KEY_NUMBER_APPS_RUNNING + " INTEGER, " +
                KEY_APPS_RUNNING + " TEXT, " + KEY_ORIENTATION + " TEXT, " +
                KEY_LATITUDE + " REAL, " + KEY_LONGITUDE + " REAL, " +
                KEY_ADDRESS + " TEXT, " + 
                KEY_AVAILABLE_MEMORY + " INT, " +
                KEY_CHARGER + " TEXT, " + KEY_BATTERY + " REAL, " + KEY_NETWORKS +
                " TEXT, " + KEY_CONNECTIONS + " TEXT, " + 
                KEY_BYTES_RECEIVED + " REAL, " + 
                KEY_BYTES_TRANSMITTED + " REAL)";


	public FeatureDataOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(FEATURE_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEATURES);
 
        // Create tables again
        onCreate(db);
	}
	
	public void recordFeatures(CollectionInstance instance) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(KEY_FOREGROUND_APP, instance.getForegroundApp());
	    values.put(KEY_NUMBER_APPS_RUNNING, instance.getNumberOfRunningApps());
	    values.put(KEY_APPS_RUNNING, instance.getAppsRunning());
	    values.put(KEY_ORIENTATION, instance.getOrientation());
	    values.put(KEY_LONGITUDE, instance.getLatitude());
	    values.put(KEY_LATITUDE, instance.getLongitude());
	    values.put(KEY_ADDRESS, instance.getAddress());
	    values.put(KEY_AVAILABLE_MEMORY, instance.getAvailableMemory());
	    values.put(KEY_CHARGER, instance.getCharger());
	    values.put(KEY_BATTERY, instance.getBattery());
	    values.put(KEY_NETWORKS, instance.getNetworks());
	    values.put(KEY_CONNECTIONS, instance.getConnections());
	    values.put(KEY_BYTES_RECEIVED, instance.getBytesReceived());
	    values.put(KEY_BYTES_TRANSMITTED, instance.getBytesTransmitted());
	    //Log.v("DEBUG", values.toString());
	 
	    // Inserting Row
	    //long newRowId;
	    //newRowId = 
	    db.insert(TABLE_FEATURES, null, values);
	    db.close(); // Closing database connection
	}
	
	public List<CollectionInstance> getAllRecords() {
	    List<CollectionInstance> recordList = new ArrayList<CollectionInstance>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_FEATURES;
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	CollectionInstance record = new CollectionInstance();
	        	record.setId(Integer.parseInt(cursor.getString(0)));
	        	record.setDateTime(cursor.getString(1));
	        	record.setForegroundApp(cursor.getString(2));
	        	record.setNumberOfRunningApps(Integer.parseInt(cursor.getString(3)));
	        	record.setAppsRunning(cursor.getString(4));
	        	record.setOrientation(cursor.getString(5));
	        	record.setLongitude(Double.parseDouble(cursor.getString(6)));
	        	record.setLatitude(Double.parseDouble(cursor.getString(7)));
	        	record.setAddress(cursor.getString(8));
	        	record.setAvailableMemory(Long.parseLong(cursor.getString(9)));
	        	record.setCharger(cursor.getString(10));
	        	record.setBattery(Float.parseFloat(cursor.getString(11)));
	        	record.setNetworks(cursor.getString(12));
	        	record.setConnections(cursor.getString(13));
	        	record.setBytesReceived(new BigDecimal(cursor.getString(14)).longValue());
	        	record.setBytesTransmitted(new BigDecimal(cursor.getString(15)).longValue());
	            // Adding contact to list
	        	recordList.add(record);
	        } while (cursor.moveToNext());
	    }
	 
	    // return contact list
	    return recordList;
	}


	/*public long getBytesReceivedRecords(){
        String[] columns = {KEY_BYTES_RECEIVED};
        Cursor cursor = getReadableDatabase().query(TABLE_FEATURES, columns, "ID = ", null, null, null, null);
        cursor.moveToPosition(cursor.getCount() - 1);
        return cursor.getLong(cursor.getColumnIndexOrThrow(KEY_BYTES_RECEIVED));
    }

    public long getBytesTransmitteddRecords(){
        String[] columns = {KEY_BYTES_TRANSMITTED};
        Cursor cursor = getReadableDatabase().query(TABLE_FEATURES, columns, "ID = ", null, null, null, null);
        cursor.moveToPosition(cursor.getCount() - 1);
        return cursor.getLong(cursor.getColumnIndexOrThrow(KEY_BYTES_TRANSMITTED));
    }*/

	public Long getLastBytesReceived() {
	    String query = "SELECT KEY_BYTES_RECEIVED FROM TABLE ORDER BY ID DESC LIMIT 1";
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(query, null);
	 
	    if (cursor.moveToFirst()) {
	    	return (new BigDecimal(cursor.getString(14)).longValue());
	    }
	    else{
	    	Log.d("ERROR", "No sql cell found");
	    	return null;
	    }
	}
	
	public Long getLastBytesTransmitted() {
	    String query = "SELECT KEY_BYTES_TRANSMITTED FROM TABLE ORDER BY ID DESC LIMIT 1";
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(query, null);
	 
	    if (cursor.moveToFirst()) {
	    	return (new BigDecimal(cursor.getString(15)).longValue());
	    }
	    else{
	    	Log.d("ERROR", "No sql cell found");
	    	return null;
	    }
	}
}
