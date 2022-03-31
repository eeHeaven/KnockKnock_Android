package org.techtown.knockknock;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.techtown.knockknock.location.GPS;
import org.techtown.knockknock.message.MessageFragment;
import org.techtown.knockknock.user.MypageFragment;

import java.io.IOException;
import java.util.List;

//하단바 띄우는 Main Activity (각종 fragment를 띄움)
//위치 정보는 이 Activity에서 가져오기
public class MainActivity extends AppCompatActivity{
    private static  ActivityResultLauncher<Intent> resultLauncher;
    private static int CALL_TYPE = 0;
    final String TAG = this.getClass().getSimpleName();
    final Geocoder geocoder = new Geocoder(this);

    FrameLayout home_ly;
    BottomNavigationView bottomNavigationView;
    private SharedPreferences preferences;
    FragmentTransaction transaction;

    //위치 정보 관련
    private GPS gps;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.toolbarmenu_search:
                //검색창으로 이동
            case android.R.id.home:{
                onBackPressed();
            }
            default: return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigationbar);

        init(); // 객체 정의
        SettingListener(); // 객체등록
        transaction = getSupportFragmentManager().beginTransaction();

        //맨처음 시작할 탭 설정
        bottomNavigationView.setSelectedItemId(R.id.tab_home);

        //Toolbar 를 AppBar로 지정하기
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.Toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기 버튼, 디폴트로 true만 해도 back 버튼이 생김

        //위치 관련
        if(checkLocationServicesStatus()){
            checkRunTimePermission();
        }else{
            showDialogForLocationServiceSetting();
        }
        SharedPreferences sharedPreferences = this.getSharedPreferences("UserInfo",MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId","");
        gps = new GPS(MainActivity.this,userId);
        Location currentlocation = gps.getLocation();
        double latitude = currentlocation.getLatitude();
        double longitude = currentlocation.getLongitude();

        //sharedPreference 에 경도와 위도값 넣기
        preferences = getSharedPreferences("Location",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Double lat = new Double(latitude);
        Double lon = new Double(longitude);
        editor.putFloat("latitude",lat.floatValue());
        editor.putFloat("longitude",lon.floatValue());
        //항상 commit & apply를 해야 저장
        editor.commit();

        //주소값 구하기 (역지오코딩)
        List<Address> list = null;
        try{
            list = geocoder.getFromLocation(latitude,longitude,10);
        }catch(IOException e){
            e.printStackTrace();
            Log.e(TAG,"Geocoder 입출력 오류");
        }
        if(list != null){
            //if(list.size() == 0) Toast.makeText(this,"현위치에 해당되는 주소 정보가 없습니다.",Toast.LENGTH_LONG).show();
           // else Toast.makeText(this,"현재 위치: "+list.get(0).getAddressLine(0),Toast.LENGTH_LONG).show();
        }
       // Toast.makeText(this, "현재위치 \n위도 " + latitude + "\n경도 " + longitude, Toast.LENGTH_LONG).show();


        //액티비티 콜백 함수
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK){
                            Intent intent = result.getData();
                            if(CALL_TYPE ==GPS_ENABLE_REQUEST_CODE){
                                //사용자가 GPS 활성 시켰는지 검사
                                if(checkLocationServicesStatus()){
                                    Log.d(TAG,"onActivityResult: GPS 활성화 되어있음");
                                    checkRunTimePermission();
                                    return;
                                }
                            }
                        }
                    }
                }
        );
    }
    private void init(){
        home_ly = findViewById(R.id.home_ly);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }
    private void SettingListener(){
        bottomNavigationView.setOnNavigationItemSelectedListener(new TabSelectedListener());
    }

    class TabSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch(item.getItemId()){
                case R.id.tab_home:{
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_ly,new HomeFragment()).commit();
                    return true;
                }
                case R.id.tab_message:{
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.home_ly,new MessageFragment())
                            .commit();
                    return true;
                }
                case R.id.tab_mypage:{
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.home_ly,new MypageFragment())
                            .commit();
                    return true;
                }
            }
            return false;
        }
    }
    public void replaceFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.home_ly,fragment).addToBackStack(null).commit();
    }

    //ActivityCompat.requestPermission을 사용한 퍼미션 요청의 결과를 리턴받는 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length) {
            boolean check_result = true;
            //모든 퍼미션을 허용했는지 체크
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            if (check_result) {
                //위치 값을 가져올 수 있음
            } else { //거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    Toast.makeText(this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(this, "퍼미션이 거부되었습니다. 설정에서 퍼미션을 허용해야 합니다.", Toast.LENGTH_LONG).show();
                }

            }
        }
    }
    //런타임 퍼미션 처리
    void checkRunTimePermission(){
        //1. 위치 퍼미션을 가지고 있는지 체크하기
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED){
            //2. 이미 퍼미션을 가지고 있다면 위치 값을 가져올 수 있음
        }else{// 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,REQUIRED_PERMISSIONS[0])){
                //3-2. 요청을 진행하기 전에 사용자에게 퍼미션이 필요한 이유를 설명
                Toast.makeText(this,"이 앱을 실행하려면 위치 접근 권한이 필요합니다.",Toast.LENGTH_LONG).show();
                //3-3. 사용자에게 퍼미션 요청
                ActivityCompat.requestPermissions(this,REQUIRED_PERMISSIONS,PERMISSIONS_REQUEST_CODE);
            }else{
                //4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우
                ActivityCompat.requestPermissions(this,REQUIRED_PERMISSIONS,PERMISSIONS_REQUEST_CODE);
            }

        }
    }

    //GPS 활성화
    private void showDialogForLocationServiceSetting(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" +"위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                CALL_TYPE = GPS_ENABLE_REQUEST_CODE;
                resultLauncher.launch(callGPSSettingIntent);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    public boolean checkLocationServicesStatus(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


}