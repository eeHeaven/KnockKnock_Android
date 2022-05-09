package org.techtown.knockknock.message;

import com.google.gson.annotations.SerializedName;

public class MessageResponse {

    @SerializedName(value="senderId")
    String senderId;
    @SerializedName(value="message")
    String message;
    @SerializedName(value="timestamp")
    String timestamp;


}
