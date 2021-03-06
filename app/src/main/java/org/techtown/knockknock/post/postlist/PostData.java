package org.techtown.knockknock.post.postlist;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.List;

public class PostData {

    //{"data":[{"writerId":"testmember1","title":"테스트입니다","content":"성공인가요?","postedTime":"2022-01-22T23:14:56.314697"}]}

    @SerializedName("id")
    Long id;

    @SerializedName("writerId")
    String writerId;

    @SerializedName("title")
    String title;

    @SerializedName("content")
    String content;

    @SerializedName("postedTime")
    String date;

    @SerializedName("hashtag")
    List<String> hashtag;

    public String getWriterId() {
        return writerId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public Long getId(){return id;}

    public List<String> getHashtag() {
        return hashtag;
    }

    public void setHashtag(List<String> hashtag) {
        this.hashtag = hashtag;
    }

    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
