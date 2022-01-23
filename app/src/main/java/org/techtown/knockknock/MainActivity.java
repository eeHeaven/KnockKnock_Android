package org.techtown.knockknock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.techtown.knockknock.post.UserPostActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView loginInfo = (TextView)findViewById(R.id.loginResult);
        button = (Button) findViewById(R.id.btn_viewUserPost);

        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo",MODE_PRIVATE);
        String nickname = sharedPreferences.getString("nickname","");
        String id = sharedPreferences.getString("userId","");
        loginInfo.setText(nickname+" 님, 환영합니다!");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //userpostactivity로 넘어가기
                Intent intent = new Intent(getApplicationContext(), UserPostActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}