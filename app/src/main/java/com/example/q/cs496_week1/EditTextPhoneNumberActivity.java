package com.example.q.cs496_week1;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditTextPhoneNumberActivity extends Activity implements View.OnClickListener{

    private EditText mPhoneNumber;

    private Button mBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.number_info_edit);

        mPhoneNumber = (EditText)findViewById(R.id.number);
        mBtn = (Button)findViewById(R.id.delete_number);

        mPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        // mPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher("KR")); // API Level 21. Country Code

        mBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.delete_number :
                String tmp = mPhoneNumber.getText().toString();
        }

    }
}
