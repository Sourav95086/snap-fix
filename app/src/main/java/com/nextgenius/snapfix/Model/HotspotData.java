package com.nextgenius.snapfix.Model;


import com.google.gson.annotations.SerializedName;

public class HotspotData {

    @SerializedName("class")
    private String hotspotClass;

    @SerializedName("report_count")
    private int reportCount;

    @SerializedName("hotspot_score")
    private double hotspotScore;

    @SerializedName("average_weight")
    private double averageWeight;


    public String getHotspotClass() {
        return hotspotClass;
    }

    public int getReportCount() {
        return reportCount;
    }

    public double getHotspotScore() {
        return hotspotScore;
    }

    public double getAverageWeight() {
        return averageWeight;
    }
}
