package com.nextgenius.snapfix.Model;

import com.google.gson.annotations.SerializedName;

public class PopulationMetadata {

    @SerializedName("ward_area_sq_km")
    private double wardAreaSqKm;

    @SerializedName("total_population")
    private double totalPopulation;


    public double getWardAreaSqKm() {
        return wardAreaSqKm;
    }

    public double getTotalPopulation() {
        return totalPopulation;
    }
}