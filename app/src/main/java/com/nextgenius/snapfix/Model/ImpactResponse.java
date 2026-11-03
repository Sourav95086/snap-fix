package com.nextgenius.snapfix.Model;

public class ImpactResponse {

    private int issue_id;
    private String issue_category;
    private String proposed_intervention;
    private String solution_description;

    private ImpactLocation location;
    private SocialImpact social_impact;
    private EconomicImpact economic_impact;
    private Scenarios scenarios;
    private Confidence confidence;

    public int getIssueId() {
        return issue_id;
    }

    public String getIssueCategory() {
        return issue_category;
    }

    public String getProposedIntervention() {
        return proposed_intervention;
    }

    public String getSolutionDescription() {
        return solution_description;
    }

    public ImpactLocation getLocation() {
        return location;
    }

    public SocialImpact getSocialImpact() {
        return social_impact;
    }

    public EconomicImpact getEconomicImpact() {
        return economic_impact;
    }

    public Scenarios getScenarios() {
        return scenarios;
    }

    public Confidence getConfidence() {
        return confidence;
    }
}