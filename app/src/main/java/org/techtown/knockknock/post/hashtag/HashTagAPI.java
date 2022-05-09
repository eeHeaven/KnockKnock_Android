package org.techtown.knockknock.post.hashtag;

import org.techtown.knockknock.post.postlist.PostListData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface HashTagAPI {

    @GET("api/search/autocomplete")
    Call<HashTagListData> getHashTagListDatabyAutoCompleteInput(@Query("input") String input);
}
