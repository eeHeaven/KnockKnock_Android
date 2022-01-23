package org.techtown.knockknock.post;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.techtown.knockknock.ErrorBody;
import org.techtown.knockknock.R;
import org.techtown.knockknock.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPostActivity extends AppCompatActivity {

    TextView writerInfo;

    // 우선 데이터를 담을 list를 하나 만든다.
    PostListData postlist;
    List<PostData> postInfo;

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post);

        postInfo = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);

        //layoutManager: recyclerview에 listview 객체를 하나씩 띄움
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //sharedPreferences 에서 현재 로그인 되어있는 유저의 id가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo",MODE_PRIVATE);
        String id = sharedPreferences.getString("userId","");
        String nickname = sharedPreferences.getString("nickname","");

        writerInfo = findViewById(R.id.tv_writerInfo);
        writerInfo.setText(nickname+" 님이 작성하신 글 입니다.");

        PostAPI postAPI = RetrofitClient.getInstance().create(PostAPI.class);
        Call<PostListData> call = postAPI.getPostListDatabyUserId(id);
        call.enqueue(new Callback<PostListData>() {
            @Override
            public void onResponse(Call<PostListData> call, Response<PostListData> response) {
                if(response.isSuccessful()){
                postlist = response.body();
                Log.d("UserPostActivity",postlist.toString());
                postInfo = postlist.data;

                //Adapter를 이용해서 postInfo에 있는 내용을 가져와서 저장해둔 listView 형식에 맞게 띄움
                recyclerAdapter = new RecyclerAdapter(getApplicationContext(),postInfo);
                recyclerView.setAdapter(recyclerAdapter);
                }

                else{
                    ErrorBody errorBody = new Gson().fromJson(response.errorBody().charStream(),ErrorBody.class);
                    Log.d("UserPostActivity",errorBody.getMessage());
                }
            }

            @Override
            public void onFailure(Call<PostListData> call, Throwable t) {
                    Log.d("UserPostActivity",t.toString());
            }
        });



    }
}