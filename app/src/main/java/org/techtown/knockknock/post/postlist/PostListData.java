package org.techtown.knockknock.post.postlist;

import com.google.gson.annotations.SerializedName;

import org.techtown.knockknock.post.postlist.PostData;

import java.util.List;

public class PostListData {

    @SerializedName("data")
    public List<PostData> data;

    public List<PostData> getData() {
        return data;
    }

    public void setData(List<PostData> data) {
        this.data = data;
    }

    @Override
    public String toString(){
        return "Data{"+"data = "+data+"}";
    }
}
