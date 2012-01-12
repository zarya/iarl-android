package org.iarl.mobile;

import java.util.List;

import org.iarl.mobile.api.Device;
import org.iarl.mobile.util.Humanize;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DeviceAdapter extends BaseAdapter {
    private Context context;
    private List<Device> devices;

    public DeviceAdapter(Context context, List<Device> devices) {
        this.context = context;
        this.devices = devices;
    }

    @Override
    public int getCount() {
        return this.devices.size();
    }

    @Override
    public Object getItem(int position) {
        return this.devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService
                  (Context.LAYOUT_INFLATER_SERVICE);

        Device device = (Device) this.getItem(position);
        //View view = new DeviceAdapterView(this.context, device);
        View view = inflater.inflate(R.layout.device_adapter_view, parent, false);
        if (view != null) {
            ImageView bearing = (ImageView) view.findViewById(R.id.device_adapter_view_bearing);
            TextView call = (TextView) view.findViewById(R.id.device_adapter_view_call);
            TextView rx = (TextView) view.findViewById(R.id.device_adapter_view_rx);
            TextView shift = (TextView) view.findViewById(R.id.device_adapter_view_shift);
            TextView location = (TextView) view.findViewById(R.id.device_adapter_view_location);
            TextView distance = (TextView) view.findViewById(R.id.device_adapter_view_distance);

            String rxText = Humanize.frequency(device.rx);
            String shiftText = Humanize.frequency(device.shift, true);
            while (rxText.length() < 13) {
                rxText = " " + rxText;
            }
            if (!shiftText.startsWith("-")) {
                shiftText = "+" + shiftText;
            }
            while (shiftText.length() < 13) {
                shiftText = " " + shiftText;
            }
            call.setText(device.call);
            rx.setText(rxText);
            shift.setText(shiftText);
            location.setText(device.location);
            distance.setText(String.format("\u00B1 %.2f km", device.distance));

            //float bearing_ = (float) (position * 45.0);
            double bearing_ = device.position.bearing(DeviceActivity.location);
            if (bearing_ < 22.5 || bearing_ > 337.5) {
                bearing.setImageResource(R.drawable.arrow_north);
            } else if (bearing_ < 67.5) {
                bearing.setImageResource(R.drawable.arrow_northeast);
            } else if (bearing_ < 112.5) {
                bearing.setImageResource(R.drawable.arrow_east);
            } else if (bearing_ < 157.5) {
                bearing.setImageResource(R.drawable.arrow_southeast);
            } else if (bearing_ < 202.5) {
                bearing.setImageResource(R.drawable.arrow_south);
            } else if (bearing_ < 247.5) {
                bearing.setImageResource(R.drawable.arrow_southwest);
            } else if (bearing_ < 292.5) {
                bearing.setImageResource(R.drawable.arrow_west);
            } else {
                bearing.setImageResource(R.drawable.arrow_northwest);
            }
        }
        return view;
    }

    public void clear() {
        this.devices.clear();
        notifyDataSetChanged();
    }
}
