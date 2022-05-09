package org.techtown.knockknock.message;

import org.techtown.knockknock.post.hashtag.HashTagListData;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface MessageAPI {

    @POST("api/message/send")
    Call<MessageResponse> sendMessage(@Body MessageSendRequest reqeust);
}
