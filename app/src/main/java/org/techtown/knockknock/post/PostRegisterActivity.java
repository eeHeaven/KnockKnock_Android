package org.techtown.knockknock.post;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.techtown.knockknock.ErrorBody;
import org.techtown.knockknock.MainActivity;
import org.techtown.knockknock.R;
import org.techtown.knockknock.RetrofitClient;
import org.techtown.knockknock.initial.MemberBasicInfo;
import org.techtown.knockknock.post.postdetail.PostDetailData;
import org.techtown.knockknock.user.MemberAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostRegisterActivity extends AppCompatActivity {
    static int point;

    EditText title;
    EditText hashtag;
    EditText content;
    Button writePost;
    ImageView img;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_register);

        title = findViewById(R.id.tv_postRegister_title);
        hashtag = findViewById(R.id.tv_postRegister_hashtag);
        content = findViewById(R.id.tv_postRegister_content);
        writePost = findViewById(R.id.reg_button);
        img = findViewById(R.id.img_postregister);

        img.setColorFilter(Color.parseColor("#FF9F1C"));

        writePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                preferences = getSharedPreferences("UserInfo",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                // TextView에서 작성 내용 가져오기
                String posttitle = title.getText().toString();
                String posthashtag = hashtag.getText().toString();
                String postcontent = content.getText().toString();

                PostSaveRequest request = new PostSaveRequest(posttitle,posthashtag,postcontent);

                SharedPreferences sharedPreferences = getSharedPreferences("UserInfo",MODE_PRIVATE);
                String id = sharedPreferences.getString("userId","");

                PostAPI postAPI = RetrofitClient.getInstance().create(PostAPI.class);
                MemberAPI memberAPI = RetrofitClient.getInstance().create(MemberAPI.class);

                Call<PostDetailData> call = postAPI.writePost(id,request);
                call.enqueue(new Callback<PostDetailData>() {
                    @Override
                    public void onResponse(Call<PostDetailData> call, Response<PostDetailData> response1) {
                        if(response1.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"게시글 작성이 완료되었습니다.",Toast.LENGTH_LONG).show();

                            // 게시글 작성시 sharePoint +5점 증가
                            Call<MemberBasicInfo> postsaveCall = memberAPI.sharePoint(id, 5);
                            postsaveCall.enqueue(new Callback<MemberBasicInfo>() {
                                @Override
                                public void onResponse(Call<MemberBasicInfo> call, Response<MemberBasicInfo> response2) {
                                    if(response2.isSuccessful()){

                                    }
                                    else{
                                        ErrorBody error = new Gson().fromJson(response2.errorBody().charStream(),ErrorBody.class);
                                        Log.d("PostRegisterActivity",error.getMessage());
                                    }
                                }

                                @Override
                                public void onFailure(Call<MemberBasicInfo> call, Throwable t) {
                                    Log.d("PostRegisterActivity",t.getMessage());
                                }
                            });


                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();

                        }
                        else{
                            ErrorBody error = new Gson().fromJson(response1.errorBody().charStream(),ErrorBody.class);
                            Log.d("PostRegisterActivity",error.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<PostDetailData> call, Throwable t) {
                        Log.d("PostRegisterActivity",t.getMessage());
                    }
                });

            }
        });


    }
}