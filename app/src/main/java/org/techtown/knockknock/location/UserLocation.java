package org.techtown.knockknock.location;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserLocation {

    private String id;
    private double latitude;
    private double longitude;
    private int count;

    public UserLocation(){}

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public UserLocation(String id, Double latitude, Double longitude, int count) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.count = count;
    }
}
