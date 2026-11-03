package com.nextgenius.snapfix.Model;

import com.google.gson.annotations.SerializedName;

public class SendWinnerEmailRequest {

    @SerializedName("email")
    private String email;

    @SerializedName("rank")
    private int rank;

    public SendWinnerEmailRequest(
            String email,
            int rank
    ) {
        this.email = email;
        this.rank = rank;
    }

    public String getEmail() {
        return email;
    }

    public int getRank() {
        return rank;
    }
}