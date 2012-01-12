package org.iarl.mobile.api;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DeviceCacheHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "devices.db";
    public static final int DATABASE_VERSION = 1;

    // Devices table name
    private static final int TABLE_DEVICES_VERSION = 1;
    private static final String TABLE_DEVICES = "device";

    // Devices table column names
    public static final String KEY_ID           = "id";
    public static final String KEY_CALL         = "call";
    public static final String KEY_LOCATION     = "location";
    public static final String KEY_LATITUDE     = "latitude";
    public static final String KEY_LONGITUDE    = "longitude";
    public static final String KEY_RX           = "rx";
    public static final String KEY_TX           = "tx";
    public static final String KEY_SHIFT        = "shift";
    public static final String KEY_CTCSS        = "ctcss";
    public static final String KEY_CTCSS_RX     = "ctcss_rx";
    public static final String KEY_CTCSS_TX     = "ctcss_tx";
    public static final String KEY_OPERATORS    = "operators";
    public static final String KEY_WEBSITE      = "website";
    public static final String KEY_CREATED      = "created_at";
    public static final String KEY_UPDATED      = "updated_at";

    public DeviceCacheHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        // Schema upgrades
        int version = getVersion();
        if (version < 2) {
            // Upgrade to version 2, etc.
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE " + TABLE_DEVICES + "_version (version INTEGER);");
            db.execSQL("INSERT INTO " + TABLE_DEVICES + "_version VALUES (" + TABLE_DEVICES_VERSION + ");");
            db.execSQL("CREATE TABLE " + TABLE_DEVICES + " (" +
                    KEY_ID          + " INTEGER PRIMARY," +
                    KEY_CREATED     + " INTEGER," +
                    KEY_UPDATED     + " INTEGER," +
                    KEY_CALL        + " TEXT," +
                    KEY_LOCATION    + " TEXT," +
                    KEY_LATITUDE    + " REAL," +
                    KEY_LONGITUDE   + " REAL," +
                    KEY_RX          + " INTEGER," +
                    KEY_TX          + " INTEGER," +
                    KEY_SHIFT       + " INTEGER," +
                    KEY_CTCSS       + " TEXT," +
                    KEY_CTCSS_RX    + " BOOLEAN," +
                    KEY_CTCSS_TX    + " BOOLEAN," +
                    KEY_OPERATORS   + " TEXT," +
                    KEY_WEBSITE     + " TEXT" +
                    ");");
        } catch (Exception e) {
            Log.e("DeviceCache", "Error creating: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    private int getVersion() {
        int version = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT version FROM " + TABLE_DEVICES + "_version;", null);
        if (cursor.moveToFirst()) {
            version = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return version;
    }

    private ContentValues getDeviceContentValues(Device device) {
        ContentValues values = new ContentValues();
        long now = System.currentTimeMillis()/1000;
        values.put(KEY_UPDATED, now);
        values.put(KEY_CALL, device.call);
        values.put(KEY_LOCATION, device.location);
        values.put(KEY_LATITUDE, device.position.latitude);
        values.put(KEY_LONGITUDE, device.position.longitude);
        values.put(KEY_RX, device.rx);
        values.put(KEY_TX, device.tx);
        values.put(KEY_SHIFT, device.shift);
        values.put(KEY_CTCSS, device.ctcss);
        values.put(KEY_CTCSS_RX, device.ctcss_rx);
        values.put(KEY_CTCSS_TX, device.ctcss_tx);
        values.put(KEY_OPERATORS, device.operators);
        values.put(KEY_WEBSITE, device.website);

        return values;
    }

    // Adding new device
    public void createDevice(Device device) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Get device values
        ContentValues values = getDeviceContentValues(device);
        values.put(KEY_CREATED, Long.valueOf((String) values.get(KEY_UPDATED)));

        // Inserting row
        db.insert(TABLE_DEVICES, null, values);
        db.close();
    }


    public void updateDevice(Device device) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Updating row
        db.update(TABLE_DEVICES, getDeviceContentValues(device),
            // Where
            KEY_CALL + " = ?", new String[]{device.call});
        db.close();
    }

    // Add new device if it doesn't exist, otherwise update
    public boolean addDevice(Device device) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id " + 
            "FROM " + TABLE_DEVICES + " " +
            "WHERE " + KEY_CALL + " = ?",
            new String[]{device.call});
        boolean added = false;

        if (cursor.moveToFirst()) {
            updateDevice(device);
        } else {
            createDevice(device);
            added = true;
        }

        cursor.close();
        db.close();
        return added;
    }

    // Purge old devices
    public int purgeDevices(int seconds) {
        long now = System.currentTimeMillis()/1000;
        long exp = (now - seconds);
        int purged = 0;
        StringBuffer purge = new StringBuffer();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + 
            KEY_ID + " " +
            "FROM " + TABLE_DEVICES + " " +
            "WHERE " + KEY_UPDATED + " < ?",
            new String[]{String.valueOf(exp)});

        if (cursor.moveToFirst()) {
            do {
                if (purge.length() > 0) {
                    purge.append(",");
                }
                purge.append(cursor.getString(0));
                ++purged;
            } while (cursor.moveToNext());

            db.execSQL("DELETE " +
                    "FROM " + TABLE_DEVICES + " " + 
                    "WHERE " + KEY_ID + " IN (" + purge.toString() + ");");

            // Commit deletes
            //db.commit();
        }

        cursor.close();
        db.close();     
        return purged;
    }

    // Get devices near a set position, within range of a set distance
    public List<Device> getDevicesNear(Coordinates position, int distance) {
        ArrayList<Device> devices = new ArrayList<Device>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DEVICES, null);
        Coordinates devicePosition = null;

        // Retrieve devices
        if (cursor.moveToFirst()) {
            do {
                devicePosition = new Coordinates(cursor.getDouble(2), cursor.getDouble(3));
                if (position.haversine(devicePosition) <= distance) {
                    Device device = new Device(DeviceType.RELAY, cursor);
                    devices.add(device);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return devices;
    }
}
