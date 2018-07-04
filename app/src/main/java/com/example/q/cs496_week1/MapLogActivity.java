package com.example.q.cs496_week1;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.ColorRes;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Interpolator;

import com.google.android.gms.common.util.MapUtils;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapLogActivity extends AppCompatActivity {

    MapView mMapView;
    private GoogleMap googleMap;
    long[] pickedList;
    ArrayList<LatLng> latLngList = new ArrayList<>();
    Date start, end;
    Activity activity = this;
    static int interval;
    static Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_log);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        pickedList = intent.getLongArrayExtra("dates");
        if(pickedList != null){
            start = new Date(pickedList[0]);
            end = new Date(pickedList[pickedList.length-1]);
            Calendar c = Calendar.getInstance();
            c.setTime(end);
            c.add(Calendar.DATE, 1);
            end = c.getTime();
            latLngList = Helper.getLatLngList(start, end);
        } else {
            //TODO: 날짜를 지정해주세요 에러 띄우기
        }

        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                PolylineOptions options = new PolylineOptions();
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                for(int i=0;i<latLngList.size();i++){
                    LatLng latlng = latLngList.get(i);
                    builder.include(latlng);
                    options.add(latlng);

                }
                options.color(Color.RED);
                options.width(3);
                googleMap.addPolyline(options);

                if(latLngList.size() != 0) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),200));
                }


                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        activity.runOnUiThread(new Runnable(){
                            public void run() {
//                                int count = setInterval(latLngList.size());
//                                if(count % (latLngList.size() / 10) != 0) return;
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.draggable(false);
                                markerOptions.position(latLngList.get(setInterval(setInterval(latLngList.size()))));
                                Marker pinnedMarker = googleMap.addMarker(markerOptions);
                                startDropMarkerAnimation(pinnedMarker);
                            }

                        });
                    }
                };
                timer = new Timer();
                long delay = 500;
                long intervalPeriod = 1 * 300;
                timer.scheduleAtFixedRate(task, delay, intervalPeriod);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent1 = new Intent(this, MainActivity.class);
                intent1.putExtra("Fragment","3");
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
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

    private static final int setInterval(int size) {
        if (interval == size -1)
            timer.cancel();
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
}
