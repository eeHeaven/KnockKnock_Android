package org.techtown.knockknock.post.postdetail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.techtown.knockknock.ErrorBody;
import org.techtown.knockknock.HomeFragment;
import org.techtown.knockknock.MainActivity;
import org.techtown.knockknock.R;
import org.techtown.knockknock.RetrofitClient;
import org.techtown.knockknock.initial.MemberBasicInfo;
import org.techtown.knockknock.post.CommentData;
import org.techtown.knockknock.post.PostAPI;
import org.techtown.knockknock.post.UserPostFragment;
import org.techtown.knockknock.user.MemberAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostdetailFragment extends Fragment {

    static int point;
    static Long postid;

    private Context context;
    private Fragment fragment;

    TextView title;
    TextView writer;
    TextView timestamp;
    TextView content;
    List<CommentData> comments;
    List<String> posthashtags;
    Button delete_bt;

    EditText commentRegister;
    Button savecomment;

    RecyclerView commentrecyclerView;
    RecyclerView hashtagrecyclerView;

    CommentRecyclerAdapter recyclerAdapter;
    HashTagRecyclerAdapter hashTagRecyclerAdapter;

    SharedPreferences preferences;

    public static PostdetailFragment newInstance(Long postid) {

        Bundle args = new Bundle();
        args.putLong("postId",postid);
        PostdetailFragment fragment = new PostdetailFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.activity_postdetail,null);
        context = container.getContext();
        fragment = this;


        comments = new ArrayList<>();
        posthashtags = new ArrayList<>();

        //comment recyclerview 관련 설정
        commentrecyclerView = mView.findViewById(R.id.recyclerView_postdetail);
        delete_bt = mView.findViewById(R.id.btn_deletepost);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        commentrecyclerView.setLayoutManager(layoutManager);

        //hashtag recyclerview 관련 설정
        hashtagrecyclerView = mView.findViewById(R.id.recyclerView_postdetail_hashtag);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false); // 가로 정렬
        hashtagrecyclerView.setLayoutManager(layoutManager1);




        //현재 로그인되어있는 user의 id 갖고 오기
        preferences = this.getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String id = preferences.getString("userId","");
        point =preferences.getInt("point",0);
        // 선택한 post 의 id 값 갖고 오기
        if(getArguments()!=null){
            postid = getArguments().getLong("postId");
        }
        // post 의 id 값으로 post 상세 내용 조회
        PostAPI postAPI = RetrofitClient.getInstance().create(PostAPI.class);
        Call<PostDetailData> call = postAPI.getPostDatabyPostId(postid);
        call.enqueue(new Callback<PostDetailData>() {
            @Override
            public void onResponse(Call<PostDetailData> call, Response<PostDetailData> response) {
                if(response.isSuccessful()){
                    Log.d("PostdetailFragment","연결성공");//조회 성공시
                    PostDetailData postdata = response.body();

                    //출력할 내용 설정
                    title = mView.findViewById(R.id.tv_postdetail_title);
                    writer = mView.findViewById(R.id.tv_postdetail_writer);
                    timestamp = mView.findViewById(R.id.tv_postdetail_timestamp);
                    content = mView.findViewById(R.id.tv_postdetail_content);

                    title.setText(postdata.getPostTitle());
                    content.setText(postdata.getPostContent());
                    writer.setText(postdata.getPostwriternickname());
                    timestamp.setText(postdata.getPostedTime());
                    comments = postdata.comments;
                    posthashtags = postdata.posthashtag;

                    MemberAPI memberAPI = RetrofitClient.getInstance().create(MemberAPI.class);

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

                                            //삭제로 인한 point 감소
                                            Call<MemberBasicInfo> pointcall = memberAPI.sharePoint(id,-5);
                                            pointcall.enqueue(new Callback<MemberBasicInfo>() {
                                                @Override
                                                public void onResponse(Call<MemberBasicInfo> call, Response<MemberBasicInfo> response) {
                                                    if(response.isSuccessful()){ //포인트 감소 data가 성공적으로 update 됨
                                                        //sharePreference도 변경

                                                    }
                                                    else {
                                                        ErrorBody error = new Gson().fromJson(response.errorBody().charStream(),ErrorBody.class);
                                                        Log.d("PostdetailActivity",error.getMessage());
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<MemberBasicInfo> call, Throwable t) {
                                                    Log.d("PostdetailActivity","포인트 수정 연결 실패");
                                                }
                                            });

                                            Toast.makeText(context,"게시글이 삭제되었습니다",Toast.LENGTH_LONG).show();
                                            ((MainActivity)getActivity()).replaceFragment(HomeFragment.newInstance());
                                        }
                                        else{
                                            Toast.makeText(context,"게시글 삭제에 실패했습니다",Toast.LENGTH_LONG).show();
                                            ErrorBody error = new Gson().fromJson(response.errorBody().charStream(),ErrorBody.class);
                                            Log.d("PostdetailActivity",error.getMessage());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(context,"게시글 삭제에 실패했습니다.",Toast.LENGTH_LONG).show();
                                        Log.d("PostdetailActivity","게시글 삭제 연결 실패");
                                    }
                                });

                            }
                        });
                    }
                    else Toast.makeText(context, "id :"+id+" writerId :"+postdata.getPostwriterId(), Toast.LENGTH_SHORT).show();

                    // 해시태그 recyclerView 설정
                    hashTagRecyclerAdapter = new HashTagRecyclerAdapter(getActivity(),posthashtags,(PostdetailFragment)fragment);
                    hashtagrecyclerView.setAdapter(hashTagRecyclerAdapter);

                    // 댓글 recyclerview 를 위한 설정
                    recyclerAdapter = new CommentRecyclerAdapter(getActivity(),comments,(PostdetailFragment)fragment);
                    commentrecyclerView.setAdapter(recyclerAdapter);

                    commentRegister = mView.findViewById(R.id.edit_save_comment);

                    //댓글 작성 버튼이 눌렸을 때
                    savecomment = mView.findViewById(R.id.btn_save_comment);
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
                                        CommentData savedComment = response.body();
                                        comments.add(savedComment);
                                        recyclerAdapter.notifyItemInserted(comments.indexOf(savedComment));
                                        commentrecyclerView.invalidate();
                                        Toast.makeText(context,"댓글 작성이 완료되었습니다.",Toast.LENGTH_LONG).show();
                                    }
                                        else{
                                            Toast.makeText(context,"댓글 작성이 실패했습니다.",Toast.LENGTH_LONG).show();
                                            ErrorBody error = new Gson().fromJson(response.errorBody().charStream(),ErrorBody.class);
                                            Log.d("PostdetailActivity",error.getMessage());
                                        }
                                }

                                @Override
                                public void onFailure(Call<CommentData> call, Throwable t) {
                                    Toast.makeText(context,"댓글 작성이 실패했습니다.",Toast.LENGTH_LONG).show();
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

    return mView;
    }
    public void refresh(){
        getFragmentManager().beginTransaction().detach(newInstance(postid)).attach(newInstance(postid)).commit();
    }

}