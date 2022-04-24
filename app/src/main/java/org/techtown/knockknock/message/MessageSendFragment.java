package org.techtown.knockknock.message;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.techtown.knockknock.MainActivity;
import org.techtown.knockknock.R;
import org.techtown.knockknock.location.GPS;
import org.techtown.knockknock.location.GeoQuery;
import org.techtown.knockknock.location.LocationChangeFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageSendFragment extends Fragment {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    Button setlocation;
    private Double targetlatitude;
    private Double targetlongitude;

    TextView messageinfo;
    TextView mylocation;
    EditText messagecontent;
    ImageView sendMessage;
    private List<String> targetusers = new ArrayList<>();

    Geocoder geocoder;
    Context context;

    public static MessageSendFragment newInstance() {

        Bundle args = new Bundle();

        MessageSendFragment fragment = new MessageSendFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //백버튼 활성화
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = container.getContext();
        View mView = inflater.inflate(R.layout.fragment_message_send, null);
        geocoder = new Geocoder(this.getActivity());
        setlocation = (Button) mView.findViewById(R.id.btn_messagesend_locationchange);
        messageinfo = (TextView) mView.findViewById(R.id.messagesend_tv_messageinfo);
        mylocation = (TextView) mView.findViewById(R.id.messagesend_tv_mylocation);
        sendMessage = (ImageView) mView.findViewById(R.id.btn_messagesend_send);
        messagecontent = (EditText) mView.findViewById(R.id.tv_messagesend_content);

        //타겟 위치 설정하기
        if (getArguments() != null) { //위치 변경에서 조회 기준 위치를 변경함
            targetlatitude = getArguments().getDouble("latitude");
            targetlongitude = getArguments().getDouble("longitude");
            messageinfo.setText("실시간 타겟 위치 1km 이내 유저들에게 메세지가 전송됩니다.");
        } else {
            //sharedPreference에서 유저의 위치 가져오기
            SharedPreferences locationpreferences = this.getActivity().getSharedPreferences("Location", MODE_PRIVATE);
            targetlatitude = Double.valueOf(locationpreferences.getFloat("latitude", 0));
            targetlongitude = Double.valueOf(locationpreferences.getFloat("longitude", 0));
            Log.d("MessageSend","lat : "+targetlatitude+" lon : "+targetlongitude);
        }

        //sharedPreference에서 유저의 아이디와 닉네임 가져오기
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        String nickname = sharedPreferences.getString("nickname", "");
        String id = sharedPreferences.getString("userId", "");

        //Geocoder로 현 위치 주소값 생성
        String location = "현위치에 해당되는 주소 정보가 없습니다.";
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(targetlatitude, targetlongitude, 10);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MypageFragment", "Geocoder 입출력 오류");
        }
        if (list != null) {
            if (list.size() == 0) location = "현위치에 해당되는 주소 정보가 없습니다.";
            else location = list.get(0).getAddressLine(0);
        }
        mylocation.setText(location);

        //위치 변경하기
        setlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                //home fragment로부터의 요청임을 보냄
                bundle.putChar("from",'m');

                LocationChangeFragment fragment = new LocationChangeFragment();
                fragment.setArguments(bundle);
                ((MainActivity)getActivity()).replaceFragment(fragment);
            }
        });
        //메세지 전송시
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messagecontent.getText().toString();
                if(message.length()==0) Toast.makeText(context,"전송할 메세지가 없습니다.", Toast.LENGTH_LONG);
                else{targetusers = GeoQuery.findUserIdsNear(targetlatitude,targetlongitude);
                targetusers.remove(id);
                if(targetusers.size() == 0){
                    Toast.makeText(context,"메세지를 받을 목표 지점 인근 유저가 없습니다.", Toast.LENGTH_LONG);
                }
                else{
                    sendDialog();
                }}
                        }



        });

        return mView;

    }
    private void sendDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("메세지 전송하기");
        builder.setMessage("목표 지점 1km 이내의 유저 "+targetusers.size()+"명에게 메세지를 보내시겠습니까?");
        builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                //예 눌렀을때의 이벤트 처리
            }
        });
        builder.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context,"메세지 전송을 취소했습니다.",Toast.LENGTH_LONG);
            }
        });
        builder.show();
    }


}