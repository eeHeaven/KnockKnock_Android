package org.techtown.knockknock.post;

public class PostSaveRequest {

    String title;
    String hashtag;
    String content;
    Float lat;
    Float lon;
    String location;

    public PostSaveRequest(String title, String hashtag, String content,Float lat, Float lon,String location) {
        this.title = title;
        this.hashtag = hashtag;
        this.content = content;
        this.lat = lat;
        this.lon = lon;
        this.location =location;
    }
}
