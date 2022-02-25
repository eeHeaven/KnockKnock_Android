package org.techtown.knockknock.user;


import org.techtown.knockknock.initial.MemberBasicInfo;
import org.techtown.knockknock.initial.SignInInfo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MemberAPI {

    @GET("api/member/{id}")
    Call<MemberBasicInfo> getMemberInfo(@Path("id") String memberId);

    @POST("api/member")
    Call<MemberBasicInfo> signMember(@Body SignInInfo signInInfo);

    @GET("api/login/{id}/{pw}")
    Call<MemberBasicInfo> login(@Path("id") String id, @Path("pw") String pw);

    @FormUrlEncoded
    @PUT("api/member/sharePoint/{id}")
    Call<MemberBasicInfo> sharePoint(@Path("id") String id, @Field("point") int point);
}
