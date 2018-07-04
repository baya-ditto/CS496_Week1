package com.example.q.cs496_week1;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

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
}
