package org.techtown.knockknock.message;

import com.google.gson.annotations.SerializedName;

import org.techtown.knockknock.post.postlist.PostData;

import java.util.List;

public class ChatRoomListData {

    @SerializedName("data")
    public List<ChatRoomData> data;

    public List<ChatRoomData> getData() {
        return data;
    }

    public void setData(List<ChatRoomData> data) {
        this.data = data;
    }

    @Override
    public String toString(){
        return "Data{"+"data = "+data+"}";
    }
}
