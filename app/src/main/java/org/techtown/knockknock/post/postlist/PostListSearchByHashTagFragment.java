package org.techtown.knockknock.post.postlist;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import org.techtown.knockknock.ErrorBody;
import org.techtown.knockknock.R;
import org.techtown.knockknock.RetrofitClient;
import org.techtown.knockknock.post.PostAPI;
import org.techtown.knockknock.post.PostRegisterActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostListSearchByHashTagFragment extends Fragment {

    PostListData postlist;
    List<PostData> postInfo;

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    Button writePost;
    TextView postamount;
    TextView hashtag;

    static String tag;

    public static PostListSearchByHashTagFragment newInstance(){

        Bundle args = new Bundle();
        PostListSearchByHashTagFragment fragment = new PostListSearchByHashTagFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_post_list_search_by_hash_tag, null);

        postInfo = new ArrayList<>();
        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView_hashtagBoard);

        postamount = (TextView) mView.findViewById(R.id.tv_hashtagfragment_number);
        hashtag = (TextView) mView.findViewById(R.id.tv_hashtagfragment_hashtag);
        writePost = (Button) mView.findViewById(R.id.btn_homefragment_postRegister);

        if(getArguments()!=null){
            tag = getArguments().getString("hashtag");
        }
        hashtag.setText(tag);

        //글 목록 가져오기
        PostAPI postAPI = RetrofitClient.getInstance().create(PostAPI.class);
        String tagforapi = tag.substring(1);
        Call<PostListData> call = postAPI.getPostDatabyHashTag(tagforapi);
        call.enqueue(new Callback<PostListData>() {
            @Override
            public void onResponse(Call<PostListData> call, Response<PostListData> response) {
                if(response.isSuccessful()){
                    postlist = response.body();
                    Log.d("PostListByTag","게시글 가져오기 성공");
                    postInfo = postlist.data;
                    postamount.setText(Integer.toString(postInfo.size()));

                    recyclerAdapter = new RecyclerAdapter(getActivity(),postInfo);
                    recyclerView.setAdapter(recyclerAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                }
                else{
                    ErrorBody error = new Gson().fromJson(response.errorBody().charStream(),ErrorBody.class);
                    Log.d("PostListByTag",error.getMessage());
                }
            }

            @Override
            public void onFailure(Call<PostListData> call, Throwable t) {
                Log.d("PostListByTag",t.getMessage());
            }
        });

        writePost.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PostRegisterActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return mView;
    }

}