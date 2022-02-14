package org.techtown.knockknock.post;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PostAPI {

    @GET("api/post/viewlist/{userId}")
    Call<PostListData> getPostListDatabyUserId(@Path("userId")String id);

    @GET("api/post/view/{postId}")
    Call<PostDetailData> getPostDatabyPostId(@Path("postId")Long id);

    @GET("api/post/view")
    Call<PostListData> getPostList();

    @POST("api/post/{userId}")
    Call<PostDetailData> writePost(@Path("userId")String userId, @Body PostSaveRequest request);

    @POST("api/{userId}/post/view/{postId}/comment")
    Call<CommentData> writeComment(@Path("userId")String userId,@Path("postId")Long postId,@Body CommentSaveRequest request);

    @DELETE("api/post/view/{postId}/delete")
    Call<Void> deletePost(@Path("postId")Long postId);

    @DELETE("api/comment/{commentid}/delete")
    Call<Void> deleteComment(@Path("commentid")Long commentId);
}
