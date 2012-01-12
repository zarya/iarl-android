package org.iarl.mobile;

import java.util.HashSet;
import java.util.Set;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class MainActivity extends TabActivity {
    public TabHost tabHost;
    private Resources res;

    // Settings
    public static final String PREFS_NAME = "IARL Mobile";

    public static Set<String> setting_bands;
    public static int setting_region = 0;
    public static int setting_update_position = 5; // Update every 5km
    public static int setting_update_time = 5;   // Update every 5mins

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        try {
            loadSettings();

            // Create tabs
            tabHost = getTabHost();
            res = getResources();
            setupTab("device", "In range", DeviceActivity.class,
                    res.getDrawable(R.drawable.ic_tab_near_selected));
            setupTab("maps", "Maps", DeviceActivity.class,
                    res.getDrawable(R.drawable.ic_tab_maps_unselected));
            setupTab("config", "Config", ConfigActivity.class,
                    res.getDrawable(R.drawable.ic_tab_about_unselected));
            setupTab("about", "About", DeviceActivity.class,
                    res.getDrawable(R.drawable.ic_tab_about_unselected));

            tabHost.setCurrentTab(0);
        } catch (Exception e) {
            // this is the line of code that sends a real error message to the log
            Log.e("ERROR", "ERROR IN CODE: " + e.toString());

            // this is the line that prints out the location in
            // the code where the error occurred.
            e.printStackTrace();
        }        
    }

    @Override
    public void onStop() {
        super.onStop();
        saveSettings();
    }

    private void loadSettings() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        // Read selected region
        setting_region = settings.getInt("region", 1);

        // Read selected bands
        setting_bands = new HashSet<String>();
        String[] bands = settings.getString("bands", "70cm,2m").split(",");
        for (int i = 0; i < bands.length; ++i) {
            setting_bands.add(bands[i]);
        }

        // Read update timers
        setting_update_position = settings.getInt("update_position", setting_update_position);
        setting_update_time = settings.getInt("update_time", setting_update_time);

    }

    private void saveSettings() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        // Save selected region
        editor.putInt("region", setting_region);

        // Save selected bands
        StringBuilder temp_bands = new StringBuilder();
        String[] temp_bands_list = (String[]) setting_bands.toArray();
        temp_bands.append(temp_bands_list[0]);
        if (temp_bands_list.length > 1) {
            for (int i = 1; i < temp_bands_list.length; ++i) {
                temp_bands.append(",");
                temp_bands.append(temp_bands_list[i]);
            }
        }
        editor.putString("bands", temp_bands.toString());

        // Save update timers
        editor.putInt("update_position", setting_update_position);
        editor.putInt("update_time", setting_update_time);

        // Commit the changes
        editor.commit();
    }

    private void setupTab(String tag, String label, Class<?> cls, Drawable drawable) {
        Log.d(this.getLocalClassName(), "Adding tab " + tag + " with label: " + label);
        Intent intent = new Intent().setClass(this, cls);
        TabHost.TabSpec spec = tabHost.newTabSpec(tag).setIndicator(label, drawable)
            .setContent(intent);
        tabHost.addTab(spec);
    }
}
