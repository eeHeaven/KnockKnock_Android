package org.techtown.knockknock.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.knockknock.MainActivity;
import org.techtown.knockknock.R;

import java.util.HashMap;

public class FirebaseTestActivity extends AppCompatActivity {

    EditText id,pw;
    Button btn;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_test);

        id = findViewById(R.id.firebase_id);
        pw = findViewById(R.id.firebase_pw);
        btn = findViewById(R.id.firebase_btn);

        //firebase 정의
        mDatabase  = FirebaseDatabase.getInstance().getReference();
        readUser();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String u_id = id.getText().toString();
                String u_pw = pw.getText().toString();

                writeNewUser("1",u_id,u_pw);
            }
        });
    }
    private void writeNewUser(String userId, String name, String email) {
        FirebaseUser user = new FirebaseUser(name, email);

        mDatabase.child("users").child(userId).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        Toast.makeText(getApplicationContext(), "저장을 완료했습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(getApplicationContext(), "저장을 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void readUser(){
        mDatabase.child("users").child("1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if(dataSnapshot.getValue(FirebaseUser.class) != null){
                    FirebaseUser post = dataSnapshot.getValue(FirebaseUser.class);
                    Log.w("FireBaseData", "getData" + post.toString());
                } else {
                    Toast.makeText(getApplicationContext(), "데이터 없음...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FireBaseData", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
}