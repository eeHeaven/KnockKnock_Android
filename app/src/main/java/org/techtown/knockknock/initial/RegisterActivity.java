package org.techtown.knockknock.initial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.techtown.knockknock.ErrorBody;
import org.techtown.knockknock.R;
import org.techtown.knockknock.RetrofitClient;
import org.techtown.knockknock.user.MemberAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText userId;
    EditText password;
    EditText nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button signIn_btn = (Button) findViewById(R.id.btn_register);
        signIn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userId = (EditText) findViewById(R.id.signIn_id);
                password = (EditText) findViewById(R.id.signIn_password);
                nickname = (EditText) findViewById(R.id.signIn_nickname);

                SignInInfo input = new SignInInfo();
                input.setMemberId(userId.getText().toString());
                input.setMemberPassword(password.getText().toString());
                input.setNickname(nickname.getText().toString());

                MemberAPI signInAPI = RetrofitClient.getInstance().create(MemberAPI.class);
                Call<MemberBasicInfo> signInResponseInfo =  signInAPI.signMember(input);
                signInResponseInfo.enqueue(new Callback<MemberBasicInfo>() {
                    @Override
                    public void onResponse(Call<MemberBasicInfo> call, Response<MemberBasicInfo> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"??????????????? ?????????????????????",Toast.LENGTH_LONG).show();
                            Log.d("POST","?????? ??????");
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            //Toast.makeText(getApplicationContext(),"??????????????? ??????????????????",Toast.LENGTH_LONG).show();
                            ErrorBody error = new Gson().fromJson(response.errorBody().charStream(),ErrorBody.class);
                            Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MemberBasicInfo> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"?????? ??????????????????",Toast.LENGTH_LONG).show();
                        Log.d("POST","Fail msg : " + t.getMessage());
                    }
                });


            }
        });

    }
}