package com.example.q.cs496_week1;

import android.content.Context;
import android.os.Environment;

import com.example.q.cs496_week1.Model.DateObject;
import com.example.q.cs496_week1.Model.LocationObject;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTimeComparator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static java.lang.Double.parseDouble;

public class Helper {

    public static final String SAVEDIRPATH = Environment.getExternalStorageDirectory() + "/gps_info/";
    public static final String SAVEFILEPATH = "gps_info.dat";

    public static File makeDirectoryAndFile(String dir_path, String file_path){
        File dir = new File(dir_path);
        if(!dir.exists()){
            dir.mkdirs();
        }
        return makeFile(dir, dir_path+file_path);
    }

    private static File makeFile(File dir, String file_path){
        File file = null;
        if(dir.isDirectory()){
            file = new File(file_path);
            if(file != null && !file.exists()){
                try{
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
            }
        }
        return file;
    }

        public static ArrayList<LatLng> getLatLngList(Date start, Date end) {

        ArrayList<LatLng> ret = new ArrayList<LatLng>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(
                    new FileReader(Helper.makeDirectoryAndFile(Helper.SAVEDIRPATH,Helper.SAVEFILEPATH)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true) {
            String line = null;
            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (line==null) break;
            String[] data = line.split("\t");
            Date date = new Date(Long.parseLong(data[0]));
            if(date.after(start) && date.before(end)) {
                ret.add(new LatLng(parseDouble(data[1]), parseDouble(data[2])));
            }
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

}
