package org.techtown.knockknock.message;

import static android.content.Context.MODE_PRIVATE;

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
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;

import org.techtown.knockknock.ErrorBody;
import org.techtown.knockknock.HomeFragment;
import org.techtown.knockknock.MainActivity;
import org.techtown.knockknock.R;
import org.techtown.knockknock.RetrofitClient;
import org.techtown.knockknock.location.LocationChangeFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageFragment extends Fragment {

    Button sendBtn;
    RecyclerView chatRoomList;

    public static MessageFragment newInstance() {

        Bundle args = new Bundle();

        MessageFragment fragment = new MessageFragment();
        fragment.setArguments(args);
        return fragment;
    }
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            //백버튼 숨기기
            ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            View mView = inflater.inflate(R.layout.fragment_message, null);
            chatRoomList = mView.findViewById(R.id.recyclerView_chatRoomlist);

            SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
            String nickname = sharedPreferences.getString("nickname", "");
            String userId = sharedPreferences.getString("userId", "");

            ChatRoomAPI chatRoomAPI = RetrofitClient.getInstance().create(ChatRoomAPI.class);
            Call<ChatRoomListData> call = chatRoomAPI.viewUserChatRoomList(userId);
            call.enqueue(new Callback<ChatRoomListData>() {
                @Override
                public void onResponse(Call<ChatRoomListData> call, Response<ChatRoomListData> response) {
                    if(response.isSuccessful()){
                        ChatRoomListData chatRoomListData = response.body();
                       List<ChatRoomData> chatRoomData = chatRoomListData.data;

                       ChatRoomRecyclerAdapter recyclerAdapter = new ChatRoomRecyclerAdapter(getActivity(),chatRoomData);
                        chatRoomList.setAdapter(recyclerAdapter);
                        chatRoomList.setLayoutManager(new LinearLayoutManager(getActivity()));

                        recyclerAdapter.notifyDataSetChanged();
                       }
                    else{
                        ErrorBody error = new Gson().fromJson(response.errorBody().charStream(),ErrorBody.class);
                        Log.d("MessageFragment",error.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ChatRoomListData> call, Throwable t) {
                        Log.d("MessageFragment",t.getMessage());
                }
            });

            sendBtn = (Button) mView.findViewById(R.id.btn_sendfirstmessage);
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) getActivity()).replaceFragment(new MessageSendFragment());
                }
            });
            return mView;


        }

}