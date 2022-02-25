package org.techtown.knockknock;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.techtown.knockknock.post.PostAPI;
import org.techtown.knockknock.post.PostData;
import org.techtown.knockknock.post.PostListData;
import org.techtown.knockknock.post.PostRegisterActivity;
import org.techtown.knockknock.post.RecyclerAdapter;
import org.techtown.knockknock.post.UserPostFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    PostListData postlist;
    List<PostData> postInfo, filteredList;
    EditText searchET;
    TextView totalboard;

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    Button button;
    Button btn_write;

    public static HomeFragment newInstance() {

        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View mView = inflater.inflate(R.layout.activity_main,null);

        postInfo = new ArrayList<>();
        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView_hashtagBoard);


        TextView loginInfo = (TextView)mView.findViewById(R.id.loginResult);
        button = (Button) mView.findViewById(R.id.btn_viewUserPost);
        btn_write = (Button)mView.findViewById(R.id.btn_hashtagfragment_postRegister);
        searchET = mView.findViewById(R.id.searchTag);
        totalboard = mView.findViewById(R.id.tv_totalBoard);

        filteredList = new ArrayList<>();


        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("UserInfo",MODE_PRIVATE);
        String nickname = sharedPreferences.getString("nickname","");
        String id = sharedPreferences.getString("userId","");
        loginInfo.setText(nickname+" 님, 환영합니다!");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //userpostactivity로 넘어가기
                ((MainActivity)getActivity()).replaceFragment(UserPostFragment.newInstance());
            }
        });

        //전체 post list 받아오기
        PostAPI postAPI = RetrofitClient.getInstance().create(PostAPI.class);
        Call<PostListData> call = postAPI.getPostList();
        call.enqueue(new Callback<PostListData>() {
            @Override
            public void onResponse(Call<PostListData> call, Response<PostListData> response) {
                if(response.isSuccessful()){
                    postlist = response.body();
                    Log.d("MainActivity",postlist.toString());
                    postInfo = postlist.data;

                    recyclerAdapter = new RecyclerAdapter(getActivity(),postInfo);
                    recyclerView.setAdapter(recyclerAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
                Intent intent = new Intent(getActivity(), PostRegisterActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        //검색 기능 활성화
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = searchET.getText().toString();
                int size = searchFilter(searchText);
                totalboard.setText(searchText+ " 검색 결과 ("+size+")");
            }
        });
        return mView;


    }
    public int searchFilter(String searchText){
        filteredList.clear();
        for(int i = 0; i< postInfo.size();i++){
            for(int j = 0; j<postInfo.get(i).getHashtag().size();j++){
                if(postInfo.get(i).getHashtag().get(j).toLowerCase().contains(searchText.toLowerCase())){
                    filteredList.add(postInfo.get(i));
                }
            }
        }
        recyclerAdapter.filterList(filteredList);
        return filteredList.size();
    }



}