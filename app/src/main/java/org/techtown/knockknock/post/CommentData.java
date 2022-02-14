package org.techtown.knockknock.post;


//{
//    "id": 2,
//    "writer": "테스트멤버1",
//    "title": "테스트입니다",
//    "content": "성공인가요?",
//    "postedTime": "2022-01-22T23:14:56.314697",
//    "commentlist": [
//        {
//            "commentId": 1001,
//            "writerNickname": "테스트멤버1",
//            "timestamp": "2022-01-24T12:19:54.123447",
//            "content": "테스트 댓글입니다."
//        }
//    ]
//}

import com.google.gson.annotations.SerializedName;

public class CommentData {

    @SerializedName("commentId")
    Long commentId;

    @SerializedName("writerNickname")
    String commentWriternickname;

    @SerializedName("writerId")
    String commentWriterId;

    @SerializedName("timestamp")
    String commentedTime;

    @SerializedName("content")
    String commentContent;

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getCommentWriterNickname() {
        return commentWriternickname;
    }

    public void setCommentWriternickname(String commentWriter) {
        this.commentWriternickname = commentWriter;
    }

    public String getCommentWriterId(){return commentWriterId;}
    public void setCommentWriterId(String commentWriterId){
        this.commentWriterId = commentWriterId;
    }

    public String getCommentedTime() {
        return commentedTime;
    }

    public void setCommentedTime(String commentedTime) {
        this.commentedTime = commentedTime;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
