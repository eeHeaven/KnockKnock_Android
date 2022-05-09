package org.techtown.knockknock.message;

public class MessageSendRequest {

    String senderId;
    String receiverId;
    String message;

    public MessageSendRequest(String senderId, String receiverId, String message) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
    }
}
