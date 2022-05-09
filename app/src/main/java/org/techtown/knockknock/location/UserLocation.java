package org.techtown.knockknock.location;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserLocation {

    private String id;
    private double latitude;
    private double longitude;
    private String geohash;

    protected UserLocation(){}

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

    public UserLocation(String id, Double latitude, Double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.geohash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(latitude, longitude));
    }
}
