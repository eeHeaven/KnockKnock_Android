package org.techtown.knockknock.location;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.techtown.knockknock.firebase.FirebaseUser;

public class GPS extends Service implements LocationListener {
    private DatabaseReference mDatabase;// 실시간 위치정보 업데이트용 DB

    private final Context mContext;
    private String userid;
    int count = 0;
    boolean isGPSEnable = false;

    boolean isNetWorkEnable = false;

    boolean isGetLocation = false;

    Location location;
    double lat; //위도 
    double lon; //경도

    private static final long MIN_DISTANCE_UPDATE = 0; //100미터 거리 변화가 있을시 자동 update
    private static final long MIN_TIME_UPDATE = 1000*1*1; //10초 후 (1000 = 1초)

    protected LocationManager locationManager;


    public GPS(Context mContext, String id){
        this.mContext = mContext;
        this.userid = id;
        mDatabase  = FirebaseDatabase.getInstance().getReference();
        getLocation();

    }

    @SuppressLint("MissingPermission")
    public Location getLocation(){
        try{
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetWorkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            if (!isGPSEnable && !isNetWorkEnable) {
            } else {
                this.isGetLocation = true;
                if (isNetWorkEnable) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_UPDATE, MIN_DISTANCE_UPDATE, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            Log.d("UPDATEGPS","위치정보 업데이트");
                        }
                    }
                }
                if (isGPSEnable) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_UPDATE, MIN_DISTANCE_UPDATE, this);
                    if (location == null) {

                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                Log.d("UPDATEGPS","위치정보 업데이트");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    private void updateUserLocation(double latitude, double longitude) {
        UserLocation data = new UserLocation(userid,latitude,longitude,count);

        mDatabase.child("userlocation").child(userid).setValue(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        Log.d("Firebase","UserLoctaion update success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Log.d("Firebase","UserLoctaion update failed");
                    }
                });

    }


    public double getLatitude() {
        if (location != null)
            lat = location.getLatitude();
        return lat;
    }

    public double getLongitude() {
        if (location != null)
            lon = location.getLongitude();
        return lon;
    }

    public boolean isGetLocation() {
        return this.isGetLocation;
    }

    public void stopUsingGPS() {
        if (locationManager != null)
            locationManager.removeUpdates(GPS.this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

        lat = location.getLatitude();
        lon = location.getLongitude();
        Log.i("MyLocTest", "onLocationChanged() 호출되었습니다.");
        Log.i("MyLocTest","내 위치는 Latitude :" + lat + " Longtitude : " + lon);
        updateUserLocation(lat,lon);
        count++;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
//        Toast toast = Toast.makeText(mContext, "2", Toast.LENGTH_SHORT);
//        toast.show();
    }

    @Override
    public void onProviderEnabled(String provider) {
//        Toast toast = Toast.makeText(mContext, "3", Toast.LENGTH_LONG);
//        toast.show();
    }

    @Override
    public void onProviderDisabled(String provider) {
//        Toast toast = Toast.makeText(mContext, "4", Toast.LENGTH_LONG);
//        toast.show();
    }
}
