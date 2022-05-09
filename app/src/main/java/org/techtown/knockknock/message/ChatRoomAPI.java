package org.techtown.knockknock.message;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ChatRoomAPI {

    @GET("api/userchatroom/list/{userId}")
    Call<ChatRoomListData> viewUserChatRoomList(@Path("userId")String userId);

    @GET("api/userchatroom/{chatroomid}")
    Call<UserChatRoomDTO> viewUserChatRoomDetail(@Path("chatroomid")Long id);
}
