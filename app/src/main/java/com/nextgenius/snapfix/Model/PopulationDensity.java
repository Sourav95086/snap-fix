package com.nextgenius.snapfix.Model;

import com.google.gson.annotations.SerializedName;

public class PopulationDensity {

    @SerializedName("unit")
    private String unit;

    @SerializedName("year")
    private int year;

    @SerializedName("field")
    private String field;

    @SerializedName("value")
    private Double value;

    @SerializedName("source")
    private String source;

    @SerializedName("metadata")
    private PopulationMetadata metadata;


    public String getUnit() {
        return unit;
    }

    public int getYear() {
        return year;
    }

    public String getField() {
        return field;
    }

    public Double getValue() {
        return value;
    }

    public String getSource() {
        return source;
    }

    public PopulationMetadata getMetadata() {
        return metadata;
    }
}