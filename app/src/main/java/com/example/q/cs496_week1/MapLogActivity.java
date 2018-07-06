package com.example.q.cs496_week1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.q.cs496_week1.Model.DateObject;
import com.example.q.cs496_week1.Model.LocationObject;
import com.google.android.gms.common.util.MapUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class MapLogActivity extends AppCompatActivity {

    MapView mMapView;
    private GoogleMap googleMap;
    long[] pickedList;
    ArrayList<LatLng>[] latLngList; // i-th date -> loclist of the very date
    ArrayList<LatLng> latLngList2 = new ArrayList<>(); // total locations within selected dates
    ArrayList<Marker> markers = new ArrayList<>();
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
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#808080\">" + "기록 보기" + "</font>"));

        Intent intent = getIntent();
        pickedList = intent.getLongArrayExtra("dates");
        if(pickedList != null){
            start = new Date(pickedList[0]);
            end = new Date(pickedList[pickedList.length-1]);

        } else {
            Toast.makeText(this,"날짜를 지정해주세요!", Toast.LENGTH_LONG).show();
            onBackPressed();
        }

        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        Log.d("PICKSD", String.valueOf(pickedList.length));
        latLngList = new ArrayList[pickedList.length];
        for (int temp = 0; temp < pickedList.length; ++temp){
            latLngList[temp] = new ArrayList<LatLng>();
        }

        Calendar c = Calendar.getInstance();
        c.setTime(end);
        c.add(Calendar.DATE, 1);
        c.add(Calendar.SECOND, -1);
        end = c.getTime();
        Realm.init(getApplicationContext());
        Realm realm = Realm.getDefaultInstance();

        RealmResults<DateObject> result =  realm.where(DateObject.class)
                .between("date", start.getTime(), end.getTime() )
                .findAll();

        Log.d("TESTT",String.valueOf(start));
        Log.d("TESTT",String.valueOf(end));

        realm.beginTransaction();

        for(int i=0;i<result.size();i++){
            RealmList<LocationObject> arr = result.get(i).getLocations();
            for(int j=0;j<arr.size();j++){
                LocationObject location = arr.get(j);
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                latLngList[i].add(loc);
                latLngList2.add(loc);
            }
        }

        realm.commitTransaction();
        realm.close();

        if(latLngList2.size() == 0){
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle("오류")
                    .setMessage("설정된 날짜에 해당하는 기록이 없습니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            dialog.dismiss();
                            onBackPressed();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {

            try {
                MapsInitializer.initialize(getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            ImageButton marker_menu = findViewById(R.id.select_marker);
            LinearLayout options = findViewById(R.id.marker_options);
            Log.d("Marker test", "Add options");
            for (int i = 0; i < options.getChildCount(); ++i){
                ImageButton v = (ImageButton) options.getChildAt(i);
                v.setTag(option3Fragment.marker_set.get(i));
                v.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        int srcid = (int) v.getTag();
                        if (srcid == option3Fragment.marker_id)
                            option3Fragment.marker_id = -1;
                        else
                            option3Fragment.marker_id = (int) v.getTag();
                    }
                });
            }

            marker_menu.setTag(options);
            marker_menu.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Log.d("Marker test", "menu clicked");
                    LinearLayout options = (LinearLayout) v.getTag();
                    if(options.getVisibility() == View.GONE) {
                        options.setVisibility(View.VISIBLE);
                    }
                    else {
                        options.setVisibility(View.GONE);
                    }
                }
            });

            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    Random rand = new Random();

                    for (int i = 0; i < latLngList.length; i++) {
                        if (latLngList[i].size() == 0) continue;
                        PolylineOptions options = new PolylineOptions();
                        options.color(Color.argb(255, rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
                        options.width(3);
                        for (int j = 0; j < latLngList[i].size(); j++) {
                            LatLng latlng = latLngList[i].get(j);
                            options.add(latlng);
                            builder.include(latlng);
                        }
                        googleMap.addPolyline(options);
                    }

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 200));

                    doMarkerAnimation(3000);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem play = menu.add(Menu.NONE, R.id.play_item, 10, "재생");
        play.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        play.setIcon(R.drawable.ic_play_arrow_black_24dp);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent1 = new Intent(this, MainActivity.class);
                intent1.putExtra("Fragment","3");
                startActivity(intent1);
                return true;
            case R.id.play_item:
                timer.cancel();
                interval = 0;
                for(int i=0;i<markers.size();i++)
                    markers.get(i).remove();
                doMarkerAnimation(100);
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

    private Bitmap bitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    private Bitmap get_and_resize_MapIcons(Context context, int vectorResId,int width, int height){
        Bitmap imageBitmap = bitmapFromVector(context, vectorResId);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private void doMarkerAnimation(long delay){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable(){
                    public void run() {
                        int count = inclineInterval();
                        int de = latLngList2.size() / 10;
                        if (de == 0) de = 1;
                        while(count % de != 0)
                            count = inclineInterval();

                        if(count > latLngList2.size() - 1){
                            timer.cancel();
                            return;
                        }

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.draggable(false);
                        markerOptions.position(latLngList2.get(count));
                        if (option3Fragment.marker_id != -1)
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(get_and_resize_MapIcons(activity, option3Fragment.marker_id, 100, 100)));
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
}
