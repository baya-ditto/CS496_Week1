package com.example.q.cs496_week1;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    private String contactid = null;
    private JSONObject contacts = null;
    private String name = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonebook_edit);


        contactid = getIntent().getStringExtra("contactid");
        contacts = MainActivity.contactList.getJSONObjectByContactId(contactid);
        show_prev_data();


        Button save_but = findViewById(R.id.save_button);
        Button cancel_but = findViewById(R.id.cancel_button);

        save_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Log.d("TESTTEST", "Here?");
                save_interact();
            }
        });

        cancel_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(PhonebookEditActivity.this);
                builder.setTitle("저장하고 돌아가시겠습니까?");
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        save_interact();
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
                finish();
            }
        });

    }


    private void save_interact() {
        AlertDialog.Builder builder0 = new AlertDialog.Builder(PhonebookEditActivity.this);
        builder0.setTitle("저장하시겠습니까?");
        builder0.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (saveData()){
                    dialog.dismiss();
                    System.out.println("Save success, let's go back!");
                    Intent intent = new Intent(PhonebookEditActivity.this, MainActivity.class);
                    intent.putExtra(MainActivity.redirectTag, MainActivity._phonebook_detail);
                    intent.putExtra(MainActivity.contactidTag, contactid);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PhonebookEditActivity.this);
                    builder.setTitle("저장이 실패했습니다. 작업을 다시 하시겠습니까?");
                    builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialog.dismiss();
                    AlertDialog new_dialog = builder.create();
                    new_dialog.show();
                }
            }
        });
        builder0.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder0.create();
        dialog.show();
    }

    private LinearLayout get_number_info(String number, String data_id){
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout info_layout = (LinearLayout) vi.inflate(R.layout.number_info_edit, null);

        EditText number_text_view = info_layout.findViewById(R.id.number);
        if (number != null) {
            number_text_view.setText(number);
            number_text_view.setTag(number);
        }
        if (data_id != null)
            number_text_view.setTag(data_id);

        Button clear_number_button = info_layout.findViewById(R.id.clear_number);
        clear_number_button.setTag(number_text_view);
        clear_number_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((EditText) v.getTag()).setText("");
            }
        });

        Button recover_number_button = info_layout.findViewById(R.id.recover_number);
        recover_number_button.setTag(number_text_view);
        recover_number_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                EditText number_tv = (EditText) v.getTag();
                if (number_tv.getTag() != null)
                    number_tv.setText((String) number_tv.getTag());
            }
        });
        return info_layout;
    }

    private LinearLayout get_email_info(String email, String type, String data_id){
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout info_layout = (LinearLayout) vi.inflate(R.layout.email_info_edit, null);

        EditText email_text_view = info_layout.findViewById(R.id.email);
        if (email != null) {
            email_text_view.setText(email);
            email_text_view.setTag(email);
        }
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

        Button clear_email_button = info_layout.findViewById(R.id.clear_email);
        clear_email_button.setTag(email_text_view);
        clear_email_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText) v.getTag()).setText("");
            }
        });

        Button recover_email_button = info_layout.findViewById(R.id.recover_email);
        recover_email_button.setTag(email_text_view);
        recover_email_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email_tv = (EditText) v.getTag();
                if (email_tv.getTag() != null)
                    email_tv.setText((String) email_tv.getTag());
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
        boolean success = updateContact(contacts.optString("contactid", null), name, numbers, email_infos, note);
        if (!success){
            Toast.makeText(getApplicationContext(), "Update failed", Toast.LENGTH_LONG);
        }

        return success;
    }




    public boolean updateContact(String contactID, String contactName, ArrayList<Pair<String, String>> contactNumbers, ArrayList<Triplet<String, String, String>> contactEmail_infos, String note) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();


        ops.add(ContentProviderOperation
            .newUpdate(ContactsContract.Data.CONTENT_URI)
            .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE
                    + "=?", new String[]{contactID, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE})
            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contactName)
            .build());


        for (int i = 0; i < contactNumbers.size(); ++i){
            Pair<String, String> pair = contactNumbers.get(i);
            String number = pair.first;
            String data_id = pair.second;
            assert data_id != null;
            ops.add(ContentProviderOperation
                    .newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE
                                    + "=? AND " + ContactsContract.CommonDataKinds.Organization.TYPE + "=? AND " + ContactsContract.Data._ID + "=?"
                            , new String[]{contactID, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                                    String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE), data_id})
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                    .build());
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

            assert data_id != null;
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.Data._ID + "=?"
                            , new String[]{contactID, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, data_id})
                    .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, contactEmail)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, emailType)
                    .build());
        }

        ops.add(ContentProviderOperation
                .newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?"
                        , new String[]{contactID, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE})
                .withValue(ContactsContract.CommonDataKinds.Note.NOTE, note)
                .build());
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}