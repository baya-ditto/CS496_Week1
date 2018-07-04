package com.example.q.cs496_week1;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PhonebookFragment extends Fragment {

    String tag = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.activity_phonebook, container, false);

        ListView listView = (ListView) view.findViewById(R.id.phonebook_list);

        ArrayAdapter<String> adapter;

        ArrayList<String> names = MainActivity.contactList.getNameArray();

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_activated_1, names);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), PhonebookDetailActivity.class);
                intent.putExtra("index", i);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Toast.makeText(getActivity(),"ADD",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), PhonebookEditActivity.class);
                intent.putExtra(PhonebookEditActivity.ModeMsg, PhonebookEditActivity.CREATE_MODE);
                startActivity(intent);
            }
        });

        return view;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        MenuItem item = menu.add(Menu.NONE, R.id.add_item, 10, R.string.add_item);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item.setIcon(R.drawable.ic_add);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case R.id.add_item:
                Toast.makeText(getActivity().getApplicationContext(),"ADD!!", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
//    private void openDetail(final Fragment fragment, int count){
//        Bundle bundle = new Bundle();
//        bundle.putInt("count",count);
//        fragment.setArguments(bundle);
//        FragmentManager fm = getFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.replace(R.id.container, fragment);
//        ft.addToBackStack(null);
//        ft.commit();
//    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

    }

}
