package com.example.q.cs496_week1;

import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


public class GalleryFragment extends Fragment {

    public static final String TAG_INPUT = "input";

    private View rootView;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        rootView = inflater.inflate(R.layout.activity_galley, container, false);

        if (MainActivity.gallery_storage != null) {
            ((TextView) rootView.findViewById(R.id.input)).setText(MainActivity.gallery_storage.input);
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        if (rootView != null)
        {
            if (MainActivity.gallery_storage == null)
                MainActivity.gallery_storage = new gallery_package();

            MainActivity.gallery_storage.input = ((TextView) rootView.findViewById(R.id.input)).getText().toString();
//            Log.d("test", "gallery_storage.input = " + MainActivity.gallery_storage.input);
        }
        super.onDestroyView();
//        Log.d("test", "onDestroyView finished");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.gallery_storage = null;
//        Log.d("test", "onDestroy finished");
    }
}
