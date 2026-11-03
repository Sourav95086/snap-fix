package com.nextgenius.snapfix;

public class UserIssue {

    private int report_id;
    private int issue_id;

    private String issue_description;

    private String reported_by_name;
    private String reported_by_phone;
    private String reported_by_email;

    private String reported_on;

    private String evidence;

    private String issue_location;

    private double latitude;
    private double longitude;

    public int getReportId() {
        return report_id;
    }

    public int getIssueId() {
        return issue_id;
    }

    public String getIssueDescription() {
        return issue_description;
    }

    public String getReportedByName() {
        return reported_by_name;
    }

    public String getReportedByPhone() {
        return reported_by_phone;
    }

    public String getReportedByEmail() {
        return reported_by_email;
    }

    public String getReportedOn() {
        return reported_on;
    }

    public String getEvidence() {
        return evidence;
    }

    public String getIssueLocation() {
        return issue_location;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}