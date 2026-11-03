package com.nextgenius.snapfix.Model;

import com.google.gson.annotations.SerializedName;

public class DemographicData {

    @SerializedName("population_density")
    private PopulationDensity populationDensity;

    @SerializedName("age_distribution")
    private DemographicField ageDistribution;

    @SerializedName("average_commute_time")
    private DemographicField averageCommuteTime;

    @SerializedName("public_transit_usage")
    private DemographicField publicTransitUsage;

    @SerializedName("vehicle_ownership_per_capita")
    private DemographicField vehicleOwnershipPerCapita;

    @SerializedName("commuting_patterns")
    private DemographicField commutingPatterns;


    public PopulationDensity getPopulationDensity() {
        return populationDensity;
    }

    public DemographicField getAgeDistribution() {
        return ageDistribution;
    }

    public DemographicField getAverageCommuteTime() {
        return averageCommuteTime;
    }

    public DemographicField getPublicTransitUsage() {
        return publicTransitUsage;
    }

    public DemographicField getVehicleOwnershipPerCapita() {
        return vehicleOwnershipPerCapita;
    }

    public DemographicField getCommutingPatterns() {
        return commutingPatterns;
    }
}
