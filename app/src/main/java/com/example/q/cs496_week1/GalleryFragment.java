package com.example.q.cs496_week1;

import android.Manifest;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.nio.file.Path;
import java.util.ArrayList;


public class GalleryFragment extends Fragment {

    private ArrayList<String> images;

    private int lastIndex;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        final View rootView = inflater.inflate(R.layout.activity_galley, container, false);

        final GridView gallery = (GridView) rootView.findViewById(R.id.galleryGridView);
        if(MainActivity.hasPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}))
            gallery.setAdapter(new ImageAdapter(getActivity()));

        lastIndex = -1;

        final ImageView image = (ImageView)rootView.findViewById(R.id.gallery_detail);

        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(lastIndex == i) {
                    image.setVisibility(View.INVISIBLE);
                    ViewGroup.LayoutParams params = image.getLayoutParams();
                    params.height = 0;
                    image.setLayoutParams(params);
                    lastIndex = -1;
                }
                else {
                    image.setVisibility(View.VISIBLE);
                    ViewGroup.LayoutParams params = image.getLayoutParams();
                    params.height = 800;
                    image.setLayoutParams(params);
                    Glide.with(getActivity()).load(images.get(i)).into(image);
                    gallery.smoothScrollToPositionFromTop(i,0);
                    lastIndex = i;
                }
            }
        });

        image.setOnTouchListener(new OnSwipeTouchListener(getActivity().getApplicationContext()){
            @Override
            public void onSwipeRight() {
                if(lastIndex == 0) return;
                gallery.performItemClick(rootView,lastIndex-1, 0);
            }
            @Override
            public void onSwipeLeft() {
                if(lastIndex >= images.size()) return;
                gallery.performItemClick(rootView,lastIndex+1, 0);
            }
        });

        return rootView;
    }

    private class ImageAdapter extends BaseAdapter {
        private Activity context;
        public ImageAdapter(Activity localContext){
            context = localContext;
            images = getAllShowImagePath(context);
        }

        public int getCount() {
            return images.size();
        }

        public Object getItem(int position){
            return position;
        }

        public long getItemId(int position){
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ImageView picturesView;
            if(convertView == null) {
                picturesView = new ImageView(context);
                picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                picturesView.setAdjustViewBounds(true);
//                picturesView.setLayoutParams(new GridView.LayoutParams(340, 200));
            } else {
                picturesView = (ImageView) convertView;
            }
            Glide.with(context).load(images.get(position))
                    .placeholder(R.drawable.ic_add).centerCrop()
                    .into(picturesView);
            return picturesView;
        }
        private ArrayList<String> getAllShowImagePath(Activity activity) {
            Uri uri;
            Cursor cursor;
            int column_index_data, column_index_folder_name;
            ArrayList<String> listOfAllImages = new ArrayList<String>();
            String absolutePathOfImage = null;
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = { MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

            cursor = activity.getContentResolver().query(uri, projection, null,
                    null, null);

            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);

                listOfAllImages.add(absolutePathOfImage);
            }

            return listOfAllImages;
        }

    }

    @Override
    public void onDestroyView() {
//        if (rootView != null)
//        {
//            if (MainActivity.gallery_storage == null)
//                MainActivity.gallery_storage = new gallery_package();
//
////            Log.d("test", "gallery_storage.input = " + MainActivity.gallery_storage.input);
//        }
        super.onDestroyView();
//        Log.d("test", "onDestroyView finished");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        MainActivity.gallery_storage = null;
//        Log.d("test", "onDestroy finished");
    }
}
