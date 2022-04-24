package org.techtown.knockknock.post.hashtag;

import com.google.gson.annotations.SerializedName;

import org.techtown.knockknock.post.postlist.PostData;

import java.util.List;

public class HashTagListData {
    @SerializedName("data")
    public List<HashTagData> data;

    public List<HashTagData> getData() {
        return data;
    }

    public void setData(List<HashTagData> data) {
        this.data = data;
    }

    @Override
    public String toString(){
        return "Data{"+"data = "+data+"}";
    }
}
