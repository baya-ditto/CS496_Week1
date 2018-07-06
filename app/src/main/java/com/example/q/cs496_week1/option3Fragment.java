package com.example.q.cs496_week1;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.DrawableRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.q.cs496_week1.Model.DateObject;
import com.example.q.cs496_week1.Model.LocationObject;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    private double distance_of_today = 0;

    ArrayList<Marker> markers = new ArrayList<>();
    static int interval;
    static Timer timer;

    public static final int GOBACK_TO_FRAG3 = 1;
    public static int marker_id = -1;
    public static ArrayList<Integer> marker_set = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.option3_fragment, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Date today = Calendar.getInstance().getTime();
        Log.d("Make data test", "today.getTime() = " + today.getTime());
        marker_set.add(R.drawable.smile_face_t);
        marker_set.add(R.drawable.ic_tag_faces_black_24dp);
        marker_set.add(R.drawable.two_foot_t_up);
        marker_set.add(R.drawable.ic_airplanemode_active_black_24dp);
        marker_set.add(R.drawable.ic_pool_black_24dp);


        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageButton marker_menu = rootView.findViewById(R.id.select_marker);
        LinearLayout options = rootView.findViewById(R.id.marker_options);
        Log.d("Marker test", "Add options");
        for (int i = 0; i < options.getChildCount(); ++i){
            ImageButton v = (ImageButton) options.getChildAt(i);
            v.setTag(marker_set.get(i));
            v.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int srcid = (int) v.getTag();
                    if (srcid == marker_id)
                        marker_id = -1;
                    else
                        marker_id = (int) v.getTag();
                }
            });
        }
        /*for (int srcid : marker_set){

            Log.d("Marker test", "start add options");
            ImageButton v = new ImageButton(getContext());

            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(50, 50);
            v.setLayoutParams(params);
            v.setBackgroundResource(R.color.transparent);
            v.setAdjustViewBounds(true);
            v.setScaleType(ImageView.ScaleType.FIT_CENTER);

            v.setTag(srcid);
            v.setImageResource(srcid);

            Log.d("Marker test", "Clicker setting");
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    marker_id = (int) ((ImageButton) v).getTag();
                    Log.d("Marker test", "Clicked! marker_id = " + String.valueOf(marker_id));
                }
            });
            options.addView(v);
        }*/
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
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#808080\">" + "발자취" + "</font>"));
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
        MenuItem edit = menu.add(Menu.NONE, R.id.calendar_item, 20, "달력");
        edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        edit.setIcon(R.drawable.ic_calendar_black_24dp);

        MenuItem play = menu.add(Menu.NONE, R.id.play_item, 10, "기록 재생");
        play.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        play.setIcon(R.drawable.ic_play_arrow_black_24dp);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.calendar_item:
                Intent intent = new Intent(getActivity(),CalenderActivity.class);
                getActivity().startActivityForResult(intent, GOBACK_TO_FRAG3);
                return true;
            case R.id.play_item:
                timer.cancel();
                interval = 0;
                for(int i=0;i<markers.size();i++)
                    markers.get(i).remove();
                doMarkerAnimation(100);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }*/

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

                        //BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.two_foot);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.draggable(false);
                        markerOptions.position(todayCourse.get(count));
                        if (option3Fragment.marker_id != -1)
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(get_and_resize_MapIcons(getActivity(), option3Fragment.marker_id, 100, 100)));
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

        // For test
        /*realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DateObject date = realm.createObject(DateObject.class);
                date.setDate((new Date(2018, 7, 3)).getTime());
                LocationObject location = realm.createObject(LocationObject.class);
                location.setLatitude(37.421998333330000);
                location.setLongitude(-122.08400000006000);
                location.setDate(new Date(2018,7,3,11,20));
                date.addLocation(location);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d("SUCCESS", "Test data inserted");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                //Log.d("FAIL", "뭐");
            }
        });*/
        //realm.close();



        DateObject result =  realm.where(DateObject.class)
                .between("date", today.getTime(), new Date().getTime())
                .findFirst();

        DateObject a = realm.where(DateObject.class)
                .between("date", today.getTime(), new Date().getTime()).findFirst();

        realm.beginTransaction();
        RealmList<LocationObject> locationList = new RealmList<>();
        if(result != null) {
            locationList = result.getLocations();
            distance_of_today = result.getDistance_of_day();
            ((TextView) getActivity().findViewById(R.id.today_dist)).setText("약 " + String.valueOf(distance_of_today) + "km");
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