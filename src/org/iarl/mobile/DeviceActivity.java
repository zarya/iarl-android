package org.iarl.mobile;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;

import org.iarl.mobile.api.API;
import org.iarl.mobile.api.Coordinates;
import org.iarl.mobile.api.Device;
import org.iarl.mobile.api.DeviceCacheHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class DeviceActivity extends Activity {
    public static API server                = new API();
    public static DeviceCacheHelper cache   = null;
    public static Coordinates location      = null;
    public ListView list                    = null;
    public DeviceAdapter adapter            = null;
    public ArrayList<Device> devices        = null;
    public static ProgressBar spinner       = null;

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
        
        //Start GPS
        LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener mlocListener = new MyLocationListener();
        
        //Create gps accuracy criteria
        Criteria coarse = new Criteria();
        coarse.setAccuracy(Criteria.ACCURACY_COARSE);
        
        mlocManager.requestLocationUpdates( mlocManager.getBestProvider(coarse, true), 0, 0, mlocListener);
        
        // Initialize cache
        cache = new DeviceCacheHelper(this);

        // Initialize spinner
        spinner = (ProgressBar) findViewById(R.id.spinner);

        // Initialize list view
        list = new ListView(this);
        devices = new ArrayList<Device>();       
        adapter = new DeviceAdapter(this, devices);
        list.setAdapter(adapter);
        list.setOnItemClickListener(showDevice);
        setContentView(list);
        
    }

    public void showSpinner() {
        spinner.setVisibility(ProgressBar.VISIBLE);
    }

    public void hideSpinner() {
        spinner.setVisibility(ProgressBar.GONE);
    }

    private void limitedConnectivity(String error) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("No connectivity");
        alertDialog.setMessage("We could not contact the IARL server, maybe you are on a slow link or have limited connectivity. Error: " + error);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
    }

	@SuppressWarnings("unused")
	private void getLocationByIp() {
        server.getJson("meta/client/info/android/" + Build.VERSION.RELEASE,
            new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    JSONObject locationObject;
                    try {
                        locationObject = response.getJSONObject("location");
                        Coordinates newLocation = new Coordinates(
                            locationObject.getDouble("latitude"),
                            locationObject.getDouble("longitude"));
                        updateLocation(newLocation);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Throwable error, String content) {
                    hideSpinner();
                    limitedConnectivity(content);
                }
        });     
    }

    private OnItemClickListener showDevice = new OnItemClickListener () {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            //showDevicePopup(devices.get(position));
        }
    };

    @SuppressWarnings("unused")
    private void showDevicePopup(Device device) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.device_popup, null, false);
        final PopupWindow popup = new PopupWindow(layout, 400, 400, true);
        TableLayout table = (TableLayout) layout.findViewById(R.id.device_popup_table);

        TableRow row = null;
        TextView text = null;

        row = new TableRow(this);
        text = new TextView(this);
        text.setText("Location");
        row.addView(text);
        text = new TextView(this);
        text.setText(device.location);
        row.addView(text);
        table.addView(row);

        Button button = new Button(this);
        button.setText("Close");
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                popup.dismiss();
            }
        });

        popup.showAtLocation(list, Gravity.CENTER, 0, 0);
    }

    private void updateDevices() {
        showSpinner();
        String path = "radio/relay/near/" 
                + String.valueOf(location.latitude) + "/"
                + String.valueOf(location.longitude);
        RequestParams query = new RequestParams();
        query.put("radius", "50");

        server.getJson(path, query,
            new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONArray response) {
                    try {
                        adapter.clear();
                        for (int i = 0; i < response.length(); ++i) {
                            JSONObject row = (JSONObject) response.get(i);
                            JSONObject fields = (JSONObject) row.getJSONObject("fields");
                            Device device = new Device(fields);
                            device.distance = device.position.haversine(location);
                            devices.add(device);
                        }
                        Collections.sort(devices, new DeviceComparator());
                        if (response.length() > 0) {
                            adapter.notifyDataSetChanged();
                        }   
                        hideSpinner();
                    } catch (JSONException e) {
                        Log.e(this.getClass().toString(), "Error reading device: " + e.getMessage());
                        hideSpinner();
                    }
                }

                @Override
                public void onFailure(Throwable error, String content) {
                    hideSpinner();
                    limitedConnectivity(content);
                }
        });
    }

    private void updateLocation(Coordinates newLocation) {
        location = newLocation;
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
    
    public class MyLocationListener implements LocationListener
    {
    	
    	@Override
    	public void onLocationChanged(Location loc)
    	{
            Coordinates newLocation = new Coordinates(loc.getLatitude(),loc.getLongitude());
            updateLocation(newLocation);
    	}

    	@Override
    	public void onProviderDisabled(String provider)
    	{
    		Log.e(null,"Location provider Disabled");
    	}

    	@Override
    	public void onProviderEnabled(String provider)
    	{
    		Log.d(null,"Location provider Enabled");
    	}

    	@Override
    	public void onStatusChanged(String provider, int status, Bundle extras)
    	{
    	}

    }
}