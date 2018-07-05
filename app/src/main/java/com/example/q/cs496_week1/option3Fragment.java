package com.example.q.cs496_week1;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import com.example.q.cs496_week1.Model.DateObject;
import com.example.q.cs496_week1.Model.LocationObject;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.joda.time.DateTimeComparator;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static java.lang.Double.parseDouble;

public class option3Fragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    private ArrayList<LatLng> todayCourse = new ArrayList<>();
    ArrayList<Marker> markers = new ArrayList<>();
    static int interval;
    static Timer timer;

    public static final int GOBACK_TO_FRAG3 = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.option3_fragment, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);

                reService();
                doMarkerAnimation(3000);
            }
        });

        return rootView;
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
        }
        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuItem edit = menu.add(Menu.NONE, R.id.edit_item, 10, R.string.edit_item);
        edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        edit.setIcon(R.drawable.ic_edit);

        MenuItem delete = menu.add(Menu.NONE, R.id.delete_item, 20, R.string.edit_item);
        edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        edit.setIcon(R.drawable.ic_edit);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_item:
                Intent intent = new Intent(getActivity(),CalenderActivity.class);
                //startActivity(intent);
                startActivityForResult(intent, GOBACK_TO_FRAG3);
                return true;
            case R.id.delete_item:
                timer.cancel();
                interval = 0;
                for(int i=0;i<markers.size();i++)
                    markers.get(i).remove();
                doMarkerAnimation(100);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doMarkerAnimation(long delay){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(getActivity() == null)
                    return;
                getActivity().runOnUiThread(new Runnable(){
                    public void run() {
                        int count = inclineInterval();
                        int de = todayCourse.size()/ 10;
                        if (de == 0) de = 1;
                        while(count % de != 0)
                            count = inclineInterval();

                        if(count > todayCourse.size() - 1){
                            timer.cancel();
                            return;
                        }

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.draggable(false);
                        markerOptions.position(todayCourse.get(count));
                        Marker pinnedMarker = googleMap.addMarker(markerOptions);
                        markers.add(pinnedMarker);
                        startDropMarkerAnimation(pinnedMarker);
                    }
                });
            }
        };
        timer = new Timer();
        long intervalPeriod = 1 * 700;
        timer.scheduleAtFixedRate(task, delay, intervalPeriod);
    }


    private static final int inclineInterval() {
        return interval++;
    }

    private void startDropMarkerAnimation(final Marker marker) {
        final LatLng target = marker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = googleMap.getProjection();
        Point targetPoint = proj.toScreenLocation(target);
        final long duration = (long) (200 + (targetPoint.y * 0.6));
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        startPoint.y = 0;
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final Interpolator interpolator = new LinearOutSlowInInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * target.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * target.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 16ms later == 60 frames per second
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    private void reService(){

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();

        Realm.init(getContext());
        Realm realm = Realm.getDefaultInstance();

        DateObject result =  realm.where(DateObject.class)
                .between("date", today.getTime(), new Date().getTime())
                .findFirst();

        realm.beginTransaction();
        RealmList<LocationObject> locationList = new RealmList<>();
        if(result != null) {
            locationList = result.getLocations();
        }

        for(int i=0;i<locationList.size();i++){
            LocationObject location = locationList.get(i);
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            todayCourse.add(new LatLng(lat,lng));
        }
        realm.commitTransaction();
        realm.close();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        PolylineOptions options = new PolylineOptions();
        for(int i=0;i<todayCourse.size();i++){
            LatLng latlng = todayCourse.get(i);
            builder.include(latlng);
            options.add(latlng);
        }
        options.color(Color.RED);
        options.width(3);
        googleMap.addPolyline(options);
        if(todayCourse.size() != 0) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),200));
        }
    }
}