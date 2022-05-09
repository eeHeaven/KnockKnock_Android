package org.techtown.knockknock.message;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.knockknock.ItemClickListener;
import org.techtown.knockknock.MainActivity;
import org.techtown.knockknock.R;

import java.util.List;

public class ChatRoomRecyclerAdapter extends RecyclerView.Adapter<ChatRoomRecyclerAdapter.MyChatRoomViewHolder>{

    private Context c;
    private List<ChatRoomData> chatRoomDataList;
    private MessageFragment fragment;

    public ChatRoomRecyclerAdapter(Context c, List<ChatRoomData> chatRoomDataList){
        this.c = c;
        this.chatRoomDataList = chatRoomDataList;
    }



    @NonNull
    @Override
    public ChatRoomRecyclerAdapter.MyChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        c = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.listview_chatroom,parent,false);
        return new ChatRoomRecyclerAdapter.MyChatRoomViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ChatRoomRecyclerAdapter.MyChatRoomViewHolder holder, int position){
        ChatRoomData chatRoomData = chatRoomDataList.get(position);
        holder.partner.setText(chatRoomData.partner);
        holder.message.setText(chatRoomData.message);
        holder.timeStamp.setText(chatRoomData.timestamp);

        holder.itemClickListener = new ItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                Long userChatRoomId = chatRoomData.id;
                Bundle bundle = new Bundle();
                bundle.putLong("userChatRoomId",userChatRoomId);

                UserChatRoomDetailFragment userChatRoomDetailFragment = new UserChatRoomDetailFragment();
                userChatRoomDetailFragment.setArguments(bundle);
                ((MainActivity)c).replaceFragment(userChatRoomDetailFragment);
            }
        };

    }

    @Override
    public int getItemCount(){
        int size = chatRoomDataList.size();
        return size;
    }

    public class MyChatRoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView partner;
        TextView timeStamp;
        TextView message;

        ItemClickListener itemClickListener;
        public MyChatRoomViewHolder(@NonNull View itemView){
            super(itemView);
            partner = (TextView)itemView.findViewById(R.id.tv_chatroomlist_partner);
            timeStamp = (TextView)itemView.findViewById(R.id.tv_chatroomlist_timestamp);
            message = (TextView)itemView.findViewById(R.id.tv_chatroomlist_message);

            itemView.setOnClickListener(this);}
        @Override
        public void onClick(View v){
            this.itemClickListener.onItemClickListener(v, getLayoutPosition());
        }
    }
}
