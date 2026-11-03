package com.nextgenius.snapfix.utils;



import android.graphics.Color;

public class HotspotColors {

    public static int getColor(String hotspotClass) {

        if (hotspotClass == null) {
            return Color.GREEN;
        }

        switch (hotspotClass.toLowerCase()) {

            case "hotspot":
                return Color.rgb(239, 68, 68);      // RED

            case "moderate":
                return Color.rgb(250, 204, 21);     // YELLOW

            case "low":
                return Color.rgb(34, 197, 94);      // GREEN

            default:
                return Color.WHITE;
        }
    }
}