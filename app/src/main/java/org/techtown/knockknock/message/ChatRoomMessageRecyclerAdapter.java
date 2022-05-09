package org.techtown.knockknock.message;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.knockknock.ItemClickListener;
import org.techtown.knockknock.MainActivity;
import org.techtown.knockknock.R;

import java.util.List;

public class ChatRoomMessageRecyclerAdapter extends RecyclerView.Adapter<ChatRoomMessageRecyclerAdapter.MyMessageViewHolder>{

    private Context c;
    private List<MessageResponse> messages;
    private MessageFragment fragment;
    private String userId;

    public ChatRoomMessageRecyclerAdapter(Context c, List<MessageResponse> messages,String userId) {
        this.c = c;
        this.messages = messages;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ChatRoomMessageRecyclerAdapter.MyMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        c = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.listview_message, parent, false);
        return new ChatRoomMessageRecyclerAdapter.MyMessageViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ChatRoomMessageRecyclerAdapter.MyMessageViewHolder holder, int position){
        MessageResponse message = messages.get(messages.size()-1-position);

        if(message.senderId.equals(userId)){
            holder.senderReceiver.setText("보낸 쪽지");
            holder.senderReceiver.setTextColor(Color.parseColor("#FFC107"));
        }
        else{
            holder.senderReceiver.setText("받은 쪽지");
            holder.senderReceiver.setTextColor(Color.parseColor("#00C897"));
        }
        holder.message.setText(message.message);
        holder.timeStamp.setText(message.timestamp);

    }

    @Override
    public int getItemCount() {
        int size = messages.size();
        return size;
    }

    public class MyMessageViewHolder extends RecyclerView.ViewHolder {

        TextView senderReceiver;
        TextView timeStamp;
        TextView message;

        public MyMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderReceiver = (TextView) itemView.findViewById(R.id.tv_message_sendreceive);
            timeStamp = (TextView) itemView.findViewById(R.id.tv_message_timestamp);
            message = (TextView) itemView.findViewById(R.id.tv_message_messagecontext);

        }
    }
}
