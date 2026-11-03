package com.nextgenius.snapfix.Model;

import com.google.gson.annotations.SerializedName;

public class DemographicField {

    @SerializedName("field")
    private String field;

    @SerializedName("value")
    private Double value;

    @SerializedName("status")
    private String status;


    public String getField() {
        return field;
    }

    public Double getValue() {
        return value;
    }

    public String getStatus() {
        return status;
    }
}