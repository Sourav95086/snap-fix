package com.nextgenius.snapfix.Model;

import com.google.gson.annotations.SerializedName;

public class Issue {

    @SerializedName("issue_id")
    private int issueId;

    @SerializedName("issue_description")
    private String issueDescription;

    @SerializedName("issue_location")
    private String issueLocation;

    @SerializedName("category_id")
    private int categoryId;

    @SerializedName("category_name")
    private String categoryName;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("issue_weight")
    private double issueWeight;


    public int getIssueId() {
        return issueId;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public String getIssueLocation() {
        return issueLocation;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getIssueWeight() {
        return issueWeight;
    }
}
