package com.example.q.cs496_week1;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.RemoteControlClient;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static gallery_package gallery_storage = null;
    /*
        MainActivity.json_db =
	        [
	            {"name":"test seo",
	            "phone_number":["(010) 496-496"],
	            "email_info":[{"email":"asdf@asdf.com","type":"1"},
	            {"email":"asdf@work.com","type":"2"}],
	            "note":"it's note"}
	        ]
     */
    public static JSONArray json_db = null;
    public static ArrayList<String> names = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String[] PERMISSIONS = {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_CONTACTS };

        if (!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this,PERMISSIONS, 1);
        }

        super.onCreate(savedInstanceState);

        if (MainActivity.json_db == null) {
            MainActivity.json_db = new JSONArray();
            MainActivity.names = new ArrayList<String>();
            if(MainActivity.hasPermissions(this,new String[]{Manifest.permission.READ_CONTACTS}))
                loadContacts();
        }

        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_phonebook:
                        fragment = new PhonebookFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.action_gallery:
                        fragment = new GalleryFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.action_three:
                        fragment = new option3Fragment();
                        loadFragment(fragment);
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
//        getSupportActionBar().setTitle("Phone Book");
        loadFragment(new PhonebookFragment());
    }

    public static boolean hasPermissions(Context context, String... permissions){
        if (context != null && permissions != null) {
            for (String permission : permissions){
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void loadContacts() {
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                ArrayList<String> phone_numbers = new ArrayList<String>();
                ArrayList<Pair<String, String>> emails = new ArrayList<Pair<String, String>>();
                //ArrayList<String> notes = new ArrayList<String>();
                String note = "";

                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    System.out.println("name : " + name + ", ID : " + id);

                    // get the phone number
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phone = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        System.out.println("phone" + phone);
                        phone_numbers.add(phone);
                    }
                    pCur.close();


                    // get email and type
                    Cursor emailCur = cr.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCur.moveToNext()) {
                        // This would allow you get several email addresses
                        // if the email addresses were stored in an array
                        String email = emailCur.getString(
                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        String emailType = emailCur.getString(
                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                        System.out.println("Email " + email + " Email Type : " + emailType);

                        emails.add(new Pair<String, String>(email, emailType));
                    }
                    emailCur.close();

                    // Get note.......
                    String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] noteWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
                    Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
                    if (noteCur.moveToFirst()) {
                        note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                        System.out.println("Note " + note);

                        //notes.add(note);
                    }
                    noteCur.close();

                    // email_info preprocessing
                    JSONArray email_infos = new JSONArray();
                    for (Pair<String, String> pair : emails) {
                        JSONObject email_info = new JSONObject();
                        try {
                            email_info.put("email", pair.first);
                            email_info.put("type", pair.second);
                            email_infos.put(email_info);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    JSONObject user = new JSONObject();
                    try {
                        MainActivity.names.add(name);
                        user.put("name", name);
                        user.put("phone_number", new JSONArray(phone_numbers));
                        user.put("email_info", email_infos);
                        user.put("note", note);
                        MainActivity.json_db.put(user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}