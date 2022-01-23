package org.techtown.knockknock.user;

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
                Call<LoginResponseInfo> signInResponseInfo =  signInAPI.signMember(input);
                signInResponseInfo.enqueue(new Callback<LoginResponseInfo>() {
                    @Override
                    public void onResponse(Call<LoginResponseInfo> call, Response<LoginResponseInfo> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"회원가입이 완료되었습니다",Toast.LENGTH_LONG).show();
                            Log.d("POST","등록 완료");
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            //Toast.makeText(getApplicationContext(),"회원가입이 실패했습니다",Toast.LENGTH_LONG).show();
                            ErrorBody error = new Gson().fromJson(response.errorBody().charStream(),ErrorBody.class);
                            Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponseInfo> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"연결 실패했습니다",Toast.LENGTH_LONG).show();
                        Log.d("POST","Fail msg : " + t.getMessage());
                    }
                });


            }
        });

    }
}