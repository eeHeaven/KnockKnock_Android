package org.techtown.knockknock.post;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.techtown.knockknock.ErrorBody;
import org.techtown.knockknock.MainActivity;
import org.techtown.knockknock.R;
import org.techtown.knockknock.RetrofitClient;
import org.techtown.knockknock.user.RegisterActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostdetailActivity extends AppCompatActivity {

    TextView title;
    TextView writer;
    TextView timestamp;
    TextView content;
    List<CommentData> comments;
    Button delete_bt;

    EditText commentRegister;
    Button savecomment;

    RecyclerView recyclerView;
    CommentRecyclerAdapter recyclerAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postdetail);

        comments = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView_postdetail);
        delete_bt = findViewById(R.id.btn_deletepost);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //현재 로그인되어있는 user의 id 갖고 오기
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo",MODE_PRIVATE);
        String id = sharedPreferences.getString("userId","");
        // 선택한 post 의 id 값 갖고 오기
        Intent intent = getIntent();
        Long postid = intent.getExtras().getLong("postid");
        // post 의 id 값으로 post 상세 내용 조회
        PostAPI postAPI = RetrofitClient.getInstance().create(PostAPI.class);
        Call<PostDetailData> call = postAPI.getPostDatabyPostId(postid);
        call.enqueue(new Callback<PostDetailData>() {
            @Override
            public void onResponse(Call<PostDetailData> call, Response<PostDetailData> response) {
                if(response.isSuccessful()){ //조회 성공시

                    PostDetailData postdata = response.body();

                    //출력할 내용 설정
                    title = findViewById(R.id.tv_postdetail_title);
                    writer = findViewById(R.id.tv_postdetail_writer);
                    timestamp = findViewById(R.id.tv_postdetail_timestamp);
                    content = findViewById(R.id.tv_postdetail_content);

                    title.setText(postdata.getPostTitle());
                    content.setText(postdata.getPostContent());
                    writer.setText(postdata.getPostwriternickname());
                    timestamp.setText(postdata.getPostedTime());
                    comments = postdata.comments;

                    // 자신이 작성한 글일 때 삭제 기능 활성화
                    if(id.equals(postdata.getPostwriterId())){
                        delete_bt.setVisibility(View.VISIBLE);
                        //Toast.makeText(getApplicationContext(), "삭제 권한이 있습니다", Toast.LENGTH_SHORT).show();
                        delete_bt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PostAPI postAPI = RetrofitClient.getInstance().create(PostAPI.class);
                                Call<Void> call = postAPI.deletePost(postid); // 삭제하기
                                call.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if(response.isSuccessful()){// 삭제가 성공적으로 이루어졌다면
                                            Toast.makeText(getApplicationContext(),"게시글이 삭제되었습니다",Toast.LENGTH_LONG).show();
                                            Intent main = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(main);
                                            finish();
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(),"게시글 삭제에 실패했습니다",Toast.LENGTH_LONG).show();
                                            ErrorBody error = new Gson().fromJson(response.errorBody().charStream(),ErrorBody.class);
                                            Log.d("PostdetailActivity",error.getMessage());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(getApplicationContext(),"게시글 삭제에 실패했습니다.",Toast.LENGTH_LONG).show();
                                        Log.d("PostdetailActivity","게시글 삭제 연결 실패");
                                    }
                                });

                            }
                        });
                    }
                    else Toast.makeText(PostdetailActivity.this, "id :"+id+" writerId :"+postdata.getPostwriterId(), Toast.LENGTH_SHORT).show();

                    // 댓글 recyclerview 를 위한 설정
                    recyclerAdapter = new CommentRecyclerAdapter(getApplicationContext(),comments);
                    recyclerView.setAdapter(recyclerAdapter);

                    commentRegister = findViewById(R.id.edit_save_comment);

                    //댓글 작성 버튼이 눌렸을 때
                    savecomment = findViewById(R.id.btn_save_comment);
                    savecomment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String content = commentRegister.getText().toString(); // 유저가 입력한 댓글 내용 가져오기
                            Call<CommentData> call = postAPI.writeComment(id,postid,new CommentSaveRequest(content)); // 댓글 작성 api 실행
                            call.enqueue(new Callback<CommentData>() {
                                @Override
                                public void onResponse(Call<CommentData> call, Response<CommentData> response) {
                                    if(response.isSuccessful()){ // 댓글 작성이 완료되면
                                        // 댓글 recyclerView 리프래쉬해서 바로 작성자가 작성한 댓글이 보이도록 설정
                                        recyclerAdapter = new CommentRecyclerAdapter(getApplicationContext(),comments);
                                        recyclerView.setAdapter(recyclerAdapter);
                                        recyclerAdapter.notifyDataSetChanged();
                                        Toast.makeText(getApplicationContext(),"댓글 작성이 완료되었습니다.",Toast.LENGTH_LONG).show();
                                        // recyclerView 리프래쉬
                                        finish();
                                        overridePendingTransition(0, 0);
                                        startActivity(getIntent());
                                        overridePendingTransition(0, 0);}
                                        else{
                                            Toast.makeText(getApplicationContext(),"댓글 작성이 실패했습니다.",Toast.LENGTH_LONG).show();
                                            ErrorBody error = new Gson().fromJson(response.errorBody().charStream(),ErrorBody.class);
                                            Log.d("PostdetailActivity",error.getMessage());
                                        }
                                }

                                @Override
                                public void onFailure(Call<CommentData> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(),"댓글 작성이 실패했습니다.",Toast.LENGTH_LONG).show();
                                    Log.d("PostdetailActivity","댓글 작성 연결 실패");

                                }
                            });

                        }
                    });
                }
                else{
                    ErrorBody error = new Gson().fromJson(response.errorBody().charStream(),ErrorBody.class);
                    Log.d("PostdetailActivity",error.getMessage());
                }
            }

            @Override
            public void onFailure(Call<PostDetailData> call, Throwable t) {
                Log.d("PostdetailActivity","게시글 조회 연결 실패");

            }
        });


    }
    @Override
    protected void onResume() {
        super.onResume();
        
    }
}