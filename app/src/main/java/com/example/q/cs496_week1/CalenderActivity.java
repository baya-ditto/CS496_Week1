package com.example.q.cs496_week1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.savvi.rangedatepicker.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalenderActivity extends AppCompatActivity {

    CalendarPickerView calendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -1);
        final Calendar nextYear = Calendar.getInstance();
        calendar.init(lastYear.getTime(), nextYear.getTime())
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDate(new Date(System.currentTimeMillis()-24*60*60*1000));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem edit = menu.add(Menu.NONE, R.id.edit_item, 10, R.string.edit_item);
        edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        edit.setIcon(R.drawable.ic_edit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent1 = new Intent(this, MainActivity.class);
                intent1.putExtra("Fragment","3");
                startActivity(intent1);
                return true;
            case R.id.edit_item:
                List<Date> dates = calendar.getSelectedDates();
                long[] pickedList = new long[dates.size()];
                for(int i=0;i<dates.size();i++){
                    pickedList[i] = dates.get(i).getTime();
                }
                Intent intent2 = new Intent(this,MapLogActivity.class);
                intent2.putExtra("dates",pickedList);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
