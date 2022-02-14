package org.techtown.knockknock.post;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.techtown.knockknock.ErrorBody;
import org.techtown.knockknock.MainActivity;
import org.techtown.knockknock.R;
import org.techtown.knockknock.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostRegisterActivity extends AppCompatActivity {

    EditText title;
    EditText content;
    Button writePost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_register);

        title = findViewById(R.id.tv_postRegister_title);
        content = findViewById(R.id.tv_postRegister_content);
        writePost = findViewById(R.id.reg_button);

        writePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String posttitle = title.getText().toString();
                String postcontent = content.getText().toString();

                PostSaveRequest request = new PostSaveRequest(posttitle,postcontent);

                SharedPreferences sharedPreferences = getSharedPreferences("UserInfo",MODE_PRIVATE);
                String id = sharedPreferences.getString("userId","");

                PostAPI postAPI = RetrofitClient.getInstance().create(PostAPI.class);
                Call<PostDetailData> call = postAPI.writePost(id,request);
                call.enqueue(new Callback<PostDetailData>() {
                    @Override
                    public void onResponse(Call<PostDetailData> call, Response<PostDetailData> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"게시글 작성이 완료되었습니다.",Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();

                        }
                        else{
                            ErrorBody error = new Gson().fromJson(response.errorBody().charStream(),ErrorBody.class);
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