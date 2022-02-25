package org.techtown.knockknock.post;



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
