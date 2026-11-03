package com.nextgenius.snapfix.Model;

import com.google.gson.annotations.SerializedName;

public class ContestReport {

    @SerializedName("report_id")
    private int reportId;

    @SerializedName("issue_id")
    private int issueId;

    @SerializedName("description")
    private String description;

    @SerializedName("reported_on")
    private String reportedOn;

    @SerializedName("location")
    private String location;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("evidence")
    private String evidence;

    public int getReportId() {
        return reportId;
    }

    public int getIssueId() {
        return issueId;
    }

    public String getDescription() {
        return description;
    }

    public String getReportedOn() {
        return reportedOn;
    }

    public String getLocation() {
        return location;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getEvidence() {
        return evidence;
    }
}