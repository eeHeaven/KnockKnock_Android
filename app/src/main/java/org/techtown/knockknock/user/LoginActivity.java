package org.techtown.knockknock.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.techtown.knockknock.ErrorBody;
import org.techtown.knockknock.MainActivity;
import org.techtown.knockknock.R;
import org.techtown.knockknock.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    Button btn_login;
    Button btn_register2;

    EditText et_id2;
    EditText et_pass2;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register2 = (Button)findViewById(R.id.btn_register2);

        btn_login.setOnClickListener(this);
        btn_register2.setOnClickListener(this);

        et_id2 = (EditText) findViewById(R.id.et_id2);
        et_pass2 = (EditText) findViewById(R.id.et_pass2);

        //getSharedPreferences("파일이름",'모드');
        // 모드 => 0 읽기 쓰기 가능
        // 모드 => Mode_Private 이 앱에서만 사용 가능
        preferences = getSharedPreferences("UserInfo",MODE_PRIVATE);


    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_login:

                //사용자 입력 id,pw 가져오기
                String id = et_id2.getText().toString();
                String pw = et_pass2.getText().toString();

                // memberAPI 연결 활성화하기
                MemberAPI memberAPI = RetrofitClient.getInstance().create(MemberAPI.class);
                Call<LoginResponseInfo> loginResponseInfoCall = memberAPI.login(id,pw);
                loginResponseInfoCall.enqueue(new Callback<LoginResponseInfo>() {
                    @Override
                    public void onResponse(Call<LoginResponseInfo> call, Response<LoginResponseInfo> response) {
                        if(response.isSuccessful()){
                        Log.d("HTTP","로그인 성공");
                            LoginResponseInfo responseInfo = response.body();

                            //Editor를 preferences에 쓰겠다고 연결
                            SharedPreferences.Editor editor = preferences.edit();
                            //putString(Key,Value)
                            String userId = responseInfo.getMemberId();
                            String nickname = responseInfo.getNickname();
                            editor.putString("userId", responseInfo.getMemberId());
                            editor.putString("nickname",responseInfo.getNickname());
                            //항상 commit & apply를 해야 저장
                            editor.commit();
                            //Main.Activity로 넘어가기
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        else{Log.d("HTTP","로그인 실패");
                            ErrorBody error = new Gson().fromJson(response.errorBody().charStream(),ErrorBody.class);
                            Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponseInfo> call, Throwable t) {
                        Log.d("HTTP","로그인 연결 실패 ");
                        Log.e("연결실패", t.getMessage());
                    }
                });

                break;

            case R.id.btn_register2:
                Intent intent2 = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent2);
                finish();
                break;
        }
    }

    private void getPreferences(){
        Toast.makeText(getApplicationContext(),"USERID = " + preferences.getString("userId","")
                + "\n USERPWD = " + preferences.getString("userpwd",""),Toast.LENGTH_LONG).show();
    }
}