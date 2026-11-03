package com.nextgenius.snapfix.Model;

import com.google.gson.annotations.SerializedName;

public class PriorityData {

    @SerializedName("city")
    private String city;

    @SerializedName("hotspot")
    private HotspotData hotspot;

    @SerializedName("reasoning")
    private String reasoning;

    @SerializedName("coordinates")
    private Coordinates coordinates;

    @SerializedName("issue_count")
    private int issueCount;

    @SerializedName("key_factors")
    private String[] keyFactors;

    @SerializedName("ward_number")
    private int wardNumber;

    @SerializedName("issue_category")
    private String issueCategory;

    @SerializedName("demographic_data")
    private DemographicData demographicData;


    public String getCity() {
        return city;
    }

    public HotspotData getHotspot() {
        return hotspot;
    }

    public String getReasoning() {
        return reasoning;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public int getIssueCount() {
        return issueCount;
    }

    public String[] getKeyFactors() {
        return keyFactors;
    }

    public int getWardNumber() {
        return wardNumber;
    }

    public String getIssueCategory() {
        return issueCategory;
    }

    public DemographicData getDemographicData() {
        return demographicData;
    }
}