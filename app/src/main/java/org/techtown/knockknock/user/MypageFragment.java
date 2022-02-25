package org.techtown.knockknock.user;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.techtown.knockknock.ErrorBody;
import org.techtown.knockknock.MainActivity;
import org.techtown.knockknock.R;
import org.techtown.knockknock.RetrofitClient;
import org.techtown.knockknock.initial.MemberBasicInfo;
import org.techtown.knockknock.post.PostRegisterActivity;
import org.techtown.knockknock.post.UserPostFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MypageFragment extends Fragment {

    TextView tv_nickname;
    TextView tv_userId;
    TextView tv_sharedPoint;
    LinearLayout ly_mypost;

    static int point;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_mypage,null);
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("UserInfo",MODE_PRIVATE);
        String nickname = sharedPreferences.getString("nickname","");
        String id = sharedPreferences.getString("userId","");

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