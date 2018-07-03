package com.example.q.cs496_week1;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class PhonebookDetailActivity extends AppCompatActivity {

    private JSONObject contacts = new JSONObject();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phonebook_detail);

        Toolbar mToolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        contacts = MainActivity.contactList.getJSONObject(getIntent().getIntExtra("index",0));
        String name = contacts.optString("name");

        getSupportActionBar().setTitle(name);

        CollapsingToolbarLayout ctl = (CollapsingToolbarLayout) findViewById(R.id.main_collapsing);


        // show profile picture - default : slack default profile
        ImageView img = (ImageView) findViewById(R.id.main_backdrop);
        img.setImageResource(R.drawable.default_pic);

        // show phone numbers
        LinearLayout numbers_layout = (LinearLayout) findViewById(R.id.numbers_layout);
        JSONArray numbers = contacts.optJSONArray("numbers");

        for (int i = 0; i < numbers.length(); ++i) {
            String number = numbers.optString(i);
            EditText text_view = new EditText(this);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(10,0,0,0);

            text_view.setLayoutParams(params);
            text_view.setText(number);
            text_view.setSingleLine(true);
            text_view.setBackgroundResource(R.color.transparent);
            text_view.setTextColor(getResources().getColor(R.color.basic));
            text_view.setEnabled(false);
            text_view.setInputType(InputType.TYPE_CLASS_PHONE);
            //text_view.setInputType(InputType.TYPE_NULL);


            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);
            params.setMargins(0,10,0,10);
            View line_view = new View(this);
            line_view.setLayoutParams(params);
            line_view.setBackgroundResource(R.color.white_gray);

            numbers_layout.addView(text_view);
            numbers_layout.addView(line_view);
        }

        // show email infos
        LinearLayout emails_layout = findViewById(R.id.emails_layout);
        JSONArray email_infos = contacts.optJSONArray("emails");
        for (int i = 0; i < email_infos.length(); ++i)
        {
            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout info_layout = (LinearLayout) vi.inflate(R.layout.email_info, null);

            JSONObject email_info = email_infos.optJSONObject(i);

            String email = email_info.optString("email");
            EditText email_text_view = info_layout.findViewById(R.id.email);
            email_text_view.setText(email);
            String type = email_info.optString("type");

            TextView type_text_view = info_layout.findViewById(R.id.type);
            switch (type) {
                case "1":
                    type_text_view.setText("개인");
                    break;
                case "2":
                    type_text_view.setText("직장");
                    break;
                default:
                    type_text_view.setText("기타");
                    break;
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);
            params.setMargins(0,10,0,10);
            View line_view = new View(this);
            line_view.setLayoutParams(params);
            line_view.setBackgroundResource(R.color.white_gray);

            emails_layout.addView(info_layout);
            emails_layout.addView(line_view);
        }

        // show note info
        String note = contacts.optString("notes");
        EditText note_text_view = findViewById(R.id.note);
        note_text_view.setText(note);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        MenuItem edit = menu.add(Menu.NONE, R.id.edit_item, 10, R.string.edit_item);
        edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        edit.setIcon(R.drawable.ic_edit);

        MenuItem delete = menu.add(Menu.NONE, R.id.delete_item, 50, R.string.delete_item);
        delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        delete.setIcon(R.drawable.ic_delete);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.edit_item:
                //switch_to_edit_layout();
                Intent intent = new Intent(PhonebookDetailActivity.this, PhonebookEditActivity.class);
                intent.putExtra(PhonebookEditActivity.ModeMsg, PhonebookEditActivity.EDIT_MODE);
                startActivity(intent);
                return true;
            case R.id.delete_item:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("삭제");
                builder.setMessage("정말로 삭제하시겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getApplicationContext(), "okay", Toast.LENGTH_LONG).show();
                                //TODO: delete & update contact
                                deleteContact();
                                onBackPressed();
                            }
                        });
                builder.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getApplicationContext(), "Deny", Toast.LENGTH_LONG).show();
                            }
                        });
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void switch_to_edit_layout() {
        // make EditText modifiable

        LinearLayout numbers_layout = findViewById(R.id.numbers_layout);
        for (int i = 0; i < numbers_layout.getChildCount(); ++i) {
            View v = numbers_layout.getChildAt(i);
            if (v instanceof EditText) {
                //((EditText) v).setInputType(InputType.TYPE_CLASS_PHONE);
                ((EditText) v).setEnabled(true);
            }
        }

        LinearLayout emails_layout = findViewById(R.id.emails_layout);
        for (int i = 0; i < emails_layout.getChildCount(); ++i) {
            View lv = emails_layout.getChildAt(i);
            if (lv instanceof LinearLayout){
                EditText et = (EditText) ((LinearLayout) lv).getChildAt(0);
                et.setEnabled(true);
            }
        }

        EditText note = findViewById(R.id.note);
        note.setInputType(InputType.TYPE_CLASS_TEXT);

        // show button which was hidden
        // TODO: set button_click_listener

        Button add_number_button = findViewById(R.id.add_number_button);
        add_number_button.setTextSize(15);
        add_number_button.setVisibility(View.VISIBLE);

        Button add_email_button = findViewById(R.id.add_email_button);
        add_email_button.setTextSize(15);
        add_email_button.setVisibility(View.VISIBLE);

        // TODO: complete button / redo button / ask user to (save | don't save | cancel)  when user go out

        // TODO: apply to device

    }

    private void deleteContact() {
        ContentResolver contactHelper = getApplicationContext().getContentResolver();
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        String[] args = new String[]{contacts.optString("id")};
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", args).build());
        try{
            contactHelper.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }
}
