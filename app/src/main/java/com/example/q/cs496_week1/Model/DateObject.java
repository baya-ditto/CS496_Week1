package com.example.q.cs496_week1.Model;

import com.example.q.cs496_week1.Model.LocationObject;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DateObject extends RealmObject {

    private long date;

    private RealmList<LocationObject> locations = new RealmList<>();

    public void addLocation (LocationObject location) {
        locations.add(location);
    }

    public RealmList<LocationObject> getLocations() {
        return locations;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
