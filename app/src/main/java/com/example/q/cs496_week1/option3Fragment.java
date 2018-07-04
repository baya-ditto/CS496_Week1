package com.example.q.cs496_week1;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.joda.time.DateTimeComparator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.Double.parseDouble;

public class option3Fragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    private ArrayList<LatLng> todayCourse;

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

                Date today = new Date();
                today.setHours(0); today.setMinutes(0); today.setSeconds(0);
                todayCourse = Helper.getLatLngList(today, new Date());

                PolylineOptions options = new PolylineOptions();
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_item:
                Intent intent = new Intent(getActivity(),CalenderActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}