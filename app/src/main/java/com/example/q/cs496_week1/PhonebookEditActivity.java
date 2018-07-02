package com.example.q.cs496_week1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

public class PhonebookEditActivity extends AppCompatActivity {

    public static final String ModeMsg = "mode";
    public static final int EDIT_MODE = 1;
    public static final int CREATE_MODE = 0;
    //static final int PICK_CONTACT_REQUEST = 1;

    private int index;
    private JSONObject contacts = new JSONObject();
    private String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonebook_edit);

        contacts = new JSONObject();
        index = getIntent().getIntExtra("index",0);

        try {
            contacts = (JSONObject)MainActivity.json_db.get(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (getIntent().getIntExtra(ModeMsg,-1) == EDIT_MODE)
            show_prev_data();

        Button add_number_but = findViewById(R.id.add_number_button);
        Button add_email_but = findViewById(R.id.add_email_button);

        add_number_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                LinearLayout numbers_layout = findViewById(R.id.numbers_layout);
                EditText text_view = new EditText(getApplicationContext());

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(10,0,0,0);

                text_view.setLayoutParams(params);
                text_view.setSingleLine(true);
                text_view.setTextColor(getResources().getColor(R.color.basic));
                text_view.setInputType(InputType.TYPE_CLASS_PHONE);

                numbers_layout.addView(text_view);
            }
        });

        add_email_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                LinearLayout emails_layout = findViewById(R.id.emails_layout);

                LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout info_layout = (LinearLayout) vi.inflate(R.layout.email_info_edit, null);

                Button type = info_layout.findViewById(R.id.type);
                type.setText("개인");
                emails_layout.addView(info_layout);
            }
        });
    }

    private void show_prev_data() {
        EditText name_text = findViewById(R.id.name);
        name = contacts.optString("name");
        name_text.setText(name);

        // show phone numbers
        LinearLayout numbers_layout = (LinearLayout) findViewById(R.id.numbers_layout);
        JSONArray numbers = contacts.optJSONArray("phone_number");
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
            text_view.setTextColor(getResources().getColor(R.color.basic));
            text_view.setInputType(InputType.TYPE_CLASS_PHONE);
            //text_view.setInputType(InputType.TYPE_NULL);


            /*params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);
            params.setMargins(0,10,0,10);
            View line_view = new View(this);
            line_view.setLayoutParams(params);
            line_view.setBackgroundResource(R.color.white_gray);*/

            numbers_layout.addView(text_view);
            //numbers_layout.addView(line_view);
        }

        // show email infos
        LinearLayout emails_layout = findViewById(R.id.emails_layout);
        JSONArray email_infos = contacts.optJSONArray("email_info");

        for (int i = 0; i < email_infos.length(); ++i)
        {
            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout info_layout = (LinearLayout) vi.inflate(R.layout.email_info_edit, null);

            JSONObject email_info = email_infos.optJSONObject(i);

            String email = email_info.optString("email");
            EditText email_text_view = info_layout.findViewById(R.id.email);
            email_text_view.setText(email);

            String type = email_info.optString("type");
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

            /*
            type_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PhonebookEditActivity.this, null);
                    intent.putExtra(TypeMsg, ((Button)v).getText());
                    startActivityForResult(intent, PICK_CONTACT_REQUEST);
                }
            });
            */

            /*LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);
            params.setMargins(0,10,0,10);
            View line_view = new View(this);
            line_view.setLayoutParams(params);
            line_view.setBackgroundResource(R.color.white_gray);*/

            emails_layout.addView(info_layout);
            //emails_layout.addView(line_view);
        }

        // show note info
        String note = contacts.optString("notes");
        EditText note_text_view = findViewById(R.id.note);
        note_text_view.setText(note);
    }
}
