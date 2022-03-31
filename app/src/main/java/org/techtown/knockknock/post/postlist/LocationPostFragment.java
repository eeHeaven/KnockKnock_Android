package org.techtown.knockknock.post.postlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationPostFragment extends Fragment {

    PostListData postlist;
    List<PostData> postInfo;

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    Button resetmylocation;
    TextView mylocation;

    Geocoder geocoder;

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
        View mView = inflater.inflate(R.layout.fragment_location_post, null);

        //위치정보 구하기
        SharedPreferences locationPreferences = this.getActivity().getSharedPreferences("Location", Context.MODE_PRIVATE);
        geocoder = geocoder = new Geocoder(this.getActivity());
        float lat = locationPreferences.getFloat("latitude",0);
        float lon = locationPreferences.getFloat("longitude",0);
        String location = "현위치에 해당되는 주소 정보가 없습니다.";
        List<Address> list = null;
        try{
            list = geocoder.getFromLocation(lat,lon,10);
        }catch(IOException e){
            e.printStackTrace();
            Log.e("LocationPostFragment","Geocoder 입출력 오류");
        }
        if(list != null){
            if(list.size() == 0) location = "현위치에 해당되는 주소 정보가 없습니다.";
            else location = list.get(0).getAddressLine(0);
        }

        postInfo = new ArrayList<>();
        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView_hashtagBoard);

        resetmylocation = (Button)mView.findViewById(R.id.locationpostlist_resetlocation_btn);
        mylocation = (TextView)mView.findViewById(R.id.tv_locationpostlist_mylocation);
        mylocation.setText(location);


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

        resetmylocation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });
        return mView;
    }

}