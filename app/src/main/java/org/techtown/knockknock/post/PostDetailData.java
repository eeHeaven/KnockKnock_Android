package org.techtown.knockknock.post;

import com.google.gson.annotations.SerializedName;

import java.util.List;

//{
//        "id": 2,
//        "writer": "테스트멤버1",
//        "title": "테스트입니다",
//        "content": "성공인가요?",
//        "postedTime": "2022-01-22T23:14:56.314697",
//        "commentlist": [
//        {
//        "commentId": 1001,
//        "writerNickname": "테스트멤버1",
//        "timestamp": "2022-01-24T12:19:54.123447",
//        "content": "테스트 댓글입니다."
//        }
//        ]
//        }
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