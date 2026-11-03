package com.nextgenius.snapfix.Model;

import java.util.List;

public class IssueResponse {

    private boolean success;
    private String city;
    private int category_id;
    private String category_name;
    private int total_issues;
    private List<Issue> issues;


    public boolean isSuccess() {
        return success;
    }

    public String getCity() {
        return city;
    }

    public int getCategory_id() {
        return category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public int getTotal_issues() {
        return total_issues;
    }

    public List<Issue> getIssues() {
        return issues;
    }
}
