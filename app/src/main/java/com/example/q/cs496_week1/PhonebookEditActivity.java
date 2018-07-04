package com.example.q.cs496_week1;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class PhonebookEditActivity extends AppCompatActivity {

    public static final String ModeMsg = "mode";
    public static final int EDIT_MODE = 1;
    public static final int CREATE_MODE = 0;
    //static final int PICK_CONTACT_REQUEST = 1;

    private String contactid = null;
    private JSONObject contacts = null;
    private String name = null;
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonebook_edit);

        mode = getIntent().getIntExtra(ModeMsg,-1);


        if (mode == EDIT_MODE){
            contactid = getIntent().getStringExtra("contactid");
            contacts = MainActivity.contactList.getJSONObjectByContactId(contactid);
            show_prev_data();
        }


        Button save_but = findViewById(R.id.save_button);
        Button cancel_but = findViewById(R.id.cancel_button);
        Button add_number_but = findViewById(R.id.add_number_button);
        Button add_email_but = findViewById(R.id.add_email_button);

        save_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Log.d("TESTTEST", "Here?");
                if (saveData()){
                    System.out.println("Save success, let's go back!");
                    if (mode == CREATE_MODE)
                        PhonebookEditActivity.this.finish();
                    else {
                        Intent intent = new Intent(PhonebookEditActivity.this, MainActivity.class);
                        intent.putExtra(MainActivity.redirectTag, MainActivity._phonebook_detail);
                        intent.putExtra(MainActivity.contactidTag, contactid);
                        startActivity(intent);
                    }
                } else {
                    finish();
                }
            }
        });

        cancel_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                finish();
            }
        });

        add_number_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                LinearLayout numbers_layout = findViewById(R.id.numbers_layout);
                numbers_layout.addView(get_number_info(null, null));
            }
        });

        add_email_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                LinearLayout emails_layout = findViewById(R.id.emails_layout);
                emails_layout.addView(get_email_info(null, "개인", null));
            }
        });


    }

    private LinearLayout get_number_info(String number, String data_id){
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout info_layout = (LinearLayout) vi.inflate(R.layout.number_info_edit, null);

        TextView number_text_view = info_layout.findViewById(R.id.number);
        if (number != null)
            number_text_view.setText(number);
        if (data_id != null)
            number_text_view.setTag(data_id);

        Button delete_number_button = info_layout.findViewById(R.id.delete_number);
        delete_number_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });
        return info_layout;
    }

    private LinearLayout get_email_info(String email, String type, String data_id){
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout info_layout = (LinearLayout) vi.inflate(R.layout.email_info_edit, null);

        EditText email_text_view = info_layout.findViewById(R.id.email);
        if (email != null)
            email_text_view.setText(email);
        if (data_id != null)
            email_text_view.setTag(data_id);

        Button type_button = info_layout.findViewById(R.id.type);
        switch (type) {
            case "1":
                type_button.setText("개인");
                break;
            case "2":
                type_button.setText("직장");
                break;
            default:
                type_button.setText("기타");
                break;
        }

        Button delete_email_button = info_layout.findViewById(R.id.delete_email);
        delete_email_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return info_layout;
    }

    private void show_prev_data() {
        EditText name_text = findViewById(R.id.name);
        name = contacts.optString("name");
        name_text.setText(name);

        // show phone numbers
        LinearLayout numbers_layout = (LinearLayout) findViewById(R.id.numbers_layout);
        JSONArray number_infos = contacts.optJSONArray("numbers");
        for (int i = 0; i < number_infos.length(); ++i) {
            JSONObject number_info = number_infos.optJSONObject(i);

            String number = number_info.optString("number");
            String data_id = number_info.optString("id");

            numbers_layout.addView(get_number_info(number, data_id));
        }

        // show email infos
        LinearLayout emails_layout = findViewById(R.id.emails_layout);
        JSONArray email_infos = contacts.optJSONArray("emails");

        for (int i = 0; i < email_infos.length(); ++i)
        {
            JSONObject email_info = email_infos.optJSONObject(i);

            String email = email_info.optString("email");
            String type = email_info.optString("type");
            String data_id = email_info.optString("id");

            emails_layout.addView(get_email_info(email, type, data_id));
        }

        // show note info
        String note = contacts.optString("notes");
        EditText note_text_view = findViewById(R.id.note);
        note_text_view.setText(note);
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static String my_phone_format(String pre_format_number) {
        String str = pre_format_number.replaceAll("[-()\\s]", "");
        String formatted_number = "";
        for (int i = 0; i < str.length(); ++i){
            formatted_number += str.charAt(i);
            if (i == 2 || i == 6)
                formatted_number += '-';
        }
        return formatted_number;
    }
    private boolean saveData(){
        Log.d("TESTTEST", "Here we are");
        EditText name_text = findViewById(R.id.name);
        name = name_text.getText().toString();

        LinearLayout numbers_layout = findViewById(R.id.numbers_layout);
        ArrayList<Pair<String, String>> numbers = new ArrayList<>();
        for (int i = 0 ; i < numbers_layout.getChildCount(); ++i){
            View v = numbers_layout.getChildAt(i);
            if (v instanceof LinearLayout){
                EditText number_text = v.findViewById(R.id.number);
                String number = number_text.getText().toString();
                String data_id = (String) number_text.getTag();
                numbers.add(new Pair<String, String>(my_phone_format(number), data_id));
            }
        }

        Log.d("TESTTEST", "here I am1");

        LinearLayout emails_layout = findViewById(R.id.emails_layout);
        ArrayList<Triplet<String, String, String>> email_infos = new ArrayList<>();
        for (int i = 0 ; i < emails_layout.getChildCount(); ++i){
            View v = emails_layout.getChildAt(i);
            if (v instanceof LinearLayout){
                EditText email_text = v.findViewById(R.id.email);
                CharSequence email_seq = email_text.getText();

                // email check
                if (!isValidEmail(email_seq)){
                    numbers.clear();
                    email_infos.clear();
                    Log.d("TESTTEST", "Wrong email format");
                    Toast.makeText(getApplicationContext(), "Wrong email format", Toast.LENGTH_LONG);
                    return false;
                }

                Button type_button = v.findViewById(R.id.type);
                String type = type_button.getText().toString();

                String data_id = (String) email_text.getTag();

                email_infos.add(new Triplet<String, String, String>(email_seq.toString(), type, data_id));
            }
        }

        EditText note_text = findViewById(R.id.note);
        String note = note_text.getText().toString();

        Log.d("TESTTEST", "here I am");
        boolean success = addORupdateContact((mode == EDIT_MODE) ? contacts.optString("contactid", null) : null, name, numbers, email_infos, note);
        if (!success){
            Toast.makeText(getApplicationContext(), "Update failed", Toast.LENGTH_LONG);
        }

        return success;
    }



    public boolean addORupdateContact(String contactID, String contactName, ArrayList<Pair<String, String>> contactNumbers, ArrayList<Triplet<String, String, String>> contactEmail_infos, String note) {
        Log.d("TESTTEST", "here You are");
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ArrayList<ContentProviderOperation> insertops = new ArrayList<>();
        int index = insertops.size();

        /*ContentResolver cr = this.getContentResolver();
        ContentValues cv = new ContentValues();*/

        if (mode == CREATE_MODE) {
            //cv.put(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, contactName);

            insertops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build());

            insertops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, index)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contactName)
                    .build());
            /*insertops.add(ContentProviderOperation.newInsert(
                    ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build());

            insertops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, index)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contactName)
                    .build());*/

        }

        if (mode == EDIT_MODE)
            ops.add(ContentProviderOperation
                .newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE
                        + "=?", new String[]{contactID, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE})
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contactName)
                .build());


        for (int i = 0; i < contactNumbers.size(); ++i){ // contactNumbers.size() should be > 0 (since I did not added size=0 in DetailActivity
            Pair<String, String> pair = contactNumbers.get(i);
            String number = pair.first;
            String data_id = pair.second;
            if (data_id != null) {
                assert (mode == EDIT_MODE);
                ops.add(ContentProviderOperation
                        .newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE
                                        + "=? AND " + ContactsContract.CommonDataKinds.Organization.TYPE + "=? AND " + ContactsContract.Data._ID + "=?"
                                , new String[]{contactID, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                                        String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE), data_id})
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                        .build());
            }
            else if (mode == CREATE_MODE){
                //cv.put(ContactsContract.CommonDataKinds.Phone.NUMBER, number);
                //cv.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);

                insertops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, index)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .build());

                /*insertops.add(ContentProviderOperation
                        .newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, index)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .build());*/
            }
            //index++;
            //System.out.println(index);
        }

        for (int i = 0; i < contactEmail_infos.size(); ++i){
            Triplet<String, String, String> email_info = contactEmail_infos.get(i);
            String contactEmail = email_info.first;
            String contactEmailType = email_info.second;
            String data_id = email_info.third;
            int emailType = ContactsContract.CommonDataKinds.Email.TYPE_OTHER;
            switch (contactEmailType) {
                case "개인":
                    emailType = ContactsContract.CommonDataKinds.Email.TYPE_HOME;
                    break;
                case "직장":
                    emailType = ContactsContract.CommonDataKinds.Email.TYPE_WORK;
                    break;
            }
            System.out.println("Updated string : " + contactEmail);

            if (data_id != null) {
                assert (mode == EDIT_MODE);
                ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.Data._ID + "=?"
                                , new String[]{contactID, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, data_id})
                        .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, contactEmail)
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, emailType)
                        .build());
            }
            else if (mode == CREATE_MODE){
                /*cv.put(ContactsContract.CommonDataKinds.Email.DATA, contactEmail);
                cv.put(ContactsContract.CommonDataKinds.Email.TYPE, emailType);*/

                insertops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, index)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, contactEmail)
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                        .build());
                /*ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, index)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, contactEmail)
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, emailType)
                        .build());*/
                /*insertops.add(ContentProviderOperation.newInsert(ContactsContract.CommonDataKinds.Email.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, index)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, contactEmail)
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, emailType)
                        .build());*/
            }
            //index++;
            //System.out.println(index);
        }

        if (mode == EDIT_MODE)
            ops.add(ContentProviderOperation
                    .newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?"
                            , new String[]{contactID, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE})
                    .withValue(ContactsContract.CommonDataKinds.Note.NOTE, note)
                    .build());
        else if (mode == CREATE_MODE)
            //cv.put(ContactsContract.CommonDataKinds.Note.NOTE, note);
        try {
            if (mode == EDIT_MODE)
                getApplicationContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            else if (mode == CREATE_MODE)
                getContentResolver().applyBatch(ContactsContract.AUTHORITY, insertops);
                //cr.insert(ContactsContract.RawContacts.CONTENT_URI, cv);
                //getApplicationContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, insertops);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}