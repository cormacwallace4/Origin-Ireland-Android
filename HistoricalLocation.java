package com.cormac.origin;

import java.io.Serializable;
import java.util.List;

public class HistoricalLocation implements Serializable {
    private String name;
    private double latitude;
    private double longitude;
    private String subtitle;
    private String description;
    private List<Integer> imageResourceIds;

    public HistoricalLocation(String name, double latitude, double longitude, String subtitle, String description, List<Integer> imageResourceIds) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.subtitle = subtitle;
        this.description = description;
        this.imageResourceIds = imageResourceIds;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getDescription() {
        return description;
    }

    public List<Integer> getImages() {
        return imageResourceIds;
    }

    // Overriding toString method to return name and subtitle
    @Override
    public String toString() {
        return name + ", " + subtitle;
    }
}
