package com.nextgenius.snapfix.Model;


public class MapIssue {

    private final Issue issue;
    private final String severity;

    public MapIssue(Issue issue, String severity) {
        this.issue = issue;
        this.severity = severity;
    }

    public Issue getIssue() {
        return issue;
    }

    public String getSeverity() {
        return severity;
    }
}
