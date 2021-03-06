package org.techtown.knockknock;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;

import org.techtown.knockknock.location.LocationChangeFragment;
import org.techtown.knockknock.post.PostAPI;
import org.techtown.knockknock.post.postlist.PostData;
import org.techtown.knockknock.post.postlist.PostListData;
import org.techtown.knockknock.post.PostRegisterActivity;
import org.techtown.knockknock.post.postlist.RecyclerAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    PostListData postlist;
    List<PostData> postInfo, filteredList;
    EditText searchET;
    TextView totalboard;
    TextView mylocation;
    TextView listinfo;

    Geocoder geocoder;

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    Button btn_write;
    Button btn_locationchange;

    private Double targetlatitude;
    private Double targetlongitude;


    private DatabaseReference mDatabase;

    public static HomeFragment newInstance() {

        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_home, null);

        //????????? ?????????
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        postInfo = new ArrayList<>();
        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView_hashtagBoard);
        geocoder = new Geocoder(this.getActivity());

        TextView loginInfo = (TextView) mView.findViewById(R.id.loginResult);
        listinfo = mView.findViewById(R.id.home_tv_listinfo);
        btn_write = (Button) mView.findViewById(R.id.btn_homefragment_postRegister);
        totalboard = mView.findViewById(R.id.userchatroomdetail_partnerId);

        filteredList = new ArrayList<>();

        if (getArguments() != null) { //?????? ???????????? ?????? ?????? ????????? ?????????
            targetlatitude = getArguments().getDouble("latitude");
            targetlongitude = getArguments().getDouble("longitude");
            listinfo.setText("????????? ?????? ?????? 1km ?????? ?????? ?????? ???????????????.");
        } else {
            //sharedPreference?????? ????????? ?????? ????????????
            SharedPreferences locationpreferences = this.getActivity().getSharedPreferences("Location", MODE_PRIVATE);
            targetlatitude = Double.valueOf(locationpreferences.getFloat("latitude", 0));
            targetlongitude = Double.valueOf(locationpreferences.getFloat("longitude", 0));
        }

        //sharedPreference?????? ????????? ???????????? ????????? ????????????
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        String nickname = sharedPreferences.getString("nickname", "");
        String id = sharedPreferences.getString("userId", "");
        loginInfo.setText(nickname + " ???, ???????????????!");

        //Geocoder??? ??? ?????? ????????? ??????
        String location = "???????????? ???????????? ?????? ????????? ????????????.";
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(targetlatitude, targetlongitude, 10);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MypageFragment", "Geocoder ????????? ??????");
        }
        if (list != null) {
            if (list.size() == 0) location = "???????????? ???????????? ?????? ????????? ????????????.";
            else location = list.get(0).getAddressLine(0);
        }

        mylocation = (TextView) mView.findViewById(R.id.home_tv_mylocation);
        mylocation.setText(location);


        //?????? ?????? ?????? ?????????
        btn_locationchange = (Button) mView.findViewById(R.id.btn_homefragment_locationchange);
        btn_locationchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                //home fragment???????????? ???????????? ??????
                bundle.putChar("from",'h');

                LocationChangeFragment fragment = new LocationChangeFragment();
                fragment.setArguments(bundle);
                ((MainActivity)getActivity()).replaceFragment(fragment);
            }
        });

        //?????? post list ????????????
        PostAPI postAPI = RetrofitClient.getInstance().create(PostAPI.class);
        Call<PostListData> call = postAPI.getPostListbyLocation(targetlatitude, targetlongitude);
        call.enqueue(new Callback<PostListData>() {
            @Override
            public void onResponse(Call<PostListData> call, Response<PostListData> response) {
                if (response.isSuccessful()) {
                    postlist = response.body();
                    Log.d("MainActivity", postlist.toString());
                    postInfo = postlist.data;

                    recyclerAdapter = new RecyclerAdapter(getActivity(), postInfo);
                    recyclerView.setAdapter(recyclerAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                    recyclerAdapter.notifyDataSetChanged();

                } else {
                    ErrorBody error = new Gson().fromJson(response.errorBody().charStream(), ErrorBody.class);
                    Log.d("MainActivity", error.getMessage());
                }
            }

            @Override
            public void onFailure(Call<PostListData> call, Throwable t) {
                Log.d("MainActivity", t.getMessage());
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


        return mView;
    }
}