package com.nextgenius.snapfix.Model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PriorityResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("city")
    private String city;

    @SerializedName("total_records")
    private int totalRecords;

    @SerializedName("data")
    private List<PriorityItem> data;


    public boolean isSuccess() {
        return success;
    }

    public String getCity() {
        return city;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public List<PriorityItem> getData() {
        return data;
    }
}
