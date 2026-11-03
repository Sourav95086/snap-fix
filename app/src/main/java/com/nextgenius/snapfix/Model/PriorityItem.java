package com.nextgenius.snapfix.Model;


import com.google.gson.annotations.SerializedName;

public class PriorityItem {

    @SerializedName("id")
    private int id;

    @SerializedName("issue_id")
    private int issueId;

    @SerializedName("priority_score")
    private double priorityScore;

    @SerializedName("priority_level")
    private String priorityLevel;

    @SerializedName("priority_data")
    private PriorityData priorityData;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("city")
    private String city;


    public int getId() {
        return id;
    }

    public int getIssueId() {
        return issueId;
    }

    public double getPriorityScore() {
        return priorityScore;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public PriorityData getPriorityData() {
        return priorityData;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getCity() {
        return city;
    }
}