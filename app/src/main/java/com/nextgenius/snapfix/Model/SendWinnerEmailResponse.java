package com.nextgenius.snapfix.Model;

import com.google.gson.annotations.SerializedName;

public class SendWinnerEmailResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("email")
    private String email;

    @SerializedName("rank")
    private int rank;

    @SerializedName("reward")
    private String reward;

    @SerializedName("email_id")
    private String emailId;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }

    public int getRank() {
        return rank;
    }

    public String getReward() {
        return reward;
    }

    public String getEmailId() {
        return emailId;
    }
}