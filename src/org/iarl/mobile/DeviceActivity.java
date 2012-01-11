package org.iarl.mobile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.iarl.mobile.api.API;
import org.iarl.mobile.api.Coordinates;
import org.iarl.mobile.api.Device;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class DeviceActivity extends Activity {
	public static API server = new API();
	public static Coordinates location = null;
	public DeviceAdapter adapter;
	public ArrayList<Device> devices;
	
	private class DeviceComparator implements Comparator<Device> {
		@Override
		public int compare(Device a, Device b) {
			int aa = (int) a.distance;
			int bb = (int) b.distance;
			return aa - bb;
		}
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ListView list = new ListView(this);
        devices = new ArrayList<Device>();
        /*
        Device device;
        for (int i = 0; i < 10; ++i) {
        	device = new Device(DeviceType.RELAY, "TEST" + String.valueOf(i), 123, 456, 600);
        	devices.add(device);
        }
        */
        
        adapter = new DeviceAdapter(this, devices);
        list.setAdapter(adapter);
        
        setContentView(list);
        getLocationByIp();
    }
    
    private void getLocationByIp() {
    	JSONObject info = server.getObject("meta/client/info/android/" + Build.VERSION.RELEASE);
    	if (info != null) {
    		try {
				JSONObject locationObject = info.getJSONObject("location");
				Coordinates newLocation = new Coordinates(
						locationObject.getDouble("latitude"),
						locationObject.getDouble("longitude"));
				updateLocation(newLocation);
			} catch (Exception e) {
				Log.e(this.getClass().toString(),
						"Failed to probe your location: " + e.getMessage());
				e.printStackTrace();
			}
    	}
    }
    
    private void updateDevices() {
    	JSONArray info = server.getArray("radio/relay/near/" 
				+ String.valueOf(this.location.latitude) + "/"
				+ String.valueOf(this.location.longitude) + "?radius=50");
    	if (info != null) {
    		adapter.clear();
    		//devices.clear();
    		try {
    			for (int i = 0; i < info.length(); ++i) {
    				JSONObject row = (JSONObject) info.get(i);
    				JSONObject fields = (JSONObject) row.getJSONObject("fields");
    				Device device = new Device(fields);
    				device.distance = device.position.haversine(location);
    				devices.add(device);
    			}
    			Collections.sort(devices, new DeviceComparator());
    		} catch (JSONException e) {
    			Log.e(this.getClass().toString(), "Error reading device: " + e.getMessage());
    		}
    		
    		if (info.length() > 0) {
    			adapter.notifyDataSetChanged();
    		}
    	}
    }
    
    private void updateLocation(Coordinates newLocation) {
    	this.location = newLocation;
    	if (devices.size() > 0) {
    		Device device;
    		for (int i = 0; i < devices.size(); ++i) {
    			device = devices.get(i);
    			device.distance = device.position.haversine(location);
    		}
    		Collections.sort(devices, new DeviceComparator());
    	}
    	updateDevices();
    }
}