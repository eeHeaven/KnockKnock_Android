package org.techtown.knockknock.post;

public class PostSaveRequest {

    String title;
    String content;

    public PostSaveRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
