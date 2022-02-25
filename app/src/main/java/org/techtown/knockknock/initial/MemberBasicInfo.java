package org.techtown.knockknock.initial;

import com.google.gson.annotations.SerializedName;

public class MemberBasicInfo {

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("memberId")
    private String memberId;

    @SerializedName("point")
    private int point;

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

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
