package com.nextgenius.snapfix.Model;

public class EconomicImpact {

    private EconomicRange estimated_cost;
    private EconomicRange estimated_benefit;
    private EconomicRange net_benefit;
    private EconomicRange benefit_cost_ratio;

    public EconomicRange getEstimatedCost() {
        return estimated_cost;
    }

    public EconomicRange getEstimatedBenefit() {
        return estimated_benefit;
    }

    public EconomicRange getNetBenefit() {
        return net_benefit;
    }

    public EconomicRange getBenefitCostRatio() {
        return benefit_cost_ratio;
    }
}