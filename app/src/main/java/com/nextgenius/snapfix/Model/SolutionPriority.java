package com.nextgenius.snapfix.Model;

import com.google.gson.annotations.SerializedName;

public class SolutionPriority {

    @SerializedName("id")
    private String id;

    @SerializedName("solution_name")
    private String solutionName;

    @SerializedName("city")
    private String city;

    @SerializedName("ward_number")
    private int wardNumber;

    @SerializedName("solution_priority")
    private double solutionPriority;


    public String getId() {
        return id;
    }


    public String getSolutionName() {
        return solutionName;
    }


    public String getCity() {
        return city;
    }


    public int getWardNumber() {
        return wardNumber;
    }


    public double getSolutionPriority() {
        return solutionPriority;
    }
}