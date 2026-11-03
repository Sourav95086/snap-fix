package com.nextgenius.snapfix.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SolutionPriorityResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("city")
    private String city;

    @SerializedName("count")
    private int count;

    @SerializedName("solutions")
    private List<SolutionPriority> solutions;


    public boolean isSuccess() {
        return success;
    }


    public String getCity() {
        return city;
    }


    public int getCount() {
        return count;
    }


    public List<SolutionPriority> getSolutions() {
        return solutions;
    }
}