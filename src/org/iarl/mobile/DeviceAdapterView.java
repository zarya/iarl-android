package org.iarl.mobile;

import org.iarl.mobile.api.Device;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DeviceAdapterView extends LinearLayout {
    public DeviceAdapterView(Context context, Device device) {
        super(context);
        //setId(device.getDeviceID());
        setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout panel = new LinearLayout(context);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setGravity(Gravity.BOTTOM);

        TextView call = new TextView(context);
        call.setTextSize(16);
        call.setText(device.call);
        panel.addView(call);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        addView(panel, params);
    }
}
