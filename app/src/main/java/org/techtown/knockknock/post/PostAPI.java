package org.techtown.knockknock.post;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PostAPI {

    @GET("api/post/view/{userId}")
    Call<PostListData> getPostListDatabyUserId(@Path("userId")String id);

}
