package org.techtown.knockknock;

import com.google.gson.annotations.SerializedName;

public class ErrorBody {

    @SerializedName("errorCode")
    private String code;

    @SerializedName("errorMessage")
    private String message;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
