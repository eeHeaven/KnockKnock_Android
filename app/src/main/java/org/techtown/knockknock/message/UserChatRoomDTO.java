package org.techtown.knockknock.message;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserChatRoomDTO {

    @SerializedName(value = "partnerNickname")
    String partner;

    @SerializedName(value="partnerid")
    String partnerId;

    @SerializedName(value ="messages")
    List<MessageResponse> messages;
}
