package org.techtown.knockknock.post.postlist;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import org.techtown.knockknock.ErrorBody;
import org.techtown.knockknock.R;
import org.techtown.knockknock.RetrofitClient;
import org.techtown.knockknock.post.PostAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPostFragment extends Fragment {

    TextView writerInfo;

    // 우선 데이터를 담을 list를 하나 만든다.
    PostListData postlist;
    List<PostData> postInfo;

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    public static UserPostFragment newInstance() {

        Bundle args = new Bundle();

        UserPostFragment fragment = new UserPostFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.activity_user_post,null);

        postInfo = new ArrayList<>();
        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);

        //layoutManager: recyclerview에 listview 객체를 하나씩 띄움
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        //sharedPreferences 에서 현재 로그인 되어있는 유저의 id가져오기
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("UserInfo",MODE_PRIVATE);
        String id = sharedPreferences.getString("userId","");
        String nickname = sharedPreferences.getString("nickname","");

        writerInfo = (TextView) mView.findViewById(R.id.tv_writerInfo);
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
                recyclerAdapter = new RecyclerAdapter(getActivity(),postInfo);
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


return mView;
    }
}