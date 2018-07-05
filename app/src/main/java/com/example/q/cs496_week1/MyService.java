package com.example.q.cs496_week1;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.q.cs496_week1.Model.DateObject;
import com.example.q.cs496_week1.Model.LocationObject;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

public class MyService extends Service implements LocationListener {
    ServiceThread thread;
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;
    public static String str_receiver = "myApp.service.receiver";

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread(handler);
        thread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        thread.stopForever();
        thread = null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            Intent intent = new Intent(MyService.this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            fn_getlocation();
            Realm.init(getApplicationContext());
            Realm realm = null;

            try {
                realm = Realm.getInstance(Realm.getDefaultConfiguration());
            } catch (RealmMigrationNeededException r) {
                Realm.deleteRealm(Realm.getDefaultConfiguration());
                realm = Realm.getDefaultInstance();
            }

            Log.d("FDF", realm.getPath());
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    Date today = cal.getTime();

                    DateObject date = realm.where(DateObject.class).equalTo("date", today.getTime()).findFirst();

                    if(date == null){
                       date = realm.createObject(DateObject.class);
                       date.setDate(today.getTime());
                    }
                    LocationObject location = realm.createObject(LocationObject.class);
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    location.setDate(new Date());
                    date.addLocation(location);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Log.d("SUCCESS", "뭐");
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Log.d("FAIL", "뭐");
                }
            });
            realm.close();
        }
    }

    private void fn_getlocation() {
        //latitude=  current Latitude
        //longitude = current Longitude

        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable) {

        } else {
            location = null;
            if (locationManager != null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);

                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null) {
                    Log.d("GPS TEST", "GPS not worked, get location by network");
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }

                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Log.d("latitude", latitude + "");
                    Log.d("longitude", longitude + "");
                } else {
                    Log.d("GPS TEST", "Both GPS method did not work");
                }
            }
        }
    }
}
