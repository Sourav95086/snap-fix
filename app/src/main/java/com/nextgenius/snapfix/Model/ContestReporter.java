package com.nextgenius.snapfix.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContestReporter {

    @SerializedName("rank")
    private int rank;

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    @SerializedName("report_count")
    private int reportCount;

    @SerializedName("reports")
    private List<ContestReport> reports;

    public int getRank() {
        return rank;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public int getReportCount() {
        return reportCount;
    }

    public List<ContestReport> getReports() {
        return reports;
    }
}