package com.nextgenius.snapfix;

import java.util.List;

public class PreviousContestResponse {

    private String issue_location;
    private int total_issues;
    private int total_unique_reporters;
    private List<PreviousContestWinner> top_reporters;

    public String getIssueLocation() {
        return issue_location;
    }

    public int getTotalIssues() {
        return total_issues;
    }

    public int getTotalUniqueReporters() {
        return total_unique_reporters;
    }

    public List<PreviousContestWinner> getTopReporters() {
        return top_reporters;
    }
}