package com.example.q.cs496_week1.Model;

import io.realm.RealmList;
import io.realm.RealmObject;

public class DateObject extends RealmObject {

    private long date;

    private double distance_of_day = 0;
    private boolean is_first = true;
    private double prev_latitude, prev_longitude;

    private static String meter = "meter";
    private static String kilometer = "kilometer";

    private RealmList<LocationObject> locations = new RealmList<>();



    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    public void addLocation (LocationObject location) {
        locations.add(location);
        if (is_first) {
            distance_of_day = 0;
            prev_latitude = location.getLatitude();
            prev_longitude = location.getLongitude();
            is_first = false;
        } else {
            double current_latitude = location.getLatitude();
            double current_longitude = location.getLongitude();
            distance_of_day += distance(prev_latitude, prev_longitude, current_latitude, current_longitude, kilometer);
            prev_latitude = current_latitude;
            prev_longitude = current_longitude;
        }
    }

    /**
     * 두 지점간의 거리 계산
     *
     * @param lat1 지점 1 위도
     * @param lon1 지점 1 경도
     * @param lat2 지점 2 위도
     * @param lon2 지점 2 경도
     * @param unit 거리 표출단위
     * @return
     */
    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if(unit == "meter"){
            dist = dist * 1609.344;
        }

        return (dist);
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

    public double getDistance_of_day() {
        return distance_of_day;
    }
}
