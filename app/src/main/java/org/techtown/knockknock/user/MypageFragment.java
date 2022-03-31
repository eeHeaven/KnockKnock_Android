package org.techtown.knockknock.user;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.techtown.knockknock.ErrorBody;
import org.techtown.knockknock.MainActivity;
import org.techtown.knockknock.R;
import org.techtown.knockknock.RetrofitClient;
import org.techtown.knockknock.initial.MemberBasicInfo;
import org.techtown.knockknock.post.postlist.UserPostFragment;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MypageFragment extends Fragment {

    TextView tv_nickname;
    TextView tv_userId;
    TextView tv_sharedPoint;
    TextView tv_location;
    LinearLayout ly_mypost;

    static int point;

    static Geocoder geocoder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        geocoder = new Geocoder(this.getActivity());

        View mView = inflater.inflate(R.layout.fragment_mypage,null);
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("UserInfo",MODE_PRIVATE);
        SharedPreferences locationPreferences = this.getActivity().getSharedPreferences("Location",MODE_PRIVATE);
        String nickname = sharedPreferences.getString("nickname","");
        String id = sharedPreferences.getString("userId","");

        //백버튼 숨기기
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //위치 정보 구하기
        float lat = locationPreferences.getFloat("latitude",0);
        float lon = locationPreferences.getFloat("longitude",0);
        String location = "현위치에 해당되는 주소 정보가 없습니다.";
        List<Address> list = null;
        try{
            list = geocoder.getFromLocation(lat,lon,10);
        }catch(IOException e){
            e.printStackTrace();
            Log.e("MypageFragment","Geocoder 입출력 오류");
        }
        if(list != null){
            if(list.size() == 0) location = "현위치에 해당되는 주소 정보가 없습니다.";
            else location = list.get(0).getAddressLine(0);
        }

        tv_location = (TextView)mView.findViewById(R.id.tv_mypage_mylocation);
        tv_location.setText(location);
        
        MemberAPI memberAPI = RetrofitClient.getInstance().create(MemberAPI.class);
        Call<MemberBasicInfo> memberinfo = memberAPI.getMemberInfo(id);
        memberinfo.enqueue(new Callback<MemberBasicInfo>() {
            @Override
            public void onResponse(Call<MemberBasicInfo> call, Response<MemberBasicInfo> response) {
                if(response.isSuccessful()){
                    MemberBasicInfo member = response.body();
                    point = member.getPoint();
                }
                else{
                    ErrorBody error = new Gson().fromJson(response.errorBody().charStream(),ErrorBody.class);
                    Log.d("MyPageFragment",error.getMessage());
                }
            }

            @Override
            public void onFailure(Call<MemberBasicInfo> call, Throwable t) {
                Log.d("MyPageFragment","포인트 조회 연결 실패");
            }
        });
        if(mView != null) {tv_nickname = (TextView)mView.findViewById(R.id.tv_mypage_nickname);
        tv_userId = (TextView)mView.findViewById(R.id.tv_mypage_id);
        tv_sharedPoint = (TextView)mView.findViewById(R.id.tv_mypage_point);
        ly_mypost = (LinearLayout)mView.findViewById(R.id.ly_button_mypost);}


        tv_nickname.setText(nickname);
        tv_userId.setText(id);
        tv_sharedPoint.setText(Integer.toString(point)+"point");

        ly_mypost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).replaceFragment(UserPostFragment.newInstance());
            }
        });

        return mView;
    }
}