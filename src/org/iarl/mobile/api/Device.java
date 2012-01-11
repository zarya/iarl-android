package org.iarl.mobile.api;

import org.iarl.mobile.api.Coordinates;
import org.iarl.mobile.api.DeviceCacheHelper;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class Device {
	public DeviceType type 		= DeviceType.RELAY;
	public String call     		= "N0CALL";
	public Coordinates position = null;
	public String location		= null;
	public long rx 		   		= 0;
	public long tx 		   		= 0;
	public long shift 	   		= 0;
	public double distance		= 0.0;
	public String ctcss			= null;
	public boolean ctcss_rx		= false;
	public boolean ctcss_tx		= false;
	public long tone			= 0;
	public String operators		= null;
	public String website		= null;
	
	// Create device reading information from a JSONObject
	public Device(JSONObject fields) {
		try {
			this.call = fields.getString("call");
			this.location = fields.getString("location");
			this.position = new Coordinates(
				fields.getDouble("latitude"),
				fields.getDouble("longitude")
			);
			this.rx = fields.getLong("rx");
			this.tx = fields.getLong("tx");
			this.shift = fields.getLong("shift");
			//this.distance = fields.getDouble("distance");
			Log.i(Device.class.getPackage().toString(),
				String.format("New device %s at %.3f,%.3f",
					this.call,
					this.position.latitude,
					this.position.longitude));
		} catch (JSONException e) {
			Log.e(Device.class.getCanonicalName(), "Failed to extract JSON: " + e.getLocalizedMessage());
			Log.i(Device.class.getCanonicalName(), fields.toString());
		}
	}

	// Create device reading information from a (SQLite) Cursor
	public Device(DeviceType type, Cursor cursor) {
		Object[] columns = cursor.getColumnNames();
		
		this.call = cursor.getString(
			indexOf(DeviceCacheHelper.KEY_CALL, columns));
		this.location = cursor.getString(
			indexOf(DeviceCacheHelper.KEY_LOCATION, columns));
		this.position = new Coordinates(
			cursor.getDouble(
				indexOf(DeviceCacheHelper.KEY_LATITUDE, columns)),
			cursor.getDouble(
				indexOf(DeviceCacheHelper.KEY_LONGITUDE, columns))
		);
		this.rx = cursor.getLong(
			indexOf(DeviceCacheHelper.KEY_RX, columns));
		this.tx = cursor.getLong(
			indexOf(DeviceCacheHelper.KEY_TX, columns));
		this.shift = cursor.getLong(
			indexOf(DeviceCacheHelper.KEY_SHIFT, columns));
		this.ctcss = cursor.getString(
			indexOf(DeviceCacheHelper.KEY_CTCSS, columns));
		this.ctcss_rx = (boolean) (cursor.getInt(
			indexOf(DeviceCacheHelper.KEY_CTCSS_RX, columns)) != 0);
		this.ctcss_tx = (boolean) (cursor.getInt(
			indexOf(DeviceCacheHelper.KEY_CTCSS_TX, columns)) != 0);
		this.operators = cursor.getString(
			indexOf(DeviceCacheHelper.KEY_OPERATORS, columns));
		this.website = cursor.getString(
			indexOf(DeviceCacheHelper.KEY_WEBSITE, columns));
	}
	
	// Create device specifying all the fields manually (for testing)
	public Device(DeviceType type, String call, long rx, long tx, long shift) {
		this.type = type;
		this.call = call;
		this.rx = rx;
		this.tx = tx;
		this.shift = shift;
	}

	public static <T>int indexOf(T needle, T[] haystack)
	{
	    for (int i=0; i<haystack.length; i++)
	    {
	        if (haystack[i] != null && haystack[i].equals(needle)
	            || needle == null && haystack[i] == null) return i;
	    }

	    // Not found
	    return -1;
	}
}
