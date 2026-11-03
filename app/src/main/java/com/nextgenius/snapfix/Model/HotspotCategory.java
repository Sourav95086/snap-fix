package com.nextgenius.snapfix.Model;

import com.google.gson.annotations.SerializedName;

public class HotspotCategory {

    @SerializedName("category_id")
    private int categoryId;

    @SerializedName("category_name")
    private String categoryName;

    @SerializedName("city")
    private String city;

    @SerializedName("report_count")
    private int reportCount;

    @SerializedName("average_weight")
    private double averageWeight;

    @SerializedName("hotspot_score")
    private double hotspotScore;

    @SerializedName("class")
    private String hotspotClass;


    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCity() {
        return city;
    }

    public int getReportCount() {
        return reportCount;
    }

    public double getAverageWeight() {
        return averageWeight;
    }

    public double getHotspotScore() {
        return hotspotScore;
    }

    public String getHotspotClass() {
        return hotspotClass;
    }
}
