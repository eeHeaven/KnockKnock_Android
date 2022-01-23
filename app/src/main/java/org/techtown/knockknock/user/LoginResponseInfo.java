package org.techtown.knockknock.user;

import com.google.gson.annotations.SerializedName;

public class LoginResponseInfo {

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("memberId")
    private String memberId;

    public String getNickname() {
        return nickname;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
}
