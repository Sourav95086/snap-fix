package com.nextgenius.snapfix;

import java.util.List;

public class UserIssuesResponse {

    private boolean success;
    private String email;
    private int total_reports;
    private List<UserIssue> issues;

    public boolean isSuccess() {
        return success;
    }

    public String getEmail() {
        return email;
    }

    public int getTotalReports() {
        return total_reports;
    }

    public List<UserIssue> getIssues() {
        return issues;
    }
}