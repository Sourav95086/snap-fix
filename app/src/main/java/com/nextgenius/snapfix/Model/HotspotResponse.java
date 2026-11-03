package com.nextgenius.snapfix.Model;

import java.util.List;

public class HotspotResponse {

    private boolean success;
    private String city;
    private List<HotspotCategory> categories;


    public boolean isSuccess() {
        return success;
    }

    public String getCity() {
        return city;
    }

    public List<HotspotCategory> getCategories() {
        return categories;
    }
}
