package com.example.q.cs496_week1;

import android.content.Intent;
import android.media.RemoteControlClient;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_phonebook:
                        //Toast.makeText(getApplicationContext(), "phonebook", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this, PhonebookActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.action_gallery:
                        Toast.makeText(getApplicationContext(), "gallery", Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.action_three:
                        Toast.makeText(getApplicationContext(), "third action", Toast.LENGTH_LONG).show();
                        return true;
                }
                return false;
            }
        });
    }
}
