package org.techtown.knockknock.location;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.techtown.knockknock.ErrorBody;
import org.techtown.knockknock.HomeFragment;
import org.techtown.knockknock.MainActivity;
import org.techtown.knockknock.R;
import org.techtown.knockknock.RetrofitClient;
import org.techtown.knockknock.firebase.FirebaseUser;
import org.techtown.knockknock.post.PostAPI;
import org.techtown.knockknock.post.postlist.PostListData;
import org.techtown.knockknock.post.postlist.RecyclerAdapter;
import org.techtown.knockknock.post.postlist.UserPostFragment;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LocationChangeFragment extends Fragment implements OnMapReadyCallback{

    String id; //유저 아이디
    double latitude;
    double longitude;

    Double targetlat;
    Double targetlon;

    Button mylocation;
    Button enter;

    SupportMapFragment mapFragment;
    GoogleMap map;

    Marker myMarker;
    MarkerOptions myLocationMarker;
    Circle circle;
    CircleOptions circle1KM;

    private DatabaseReference mDatabase;

    public static LocationChangeFragment newInstance() {

        Bundle args = new Bundle();

        LocationChangeFragment fragment = new LocationChangeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_location_change,null,false);
        mylocation = (Button) mView.findViewById(R.id.btn_locationchange_myloc);
        enter = (Button) mView.findViewById(R.id.btn_locationchange_enter);

        mapFragment = (SupportMapFragment)this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //유저의 현재 위치 가져오기위한 DB와 id 세팅
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("UserInfo",MODE_PRIVATE);
        id = sharedPreferences.getString("userId","");
        mDatabase  = FirebaseDatabase.getInstance().getReference();
        mapFragment.getView().setVisibility(View.GONE);
        mapFragment.getView().setVisibility(View.VISIBLE);
        getCurrentLocation();
    //    showCurrentLocation(latitude,longitude);
        targetlat = latitude;
        targetlon = longitude;

        mylocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation(); //위치 정보 업데이트
                showCurrentLocation(latitude,longitude);
                targetlat = latitude;
                targetlon = longitude;
            }
        });
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude",targetlat);
                bundle.putDouble("longitude",targetlon);
                Log.d("LocationChangeFragment","Target Location: "+targetlat+" , "+targetlon);

                HomeFragment home= new HomeFragment();
                home.setArguments(bundle);
                ((MainActivity)getActivity()).replaceFragment(home);
            }
        });


        return mView;
    }

    private void getCurrentLocation(){
        mDatabase.child("userlocation").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if(dataSnapshot.getValue(UserLocation.class) != null){
                    UserLocation currentlocation = dataSnapshot.getValue(UserLocation.class);
                    latitude = currentlocation.getLatitude();
                    longitude = currentlocation.getLongitude();
                    Log.w("FireBaseData", "getData" + currentlocation.toString());
                } else {
                    Log.w("FireBaseData", "변경된 데이터 없음");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FireBaseData", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    private void showCurrentLocation(double latitude, double longitude) {
        LatLng curPoint = new LatLng(latitude, longitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
        showMyLocationMarker(curPoint);
    }

    private void showMyLocationMarker(LatLng curPoint) {
        if (myLocationMarker == null) {
            myLocationMarker = new MarkerOptions(); // 마커 객체 생성
            myLocationMarker.position(curPoint);
            myLocationMarker.title("최근위치 \n");
            myLocationMarker.snippet("*GPS로 확인한 최근위치");
            // myLocationMarker.icon(BitmapDescriptorFactory.fromResource((R.drawable.mylocation)));
            myMarker = map.addMarker(myLocationMarker);
        } else {
            myMarker.remove(); // 마커삭제
            myLocationMarker.position(curPoint);
            myMarker = map.addMarker(myLocationMarker);
        }

        // 반경추가
        if (circle1KM == null) {
            circle1KM = new CircleOptions().center(curPoint) // 원점
                    .radius(1000)       // 반지름 단위 : m
                    .strokeWidth(1.0f);    // 선너비 0f : 선없음
            //.fillColor(Color.parseColor("#1AFFFFFF")); // 배경색
            circle = map.addCircle(circle1KM);

        } else {
            circle.remove(); // 반경삭제
            circle1KM.center(curPoint);
            circle = map.addCircle(circle1KM);
        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        if (ActivityCompat.checkSelfPermission(this.getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: 권한 승인 요청하기
            return;
        }
        map.setMyLocationEnabled(true);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
           @Override
            public void onMapClick(LatLng point) {
               Double lat = point.latitude;
               Double lon = point.longitude;
               targetlat = lat;
               targetlon = lon;
               if (myLocationMarker == null) {
                   myLocationMarker = new MarkerOptions(); // 마커 객체 생성
                   myLocationMarker.position(point);
                   myLocationMarker.title("selected point");
                   myLocationMarker.snippet(lat.toString() + ", " + lon.toString());
                   myMarker = googleMap.addMarker(myLocationMarker);
               } else {
                   myMarker.remove(); // 마커삭제
                   myLocationMarker.position(point);
                   myMarker = googleMap.addMarker(myLocationMarker);
               }
            }
        });
    }
}