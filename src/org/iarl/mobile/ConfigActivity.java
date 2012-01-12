package org.iarl.mobile;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout;

public class ConfigActivity extends Activity {
	private LinkedHashMap<String,String> bands;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Bands hash map
        bands = new LinkedHashMap<String,String>();
        bands.put("6m", "6m (50 MHz)");
        bands.put("2m", "2m (144-148 MHz)");
        bands.put("70cm", "70cm (430-440 MHz)");
        Set<String> bandKeys = bands.keySet();
        
        // Get ListView object
        ListView list = (ListView) findViewById(R.id.config_listview);
        setContentView(list);
        
        // Variables
        LinearLayout bbox;
        LinearLayout vbox;
        TextView label;
        EditText value;
        CheckBox check;
        
        // Update timer
        bbox = new LinearLayout(this);
        label = new TextView(this);
        label.setText("Device list update timer");
        value = new EditText(this);
        value.setInputType(InputType.TYPE_CLASS_NUMBER);
        value.setGravity(Gravity.RIGHT);      
        bbox.addView(label);
        bbox.addView(value);
        bbox.setGravity(Gravity.LEFT);
        list.addView(bbox);
        
        // Update distance
        bbox = new LinearLayout(this);
        label = new TextView(this);
        label.setText("Device list update distance");
        value = new EditText(this);
        value.setInputType(InputType.TYPE_CLASS_NUMBER);
        value.setGravity(Gravity.RIGHT);      
        bbox.addView(label);
        bbox.addView(value);
        bbox.setGravity(Gravity.LEFT);
        list.addView(bbox);
        
        // Band selectors
        bbox = new LinearLayout(this);
        Iterator<String> bandIterator = bandKeys.iterator();
        do {
        	String key = bandIterator.next();
        	String val = bands.get(key);
        	vbox = new LinearLayout(this);
            label = new TextView(this);
            label.setText(val);
            check = new CheckBox(this);
            Iterator<String> selectedIterator = MainActivity.setting_bands.iterator();
            do {
            	String selected = selectedIterator.next();
            	if (selected == key) {
            		check.setChecked(true);
            	}
            } while (selectedIterator.hasNext());
            vbox.addView(label);
            vbox.addView(check);
            bbox.addView(vbox);	
        } while (bandIterator.hasNext());
        list.addView(bbox);
    }
}
