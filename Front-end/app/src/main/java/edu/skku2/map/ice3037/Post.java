package edu.skku2.map.ice3037;

import com.google.gson.annotations.SerializedName;

public class Post {
    @SerializedName("message")
    private String message;
    @SerializedName("success")
    private Boolean success;

    public Post(){
        //
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}

