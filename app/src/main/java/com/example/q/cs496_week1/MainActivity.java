package com.example.q.cs496_week1;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public final static String redirectTag = "redirect";
    public final static String contactidTag = "contactid";
    public final static int _phonebook_detail = 1;

    public static ContactList contactList;
    private boolean redirect_flag = false;
    private String redirect_contactid = null;


    private static String[] PERMISSIONS = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getIntExtra(redirectTag, -1) == _phonebook_detail) {
            redirect_flag = true;
            redirect_contactid = getIntent().getStringExtra(contactidTag);
        }
        if (redirect_flag)
            return;

        Intent intent = new Intent(getApplicationContext(),MyService.class);
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);

        boolean isServiceRunning = false;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.q.cs496_week1.MyService".equals(service.service.getClassName())) {
                isServiceRunning = true;
                break;
            }
        }
        if(!isServiceRunning){
            Toast.makeText(getApplicationContext(),"Service 시작",Toast.LENGTH_SHORT).show();
            startService(intent);
        }

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

    public static boolean hasPermissions(Context context, String... permissions){
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void locationAndContactsTask(Context context){

        if(hasPermissions(this,PERMISSIONS)) {
            new GetExternalData().execute();
        } else {
            ActivityCompat.requestPermissions(this,PERMISSIONS, 1);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    private class GetExternalData extends AsyncTask<String, String, ContactList> {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Fetching Contacts...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected ContactList doInBackground(String... params){
            ContentResolver cr = MainActivity.this.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);

            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    ArrayList<Pair<String, String>> phone_numbers = new ArrayList<>();
                    ArrayList<Triplet<String, String, String>> emails = new ArrayList<>();
                    String note = "";
                    String starred = "";

                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        System.out.println("name : " + name + ", ID : " + id);

                        // get the phone number
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phone = pCur.getString(
                                    pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String data_id = pCur.getString(
                                    pCur.getColumnIndex(ContactsContract.Data._ID));
                            System.out.println("phone" + phone);
                            phone_numbers.add(new Pair<String, String>(phone, data_id));
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
                            String data_id = emailCur.getString(
                                    emailCur.getColumnIndex(ContactsContract.Data._ID)
                            );

                            System.out.println("Email " + email + " Email Type : " + emailType + " data_id : " + data_id);

                            emails.add(new Triplet<String, String, String>(email, emailType, data_id));
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

                        // get starred info

                        Cursor starCur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts._ID + " =?", new String[]{id}, null);
                        if (starCur.moveToFirst()) {
                            starred = starCur.getString(starCur.getColumnIndex(ContactsContract.Contacts.STARRED));
                            System.out.println("Starred " + starred);
                        }

                        try {
                            contactList.addContact(id, name, phone_numbers, emails, note, starred);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return contactList;
        }

        @Override
        protected void onPostExecute(ContactList result) {
            super.onPostExecute(result);

            contactList.sorting(false);
            contactList.sorting(true);
            if (!redirect_flag && result != null) {
                loadFragment(new PhonebookFragment());
            }

            if (pDialog.isShowing())
                pDialog.dismiss();

            if (redirect_flag){
                Intent intent = new Intent(MainActivity.this, PhonebookDetailActivity.class);
                intent.putExtra("contactid", redirect_contactid);
                redirect_flag = false;
                startActivity(intent);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        String test = getIntent().getStringExtra("Fragment");
        if(test != null){
            loadFragment(new option3Fragment());
            return;
        }
        contactList = new ContactList();
        locationAndContactsTask(this);
    }
}