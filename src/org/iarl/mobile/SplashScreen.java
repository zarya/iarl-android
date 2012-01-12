package org.iarl.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (waited < 5000) {
                        sleep(100);
                        waited += 100;

                        // Stop if we have a location fix
                        if (waited > 500 && DeviceActivity.location != null) {
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    // Ignore
                } finally {
                    finish();
                    if (DeviceActivity.location != null) {
                        Intent i = new Intent();
                        i.setClassName("org.iarl.mobile",
                            "org.iarl.mobile.MainActivity");
                        startActivity(i);
                    } else {
                        showConnectivityAlert();
                    }
                }
            }
        };
        splashThread.start();
    }

    private void showConnectivityAlert() {
        /*
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("No connectivity");
        alertDialog.setMessage("We could not contact the IARL server, maybe you are on a slow link or have limited connectivity.");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            */
                Intent i = new Intent();
                i.setClassName("org.iarl.mobile",
                    "org.iarl.mobile.MainActivity");
                startActivity(i);
                return;
            /*
            }
        });
        */
    }
}
