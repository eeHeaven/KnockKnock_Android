package org.techtown.knockknock.message;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.techtown.knockknock.ErrorBody;
import org.techtown.knockknock.MainActivity;
import org.techtown.knockknock.R;
import org.techtown.knockknock.RetrofitClient;
import org.techtown.knockknock.location.LocationChangeFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserChatRoomDetailFragment extends Fragment {
    Context context;
    RecyclerView recyclerView;
    TextView partnerIdTextView;
    Long chatroomId;
    ImageView sendBtn;
    static String partnerNickname;
    static String partnerId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //백버튼 활성화
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = container.getContext();
        View mView = inflater.inflate(R.layout.fragment_user_chat_room_detail, null);
        recyclerView = (RecyclerView) mView.findViewById(R.id.userchatroomdetail_recycler);
        partnerIdTextView = (TextView) mView.findViewById(R.id.userchatroomdetail_partnerId);
        chatroomId = getArguments().getLong("userChatRoomId");
        sendBtn = (ImageView)mView.findViewById(R.id.btn_message_send);

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        String nickname = sharedPreferences.getString("nickname", "");
        String userId = sharedPreferences.getString("userId", "");

        ChatRoomAPI chatRoomAPI = RetrofitClient.getInstance().create(ChatRoomAPI.class);
        Call<UserChatRoomDTO> call = chatRoomAPI.viewUserChatRoomDetail(chatroomId);
        call.enqueue(new Callback<UserChatRoomDTO>() {
            @Override
            public void onResponse(Call<UserChatRoomDTO> call, Response<UserChatRoomDTO> response) {
                if(response.isSuccessful()){
                    UserChatRoomDTO userChatRoomDTO= response.body();
                    partnerIdTextView.setText(userChatRoomDTO.partner +" 님과의 쪽지");
                    partnerNickname = userChatRoomDTO.partner;
                    partnerId = userChatRoomDTO.partnerId;

                    List<MessageResponse> messages = userChatRoomDTO.messages;
                    ChatRoomMessageRecyclerAdapter recyclerAdapter = new ChatRoomMessageRecyclerAdapter(getActivity(),messages,userId);
                    recyclerView.setAdapter(recyclerAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                    recyclerAdapter.notifyDataSetChanged();

                }
                else{
                    ErrorBody error = new Gson().fromJson(response.errorBody().charStream(),ErrorBody.class);
                    Log.d("UserChatRoomDetail",error.getMessage());
                }
            }

            @Override
            public void onFailure(Call<UserChatRoomDTO> call, Throwable t) {
                Log.d("UserChatRoomDetail",t.getMessage());
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("partnerNickname",partnerNickname);
                bundle.putString("partnerId",partnerId);
                bundle.putLong("userchatroomid",chatroomId);
            

                UserChatRoomMessageSendFragment fragment = new UserChatRoomMessageSendFragment();
                fragment.setArguments(bundle);
                ((MainActivity) getActivity()).replaceFragment(fragment);
            }
        });
        return mView;
    }
}