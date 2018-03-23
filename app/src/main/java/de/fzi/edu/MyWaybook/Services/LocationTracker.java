package de.fzi.edu.MyWaybook.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import de.fzi.edu.MyWaybook.HomeScreen;
import de.fzi.edu.MyWaybook.Manifest;
import de.fzi.edu.MyWaybook.R;


/**
 * Created by rickert on 09.12.2016.
 * This Class provides a service for locationtracking.
 */

public class LocationTracker extends Service implements LocationListener {

    private LocationManager locationManager;
    public static Handler updateHandler;
    public static boolean GPSEnabled;

    /**
     * Required implementation by Service Class. Not used yet
     * @param intent
     * @return null
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Called by startService method. Checks for Locationupdates and builds a Notification for the Statusbar.
     * @param intent not used
     * @param flags not used
     * @param startId not used
     * @return super
     */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        GPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (GPSEnabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 6, this);
            Intent notificationIntent = new Intent(this, HomeScreen.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification = new Notification.Builder(this)
                    .setContentTitle("Wegetagebuch")
                    .setContentText("Weg wird aufgezeichnet")
                    .setSmallIcon(R.drawable.play2)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(1, notification);
        }else{
            Toast.makeText(this,"Bitte lassen Sie den Standortzugriff zu", Toast.LENGTH_LONG).show();
        }




        return super.onStartCommand(intent, flags, startId);


    }

    /**
     * Called when the service is stopped via stopService. Removes the Statusbar Notification
     */
    @Override
    public void onDestroy() {
        locationManager.removeUpdates(this);
        stopForeground(true);
        super.onDestroy();

    }

    /**
     * Called when a new Location is received. Checks if Accuracy is sufficient and passes it
     * to the TrackHandler class
     * @param location received by the LocationManager
     */
    @Override
    public void onLocationChanged(Location location) {
        if (location.getAccuracy()<21) {
            Message msg = Message.obtain();

            msg.obj = location;


            if (updateHandler != null && msg.obj != null) {
                updateHandler.sendMessage(msg);
            }
        }

    }

    /**
     * Called when the provider status changes.
     * @param provider name of the provider
     * @param status status of the provider
     * @param extras status specific variables
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    /**
     * Called when the provider gets enabled
     * @param provider name of the provider
     */

    @Override
    public void onProviderEnabled(String provider) {

    }

    /**
     * Called when the provider gets disabled.
     * @param provider name of the provider
     */

    @Override
    public void onProviderDisabled(String provider) {

    }

}
