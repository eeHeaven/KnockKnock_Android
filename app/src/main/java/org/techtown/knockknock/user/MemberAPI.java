package org.techtown.knockknock.user;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MemberAPI {

    @GET("api/member/{id}")
    Call<LoginResponseInfo> getMemberInfo(@Path("id") Long memberId);

    @POST("api/member")
    Call<LoginResponseInfo> signMember(@Body SignInInfo signInInfo);

    @GET("api/login/{id}/{pw}")
    Call<LoginResponseInfo> login(@Path("id") String id, @Path("pw") String pw);
}
