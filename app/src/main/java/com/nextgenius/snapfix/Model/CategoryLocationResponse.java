package com.nextgenius.snapfix.Model;

import java.util.List;

public class CategoryLocationResponse {

    public String issue_category;
    public String issue_location;
    public int total_issues;

    public List<CategoryLocationIssue> issues;
}