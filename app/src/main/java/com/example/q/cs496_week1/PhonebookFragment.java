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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.List;


public class PhonebookFragment extends Fragment {

    String tag = null;
    public static final String list_tag = "list_tag";
    public static final int starredListNum = 1;
    public static final int normalListNum = 0;

    private RecyclerView starred_recyclerView;
    private ArrayList<String> starred_names;
    private RecyclerView recyclerView;
    private ArrayList<String> names;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        ArrayAdapter<String> adapter;
        View view = inflater.inflate(R.layout.activity_phonebook, container, false);


        starred_recyclerView = (RecyclerView) view.findViewById(R.id.phonebook_starred_list);
        starred_names = MainActivity.contactList.getStarredNameArray(true);

        List<RecyclerItem> mList = new ArrayList<RecyclerItem>();

        for (String name : starred_names){
            RecyclerItem item = new RecyclerItem(name, -1, true);
//            item.setName(name);
            //item.setImage(R.drawable.default_pic);
            mList.add(item);
        }

        starred_recyclerView.setAdapter(new RecyclerAdapter(mList, R.layout.contact_view, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(getActivity(), PhonebookDetailActivity.class);
                intent.putExtra("index", position);
                Toast.makeText(getActivity().getApplicationContext(), "Pos : " + Integer.toString(position), Toast.LENGTH_LONG).show();
                intent.putExtra(list_tag, starredListNum);
                startActivity(intent);
            }
        }));
        starred_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        starred_recyclerView.setItemAnimator(new DefaultItemAnimator());
        /*ListView starred_listView = (ListView) view.findViewById(R.id.phonebook_starred_list);
        ArrayList<String> starredNames = MainActivity.contactList.getStarredNameArray(true);

        adapter =  new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_activated_1, starredNames);
        starred_listView.setAdapter(adapter);

        starred_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), PhonebookDetailActivity.class);
                intent.putExtra("index", i);
                intent.putExtra(list_tag, starredListNum);
                startActivity(intent);
            }
        });*/


        recyclerView = (RecyclerView) view.findViewById(R.id.phonebook_list);
        names = MainActivity.contactList.getNameArray();

        mList = new ArrayList<RecyclerItem>();

        for (String name : names){
            RecyclerItem item = new RecyclerItem(name, -1, false);
//            item.setName(name);
            //item.setImage(R.drawable.default_pic);
            mList.add(item);
        }

        recyclerView.setAdapter(new RecyclerAdapter(mList, R.layout.contact_view, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(getActivity(), PhonebookDetailActivity.class);
                intent.putExtra("index", position);
                Toast.makeText(getActivity().getApplicationContext(), "Pos : " + Integer.toString(position), Toast.LENGTH_LONG).show();
                intent.putExtra(list_tag, normalListNum);
                startActivity(intent);
            }
        }));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        /*ListView listView = (ListView) view.findViewById(R.id.phonebook_list);
        ArrayList<String> names = MainActivity.contactList.getNameArray();

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_activated_1, names);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), PhonebookDetailActivity.class);
                intent.putExtra("index", i);
                intent.putExtra(list_tag, normalListNum);
                startActivity(intent);
            }
        });*/

        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Toast.makeText(getActivity(),"ADD",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), PhonebookAddActivity.class);
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
