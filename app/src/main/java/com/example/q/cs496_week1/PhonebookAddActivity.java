package com.example.q.cs496_week1;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PhonebookAddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonebook_add);

        /*Toolbar mToolbar = (Toolbar)findViewById(R.id.add_toolbar);
        setSupportActionBar(mToolbar);*/

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Button save_but = findViewById(R.id.save_button);
        Button cancel_but = findViewById(R.id.cancel_button);*/
        Button add_number_but = findViewById(R.id.add_number_button);
        Button add_email_but = findViewById(R.id.add_email_button);

        /*save_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                save_interact();
            }
        });

        cancel_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(PhonebookAddActivity.this);
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
        });*/

        add_number_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                LinearLayout numbers_layout = findViewById(R.id.numbers_layout);
                numbers_layout.addView(get_number_info_layout(null));
            }
        });

        add_email_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                LinearLayout emails_layout = findViewById(R.id.emails_layout);
                emails_layout.addView(get_email_info_layout(null, "개인"));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        MenuItem save = menu.add(Menu.NONE, R.id.save_item, 50, "SAVE");
        save.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        save.setIcon(R.drawable.ic_save_black_24dp);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AlertDialog.Builder builder = new AlertDialog.Builder(PhonebookAddActivity.this);
                builder.setTitle("정말 돌아가시겠습니까? 입력한 정보는 저장되지 않습니다.");
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onBackPressed();
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            case R.id.save_item:
                save_interact();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void save_interact() {
        AlertDialog.Builder builder0 = new AlertDialog.Builder(PhonebookAddActivity.this);
        builder0.setTitle("저장하시겠습니까?");
        builder0.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (saveData()){
                    dialog.dismiss();
                    System.out.println("Save success, let's go back!");
                    Intent intent = new Intent(PhonebookAddActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PhonebookAddActivity.this);
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
    private LinearLayout get_number_info_layout(String number){
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout info_layout = (LinearLayout) vi.inflate(R.layout.number_info_add, null);

        EditText number_text_view = info_layout.findViewById(R.id.number);
        if (number != null)
            number_text_view.setText(number);

        Button delete_number_button = info_layout.findViewById(R.id.delete_number);
        delete_number_button.setTag(info_layout);
        delete_number_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //TODO: delete entry
                View info_layout = (View) v.getTag();
                ((ViewGroup) info_layout.getParent()).removeView(info_layout);
            }
        });

        Button clear_number_button = info_layout.findViewById(R.id.clear_number);
        clear_number_button.setTag(number_text_view);
        clear_number_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((EditText) v.getTag()).setText("");
            }
        });
        return info_layout;
    }

    private LinearLayout get_email_info_layout(String email, String type){
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout info_layout = (LinearLayout) vi.inflate(R.layout.email_info_add, null);

        EditText email_text_view = info_layout.findViewById(R.id.email);
        if (email != null)
            email_text_view.setText(email);

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
        delete_email_button.setTag(info_layout);
        delete_email_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: delete entry
                View info_layout = (View) v.getTag();
                ((ViewGroup) info_layout.getParent()).removeView(info_layout);
            }
        });

        Button clear_email_button = info_layout.findViewById(R.id.clear_email);
        clear_email_button.setTag(email_text_view);
        clear_email_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText) v.getTag()).setText("");
            }
        });
        return info_layout;
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
        String name = name_text.getText().toString();

        LinearLayout numbers_layout = findViewById(R.id.numbers_layout);
        ArrayList<String> numbers = new ArrayList<>();
        for (int i = 0 ; i < numbers_layout.getChildCount(); ++i){
            View v = numbers_layout.getChildAt(i);
            if (v instanceof LinearLayout){
                EditText number_text = v.findViewById(R.id.number);
                String number = number_text.getText().toString();
                numbers.add(my_phone_format(number));
            }
        }

        Log.d("TESTTEST", "here I am1");

        LinearLayout emails_layout = findViewById(R.id.emails_layout);
        ArrayList<Pair<String, String>> email_infos = new ArrayList<>();
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

                email_infos.add(new Pair<String, String>(email_seq.toString(), type));
            }
        }

        EditText note_text = findViewById(R.id.note);
        String note = note_text.getText().toString();

        Log.d("TESTTEST", "here I am");
        boolean success = addContact(name, numbers, email_infos, note);
        if (!success){
            Toast.makeText(getApplicationContext(), "Update failed", Toast.LENGTH_LONG);
        }

        return success;
    }

    public boolean addContact(String contactName, ArrayList<String> contactNumbers, ArrayList<Pair<String, String>> contactEmail_infos, String note){
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        int index = ops.size();

        // Adding insert operation to operations list
        // For insert a new raw contact in the ContactsContract.RawContacts
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // For insert display name in the ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, index)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contactName)
                .build());

        // For insert Mobile Number in the ContactsContract.Data
        for (int i = 0; i < contactNumbers.size(); ++i){ // contactNumbers.size() should be > 0 (since I did not added size=0 in DetailActivity
            String number = contactNumbers.get(i);

            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, index)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }


        // For insert Work Email in the ContactsContract.Data
        for (int i = 0; i < contactEmail_infos.size(); ++i){
            Pair<String, String> email_info = contactEmail_infos.get(i);
            String contactEmail = email_info.first;
            String contactEmailType = email_info.second;
            int emailType = ContactsContract.CommonDataKinds.Email.TYPE_OTHER;
            switch (contactEmailType) {
                case "개인":
                    emailType = ContactsContract.CommonDataKinds.Email.TYPE_HOME;
                    break;
                case "직장":
                    emailType = ContactsContract.CommonDataKinds.Email.TYPE_WORK;
                    break;
            }
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, index)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, contactEmail)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, emailType)
                    .build());
        }

        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, index)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
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
