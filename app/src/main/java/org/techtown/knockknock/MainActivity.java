package org.techtown.knockknock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.techtown.knockknock.post.PostAPI;
import org.techtown.knockknock.post.PostData;
import org.techtown.knockknock.post.PostListData;
import org.techtown.knockknock.post.PostRegisterActivity;
import org.techtown.knockknock.post.RecyclerAdapter;
import org.techtown.knockknock.post.UserPostActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    PostListData postlist;
    List<PostData> postInfo;

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    Button button;
    Button btn_write;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        postInfo = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView_totalBoard);


        TextView loginInfo = (TextView)findViewById(R.id.loginResult);
        button = (Button) findViewById(R.id.btn_viewUserPost);
        btn_write = (Button)findViewById(R.id.btn_main_postRegister);

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
            }
        });

        PostAPI postAPI = RetrofitClient.getInstance().create(PostAPI.class);
        Call<PostListData> call = postAPI.getPostList();
        call.enqueue(new Callback<PostListData>() {
            @Override
            public void onResponse(Call<PostListData> call, Response<PostListData> response) {
                if(response.isSuccessful()){
                    postlist = response.body();
                    Log.d("MainActivity",postlist.toString());
                    postInfo = postlist.data;

                    recyclerAdapter = new RecyclerAdapter(getApplicationContext(),postInfo);
                    recyclerView.setAdapter(recyclerAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    recyclerAdapter.notifyDataSetChanged();

                }
                else{
                    ErrorBody error = new Gson().fromJson(response.errorBody().charStream(),ErrorBody.class);
                    Log.d("MainActivity",error.getMessage());
                }
            }

            @Override
            public void onFailure(Call<PostListData> call, Throwable t) {
                Log.d("MainActivity",t.getMessage());
            }
        });

        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PostRegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }



}