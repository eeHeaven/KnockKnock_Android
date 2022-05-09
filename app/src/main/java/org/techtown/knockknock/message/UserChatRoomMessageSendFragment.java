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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.techtown.knockknock.MainActivity;
import org.techtown.knockknock.R;
import org.techtown.knockknock.RetrofitClient;
import org.techtown.knockknock.location.GeoQuery;
import org.techtown.knockknock.location.LocationChangeFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserChatRoomMessageSendFragment extends Fragment {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static String id;
    static String partnerId;
    static String partnerNickname;
    static Long userchatroomid;
    Button sendBtn;
    TextView partnerinfo;
    EditText messageContext;

    Context context;

    public static UserChatRoomMessageSendFragment newInstance() {

        Bundle args = new Bundle();

        UserChatRoomMessageSendFragment fragment = new UserChatRoomMessageSendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //백버튼 활성화
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = container.getContext();
        View mView = inflater.inflate(R.layout.fragment_user_chat_room_message_send, null);
        partnerinfo = (TextView)mView.findViewById(R.id.userchatroommessage_partner);
        sendBtn = (Button)mView.findViewById(R.id.userchatroommessage_sendbtn);
        messageContext = (EditText)mView.findViewById(R.id.userchatroommessage_content);

        userchatroomid = getArguments().getLong("userchatroomid");
        
        //sharedPreference에서 유저의 아이디와 닉네임 가져오기
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        String nickname = sharedPreferences.getString("nickname", "");
        id = sharedPreferences.getString("userId", "");
        partnerNickname = getArguments().getString("partnerNickname");
        partnerId = getArguments().getString("partnerId");
        
        partnerinfo.setText(partnerNickname+"님께 쪽지 전송");


        //메세지 전송시
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageContext.getText().toString();
                if (message.length() == 0)
                    Toast.makeText(context, "전송할 메세지가 없습니다.", Toast.LENGTH_LONG).show();
                else {
                   sendDialog(partnerId,partnerNickname,message);
                }
            }


        });

        return mView;

    }

    private void sendDialog(String partnerId,String partnerNickname,String messageContext) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("메세지 전송하기");
        builder.setMessage(partnerNickname+"님께 메세지를 보내시겠습니까?");
        builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendMessageAPIcall(partnerId,messageContext);
            }
        });
        builder.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "메세지 전송을 취소했습니다.", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    private void sendMessageAPIcall(String receiver, String message){
        MessageAPI messageAPI = RetrofitClient.getInstance().create(MessageAPI.class);
            MessageSendRequest request = new MessageSendRequest(id,receiver,message);
            Call<MessageResponse> call = messageAPI.sendMessage(request);
            call.enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    Log.d("sendMessageAPIcall",receiver+" 에게 메세지 전송 완료");
                    Toast.makeText(getContext(),"메세지 전송이 완료되었습니다.",Toast.LENGTH_LONG).show();

                    //chatroom 으로 돌아가기
                    Bundle bundle = new Bundle();
                    bundle.putLong("userChatRoomId",userchatroomid);
                    UserChatRoomDetailFragment fragment = new UserChatRoomDetailFragment();
                    fragment.setArguments(bundle);
                    ((MainActivity) getActivity()).replaceFragment(fragment);

                }

                @Override
                public void onFailure(Call<MessageResponse> call, Throwable t) {
                    Log.d("sendMessageAPIcall",t.getMessage());
                }
            });
        }
        

    }


