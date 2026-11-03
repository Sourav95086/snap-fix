package com.nextgenius.snapfix.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TopReportersResponse {

    @SerializedName("issue_location")
    private String issueLocation;

    @SerializedName("total_issues")
    private int totalIssues;

    @SerializedName("total_unique_reporters")
    private int totalUniqueReporters;

    @SerializedName("top_reporters")
    private List<ContestReporter> topReporters;

    public String getIssueLocation() {
        return issueLocation;
    }

    public int getTotalIssues() {
        return totalIssues;
    }

    public int getTotalUniqueReporters() {
        return totalUniqueReporters;
    }

    public List<ContestReporter> getTopReporters() {
        return topReporters;
    }
}