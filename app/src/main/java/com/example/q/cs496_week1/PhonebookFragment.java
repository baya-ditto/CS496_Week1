package com.example.q.cs496_week1;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
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
import android.text.Html;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;


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

    public static boolean select_flag = false;
    private static boolean select_lock = false;

    private RecyclerView starred_recyclerView;
    private ArrayList<String> starred_names;
    private RecyclerView recyclerView;
    private ArrayList<String> names;

    private ArrayList<String> selected_cid_list = new ArrayList<>();
    private Menu my_menu = null;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }
    @Override
    public void onResume(){
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#808080\">" + "전화번호부" + "</font>"));
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        ArrayAdapter<String> adapter;
        select_lock = false;
        select_flag = false; // 제외해도 됨.
        selected_cid_list.clear();

        View view = inflater.inflate(R.layout.activity_phonebook, container, false);

        starred_recyclerView = (RecyclerView) view.findViewById(R.id.phonebook_starred_list);
        starred_names = MainActivity.contactList.getStarredNameArray(true);

        List<RecyclerItem> mList = new ArrayList<RecyclerItem>();

        for (String name : starred_names){
            RecyclerItem item = new RecyclerItem(name, -1, true, false);
//            item.setName(name);
            //item.setImage(R.drawable.default_pic);
            mList.add(item);
        }

        starred_recyclerView.setAdapter(new RecyclerAdapter(mList, R.layout.contact_view, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (select_flag) {
                    // not implemented
                }
                else {
                    Intent intent = new Intent(getActivity(), PhonebookDetailActivity.class);
                    intent.putExtra("index", position);
                    intent.putExtra(list_tag, starredListNum);
                    startActivity(intent);
                }

            }
        }));
        starred_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        starred_recyclerView.setItemAnimator(new DefaultItemAnimator());


        recyclerView = (RecyclerView) view.findViewById(R.id.phonebook_list);
        names = MainActivity.contactList.getNameArray();

        mList = new ArrayList<RecyclerItem>();

        for (String name : names){
            RecyclerItem item = new RecyclerItem(name, -1, false, false);
//            item.setName(name);
            //item.setImage(R.drawable.default_pic);
            mList.add(item);
        }

        recyclerView.setAdapter(new RecyclerAdapter(mList, R.layout.contact_view, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (select_flag) {
                    CheckBox check_box = (CheckBox) v.findViewById(R.id.contact_select);
                    check_box.setChecked(!check_box.isChecked());
                }
                else {
                    Intent intent = new Intent(getActivity(), PhonebookDetailActivity.class);
                    intent.putExtra("index", position);
                    intent.putExtra(list_tag, normalListNum);
                    startActivity(intent);
                }
            }
        }));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PhonebookAddActivity.class);
                startActivity(intent);
            }
        });

        Button fold_button = view.findViewById(R.id.fold_starred);
        fold_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (starred_recyclerView.getVisibility() == View.GONE){
                    starred_recyclerView.setVisibility(View.VISIBLE);
                    v.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                }
                else {
                    starred_recyclerView.setVisibility(View.GONE);
                    v.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }
            }
        });

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item = menu.add(Menu.NONE, R.id.select_item, 50, "선택");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item.setTitle(Html.fromHtml("<font color=\"#808080\">" + "선택" + "</font>"));

        item = menu.add(Menu.NONE, R.id.cancel_item, 40, "취소");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item.setVisible(false);
        item.setTitle(Html.fromHtml("<font color=\"#808080\">" + "취소" + "</font>"));
        this.my_menu = menu;
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        final int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        final int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

        switch(item.getItemId()) {
            case R.id.select_item:
                if (select_lock)
                    return false;
                select_lock = true;

                if (select_flag == false) {
                    // select mode 진입
                    select_flag = true;
                    // get cancel item from parent menu
                    MenuItem cancel_item = (MenuItem) (this.my_menu).findItem(R.id.cancel_item);
                    cancel_item.setVisible(true);

                    item.setTitle(Html.fromHtml("<font color=\"#808080\">" + "삭제" + "</font>"));
                    selected_cid_list.clear();
                    for (int i = firstVisibleItemPosition; i <= recyclerView.getChildCount() && i <= lastVisibleItemPosition; ++i) {
                        RecyclerAdapter.ViewHolder holder = (RecyclerAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                        holder.select_box.setVisibility(View.VISIBLE);
                        holder.select_box.setChecked(false);
                    }
                }
                else {
                    //  selected contacts 삭제
                    select_flag = false;
                    
                    for (int i = 0; i < recyclerView.getChildCount(); ++i) {
                        RecyclerAdapter.ViewHolder holder = (RecyclerAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                        if(holder.select_box.isChecked()) {
                            selected_cid_list.add(MainActivity.contactList.getJSONObjectByIndex(i).optString("contactid"));
                        }
                        //holder.select_box.setVisibility(View.GONE);
                    }

                    deleteContacts(selected_cid_list);
                    /*item.setTitle("선택");
                    MenuItem cancel_item = (MenuItem) (this.my_menu).findItem(R.id.cancel_item);
                    cancel_item.setVisible(false);*/
                    selected_cid_list.clear();
                    select_lock = false;
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
                select_lock = false;
                return true;
            case R.id.cancel_item:
                select_flag = false;
                for (int i = firstVisibleItemPosition; i <= recyclerView.getChildCount() && i <= lastVisibleItemPosition; ++i) {
                    RecyclerAdapter.ViewHolder holder = (RecyclerAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                    holder.select_box.setVisibility(View.GONE);
                }
                item.setVisible(false);
                this.my_menu.findItem(R.id.select_item).setTitle(Html.fromHtml("<font color=\"#808080\">" + "선택" + "</font>"));
                selected_cid_list.clear();
                select_lock = false;
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void deleteContacts(ArrayList<String> contact_ids) {
        ContentResolver contactHelper = getActivity().getContentResolver();
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        for (String contact_id :contact_ids) {
            String[] args = new String[]{contact_id};
            ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                    .withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", args).build());
        }
        try{
            contactHelper.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }
}
