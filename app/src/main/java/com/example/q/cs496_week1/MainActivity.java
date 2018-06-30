package com.example.q.cs496_week1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RemoteControlClient;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static gallery_package gallery_storage = null;

    /*
        MainActivity.json_db =
	        [
	            {"name":"test seo","phone_number":["(010) 496-496"],"email_info":[{"email":"asdf@asdf.com","type":"1"},{"email":"asdf@work.com","type":"2"}],"notes":["it's note"]}
	        ]
     */
    public static JSONArray json_db = null;
    public static ArrayList<String> names = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_phonebook:
                        //Toast.makeText(getApplicationContext(), "phonebook", Toast.LENGTH_LONG).show();
                        getSupportActionBar().setTitle("Phone Book");
                        fragment = new PhonebookFragment();
                        loadFragment(fragment);
//                        Intent intent = new Intent(MainActivity.this, PhonebookActivity.class);
//                        startActivity(intent);
                        return true;
                    case R.id.action_gallery:
                        Toast.makeText(getApplicationContext(), "gallery", Toast.LENGTH_LONG).show();
                        getSupportActionBar().setTitle("Gallery");
                        fragment = new GalleryFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.action_three:
                        getSupportActionBar().setTitle("Option3");
                        fragment = new option3Fragment();
                        loadFragment(fragment);
                        Toast.makeText(getApplicationContext(), "third action", Toast.LENGTH_LONG).show();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportActionBar().setTitle("Phone Book");
        loadFragment(new PhonebookFragment());
    }


    private void loadFragment(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}