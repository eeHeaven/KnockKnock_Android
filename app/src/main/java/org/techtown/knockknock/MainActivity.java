package org.techtown.knockknock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.techtown.knockknock.message.MessageFragment;
import org.techtown.knockknock.user.MypageFragment;

public class MainActivity extends AppCompatActivity{
    final String TAG = this.getClass().getSimpleName();

    LinearLayout home_ly;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigationbar);

        init(); // 객체 정의
        SettingListener(); // 객체등록

        //맨처음 시작할 탭 설정
        bottomNavigationView.setSelectedItemId(R.id.tab_home);
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
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.home_ly,new HomeFragment())
                            .commit();
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
        getSupportFragmentManager().beginTransaction().replace(R.id.home_ly,fragment).commit();
    }


}