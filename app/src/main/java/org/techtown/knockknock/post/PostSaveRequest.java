package org.techtown.knockknock.post;

public class PostSaveRequest {

    String title;
    String hashtag;
    String content;

    public PostSaveRequest(String title, String hashtag, String content) {
        this.title = title;
        this.hashtag = hashtag;
        this.content = content;
    }
}
