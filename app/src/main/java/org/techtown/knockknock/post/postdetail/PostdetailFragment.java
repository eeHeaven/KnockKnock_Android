package org.techtown.knockknock.post.postdetail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.techtown.knockknock.ErrorBody;
import org.techtown.knockknock.HomeFragment;
import org.techtown.knockknock.MainActivity;
import org.techtown.knockknock.R;
import org.techtown.knockknock.RetrofitClient;
import org.techtown.knockknock.initial.MemberBasicInfo;
import org.techtown.knockknock.post.postdetail.comment.CommentData;
import org.techtown.knockknock.post.PostAPI;
import org.techtown.knockknock.post.postdetail.comment.CommentRecyclerAdapter;
import org.techtown.knockknock.post.postdetail.comment.CommentSaveRequest;
import org.techtown.knockknock.post.postdetail.hashtag.HashTagRecyclerAdapter;
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
    ImageView image;
    TextView content;
    TextView postlocation;
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
        //????????? ?????????
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = container.getContext();
        fragment = this;
        comments = new ArrayList<>();
        posthashtags = new ArrayList<>();

        //comment recyclerview ?????? ??????
        commentrecyclerView = mView.findViewById(R.id.recyclerView_postdetail);
        delete_bt = mView.findViewById(R.id.btn_deletepost);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        commentrecyclerView.setLayoutManager(layoutManager);

        //hashtag recyclerview ?????? ??????
        hashtagrecyclerView = mView.findViewById(R.id.recyclerView_postdetail_hashtag);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false); // ?????? ??????
        hashtagrecyclerView.setLayoutManager(layoutManager1);



        //?????? ????????????????????? user??? id ?????? ??????
        preferences = this.getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String id = preferences.getString("userId","");
        point =preferences.getInt("point",0);
        // ????????? post ??? id ??? ?????? ??????
        if(getArguments()!=null){
            postid = getArguments().getLong("postId");
        }
        // post ??? id ????????? post ?????? ?????? ??????
        PostAPI postAPI = RetrofitClient.getInstance().create(PostAPI.class);
        Call<PostDetailData> call = postAPI.getPostDatabyPostId(postid);
        call.enqueue(new Callback<PostDetailData>() {
            @Override
            public void onResponse(Call<PostDetailData> call, Response<PostDetailData> response) {
                if(response.isSuccessful()){
                    Log.d("PostdetailFragment","????????????");//?????? ?????????
                    PostDetailData postdata = response.body();

                    //????????? ?????? ??????
                    title = mView.findViewById(R.id.tv_postdetail_title);
                    writer = mView.findViewById(R.id.tv_postdetail_writer);
                    timestamp = mView.findViewById(R.id.tv_postdetail_timestamp);
                    content = mView.findViewById(R.id.tv_postdetail_content);
                    image = mView.findViewById(R.id.postdetail_img);
                    postlocation = mView.findViewById(R.id.tv_postdetail_location);

                    title.setText(postdata.getPostTitle());
                    content.setText(postdata.getPostContent());
                    writer.setText(postdata.getPostwriternickname());
                    timestamp.setText(postdata.getPostedTime());
                    postlocation.setText(postdata.getLocation());

                    if(postdata.getImage()!= null) {
                        Uri uri = Uri.parse(postdata.getImage());
                        Glide.with(getActivity()).load(uri).into(image);
                        image.setVisibility(View.VISIBLE);}
                    else image.setVisibility(View.GONE);

                    comments = postdata.comments;
                    posthashtags = postdata.posthashtag;

                    MemberAPI memberAPI = RetrofitClient.getInstance().create(MemberAPI.class);

                    // ????????? ????????? ?????? ??? ?????? ?????? ?????????
                    if(id.equals(postdata.getPostwriterId())){
                        delete_bt.setVisibility(View.VISIBLE);
                        //Toast.makeText(getApplicationContext(), "?????? ????????? ????????????", Toast.LENGTH_SHORT).show();
                        delete_bt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PostAPI postAPI = RetrofitClient.getInstance().create(PostAPI.class);
                                Call<Void> call = postAPI.deletePost(postid); // ????????????
                                call.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if(response.isSuccessful()){// ????????? ??????????????? ??????????????????

                                            //????????? ?????? point ??????
                                            Call<MemberBasicInfo> pointcall = memberAPI.sharePoint(id,-5);
                                            pointcall.enqueue(new Callback<MemberBasicInfo>() {
                                                @Override
                                                public void onResponse(Call<MemberBasicInfo> call, Response<MemberBasicInfo> response) {
                                                    if(response.isSuccessful()){ //????????? ?????? data??? ??????????????? update ???
                                                        //sharePreference??? ??????

                                                    }
                                                    else {
                                                        ErrorBody error = new Gson().fromJson(response.errorBody().charStream(),ErrorBody.class);
                                                        Log.d("PostdetailActivity",error.getMessage());
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<MemberBasicInfo> call, Throwable t) {
                                                    Log.d("PostdetailActivity","????????? ?????? ?????? ??????");
                                                }
                                            });

                                            Toast.makeText(context,"???????????? ?????????????????????",Toast.LENGTH_LONG).show();
                                            ((MainActivity)getActivity()).replaceFragment(new HomeFragment());
                                        }
                                        else{
                                            Toast.makeText(context,"????????? ????????? ??????????????????",Toast.LENGTH_LONG).show();
                                            ErrorBody error = new Gson().fromJson(response.errorBody().charStream(),ErrorBody.class);
                                            Log.d("PostdetailActivity",error.getMessage());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(context,"????????? ????????? ??????????????????.",Toast.LENGTH_LONG).show();
                                        Log.d("PostdetailActivity","????????? ?????? ?????? ??????");
                                    }
                                });

                            }
                        });
                    }
                    else Toast.makeText(context, "id :"+id+" writerId :"+postdata.getPostwriterId(), Toast.LENGTH_SHORT).show();

                    // ???????????? recyclerView ??????
                    hashTagRecyclerAdapter = new HashTagRecyclerAdapter(getActivity(),posthashtags,(PostdetailFragment)fragment);
                    hashtagrecyclerView.setAdapter(hashTagRecyclerAdapter);

                    // ?????? recyclerview ??? ?????? ??????
                    recyclerAdapter = new CommentRecyclerAdapter(getActivity(),comments,(PostdetailFragment)fragment);
                    commentrecyclerView.setAdapter(recyclerAdapter);

                    commentRegister = mView.findViewById(R.id.edit_save_comment);

                    //?????? ?????? ????????? ????????? ???
                    savecomment = mView.findViewById(R.id.btn_save_comment);
                    savecomment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String content = commentRegister.getText().toString(); // ????????? ????????? ?????? ?????? ????????????
                            Call<CommentData> call = postAPI.writeComment(id,postid,new CommentSaveRequest(content)); // ?????? ?????? api ??????
                            call.enqueue(new Callback<CommentData>() {
                                @Override
                                public void onResponse(Call<CommentData> call, Response<CommentData> response) {
                                    if(response.isSuccessful()){ // ?????? ????????? ????????????
                                        // ?????? recyclerView ?????????????????? ?????? ???????????? ????????? ????????? ???????????? ??????
                                        CommentData savedComment = response.body();
                                        comments.add(savedComment);
                                        recyclerAdapter.notifyItemInserted(comments.indexOf(savedComment));
                                        commentrecyclerView.invalidate();
                                        Toast.makeText(context,"?????? ????????? ?????????????????????.",Toast.LENGTH_LONG).show();
                                        commentRegister.setText(null);
                                    }
                                        else{
                                            Toast.makeText(context,"?????? ????????? ??????????????????.",Toast.LENGTH_LONG).show();
                                            ErrorBody error = new Gson().fromJson(response.errorBody().charStream(),ErrorBody.class);
                                            Log.d("PostdetailActivity",error.getMessage());
                                        }
                                }

                                @Override
                                public void onFailure(Call<CommentData> call, Throwable t) {
                                    Toast.makeText(context,"?????? ????????? ??????????????????.",Toast.LENGTH_LONG).show();
                                    Log.d("PostdetailActivity","?????? ?????? ?????? ??????");

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
                Log.d("PostdetailActivity","????????? ?????? ?????? ??????");

            }
        });

    return mView;
    }
    public void refresh(){
        getFragmentManager().beginTransaction().detach(newInstance(postid)).attach(newInstance(postid)).commit();
    }

}