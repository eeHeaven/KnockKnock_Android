package org.techtown.knockknock.post.hashtag;

import com.google.gson.annotations.SerializedName;

public class HashTagData {

    @SerializedName("tag")
    String tag;

    public String getTag() {
        return tag;
    }
}
