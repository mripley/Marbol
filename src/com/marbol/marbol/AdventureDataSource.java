package com.marbol.marbol;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.marbol.marbol.Adventure;
import com.marbol.marbol.MarbolSQLHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

public class AdventureDataSource {

	private SQLiteDatabase db;
	private MarbolSQLHelper dbHelper;
	private String[] allColumns = {MarbolSQLHelper.COLUMN_ID,
						           MarbolSQLHelper.ADVENTURE_NAME, 
								   MarbolSQLHelper.ADVENTURE_DATE,
								   MarbolSQLHelper.ADVENTURE_AREA,
								   MarbolSQLHelper.ADVENTURE_DISTANCE, 
								   MarbolSQLHelper.ADVENTURE_TIME,
								   MarbolSQLHelper.ADVENTURE_GPS_POINTS,
								   MarbolSQLHelper.ADVENTURE_ELEVATION,
								   MarbolSQLHelper.ADVENTURE_SPEED};
	
	public AdventureDataSource(Context c){
		dbHelper = new MarbolSQLHelper(c);
	}
	
	public void open() throws SQLException{
		db = dbHelper.getWritableDatabase();
	}
	
	public void close(){
		dbHelper.close();
	}
	
	public Adventure addAdventure(Adventure adv){
		ContentValues values = new ContentValues();
		values.put(MarbolSQLHelper.ADVENTURE_NAME, adv.getAdvName());
		values.put(MarbolSQLHelper.ADVENTURE_DISTANCE, adv.getDistanceInMeters());
		values.put(MarbolSQLHelper.ADVENTURE_AREA, adv.getArea(Adventure.STANDARD_RADIUS));
		
		// convert our Date to an appropriate string
		java.text.DateFormat dateFormater = SimpleDateFormat.getDateInstance();
		values.put(MarbolSQLHelper.ADVENTURE_DATE, dateFormater.format(adv.getAdvDate()));
		
		values.put(MarbolSQLHelper.ADVENTURE_TIME, adv.getAdvTime());
		
		String points = new String();
		if(adv.getGpsPoints().size() > 0){
			// convert the arraylist of points into a single string to stuff into the DB 
			for(Location l : adv.getGpsPoints()){
				points += l.getLongitude()+","+l.getLatitude()+","+l.getAltitude()+","+l.getSpeed()+":";
			}
		}
	
		values.put(MarbolSQLHelper.ADVENTURE_GPS_POINTS, points);
		values.put(MarbolSQLHelper.ADVENTURE_ELEVATION, adv.getElevationDiff());
		values.put(MarbolSQLHelper.ADVENTURE_SPEED, adv.getAverageSpeed());
		
		long rowID = db.insert(MarbolSQLHelper.TABLE_ADVENTURE, null, values);
		
		adv.setAdvID(rowID);
		return adv;
	}
	
	// requires a complete Adventure including its id
	public void deleteAdventure(Adventure adv){
		Log.i("Adventure DB", "Deleting Adventure: " + adv.getAdvName() + " ID: " + adv.getAdvID());
		db.delete(MarbolSQLHelper.TABLE_ADVENTURE, MarbolSQLHelper.COLUMN_ID + " = " + adv.getAdvID(), null);

	}
	
	public void deleteAdventure(long id){
		Log.i("Adventure DB", "Deleting Adventure ID: " + " ID: " + id);
		db.delete(MarbolSQLHelper.TABLE_ADVENTURE, MarbolSQLHelper.COLUMN_ID + " = " + id, null);
	}
	
	public Cursor getAdventures(){	
		Cursor cursor = db.query(MarbolSQLHelper.TABLE_ADVENTURE, allColumns, null, null, null, null, null);
		
		if(cursor != null){
			cursor.moveToFirst();
		}
		return cursor;
	}
	
	public Adventure getAdventure(long id){
		Cursor c = db.query(MarbolSQLHelper.TABLE_ADVENTURE, allColumns, MarbolSQLHelper.COLUMN_ID + " = " + id, null, null, null, null);
		c.moveToFirst();
		return cursorToAdventure(c);
	}
	
	public void updateAdventure(Adventure adv){
		ContentValues values = new ContentValues();
		values.put(MarbolSQLHelper.ADVENTURE_NAME, adv.getAdvName());
		values.put(MarbolSQLHelper.ADVENTURE_DISTANCE, adv.getDistanceInMeters());
		values.put(MarbolSQLHelper.ADVENTURE_AREA, adv.getArea(Adventure.STANDARD_RADIUS));
		
		// convert our Date to an appropriate string
		java.text.DateFormat dateFormater = SimpleDateFormat.getDateInstance();
		values.put(MarbolSQLHelper.ADVENTURE_DATE, dateFormater.format(adv.getAdvDate()));
		values.put(MarbolSQLHelper.ADVENTURE_TIME, adv.getAdvTime());
		
		String points = new String();
		if(adv.getGpsPoints().size() > 0){
			// convert the array list of points into a single string to stuff into the DB 
			for(Location l : adv.getGpsPoints()){
				points += l.getLongitude()+","+l.getLatitude()+","+l.getAltitude()+","+l.getSpeed()+":";
			}
		}
	
		values.put(MarbolSQLHelper.ADVENTURE_GPS_POINTS, points);
		values.put(MarbolSQLHelper.ADVENTURE_ELEVATION, adv.getElevationDiff());
		values.put(MarbolSQLHelper.ADVENTURE_SPEED, adv.getAverageSpeed());
		
		db.update(MarbolSQLHelper.TABLE_ADVENTURE, values, 
				  MarbolSQLHelper.COLUMN_ID + "= ?", 
				  new String[] {String.valueOf(adv.getAdvID())});
	}
	
	public static Adventure cursorToAdventure(Cursor c){
		SimpleDateFormat formater =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d;
		Adventure retval = new Adventure();
		retval.setAdvID(c.getLong(0));
		retval.setAdvName(c.getString(1));
		try {
			d = formater.parse(c.getString(2));
		
		} catch (ParseException e) {
			d = new Date();
			Log.e("SQL Stuff", "Unable to deserialize date from cursor");
		}
		retval.setAdvDate(d);
		retval.setAdvArea(c.getDouble(3));
		retval.setAdvDistance(c.getDouble(4));
		retval.setAdvTime(c.getLong(5));
		
		// get the string containing all the points 
		String strPoints = c.getString(6);
		
		if (!strPoints.equals(""))
		{
			// convert the string back into a series of locations.
			ArrayList<Location> points = new ArrayList<Location>();
			String[] splitPoints = strPoints.split(":");
			for (String loc : splitPoints) {
				String[] splitLocation = loc.split(",");
				Location l = new Location("SQL_db");
				l.setLongitude(Double.parseDouble(splitLocation[0]));
				l.setLatitude(Double.parseDouble(splitLocation[1]));
				l.setAltitude(Double.parseDouble(splitLocation[2]));
				l.setSpeed(Float.parseFloat(splitLocation[3]));
				points.add(l);
			}
			retval.setGpsPoints(points);
		}

		retval.setElevationChange(c.getDouble(7));
		retval.setAverageSpeed(c.getDouble(8));
		return retval;
	}

}