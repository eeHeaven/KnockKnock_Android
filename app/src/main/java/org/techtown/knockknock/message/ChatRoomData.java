package org.techtown.knockknock.message;

import com.google.gson.annotations.SerializedName;

public class ChatRoomData {

    @SerializedName(value = "id")
    Long id;

    @SerializedName(value = "lastMessage")
    String message;

    @SerializedName(value = "partnerNickname")
    String partner;

    @SerializedName(value = "lastMessageTimeStamp")
    String timestamp;
}
