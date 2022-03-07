package org.techtown.knockknock.post.postdetail;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.techtown.knockknock.post.CommentData;

import java.util.List;

public class PostDetailData {

    @SerializedName("id")
    Long postid;

    @SerializedName("writernickname")
    String postwriternickname;

    @SerializedName("writerId")
    String postwriterId;

    @SerializedName("title")
    String postTitle;

    @SerializedName("content")
    String postContent;

    @SerializedName("postedTime")
    String postedTime;

    @SerializedName("commentlist")
    List<CommentData> comments;

    @SerializedName("posthashtag")
    List<String> posthashtag;

    @Nullable
    @SerializedName("image")
    String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getPostid() {
        return postid;
    }

    public void setPostid(Long postid) {
        this.postid = postid;
    }

    public String getPostwriternickname() {
        return postwriternickname;
    }

    public void setPostwriternickname(String postwriternickname) {
        this.postwriternickname = postwriternickname;
    }

    public List<String> getPosthashtag() {
        return posthashtag;
    }

    public void setPosthashtag(List<String> posthashtag) {
        this.posthashtag = posthashtag;
    }

    public String getPostwriterId(){return postwriterId;}
    public void setPostwriterId(String postwriterId){
        this.postwriterId = postwriterId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getPostedTime() {
        return postedTime;
    }

    public void setPostedTime(String postedTime) {
        this.postedTime = postedTime;
    }

    public List<CommentData> getComments() {
        return comments;
    }

    public void setComments(List<CommentData> comments) {
        this.comments = comments;
    }


}
