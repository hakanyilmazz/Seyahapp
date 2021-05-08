package com.hakanyilmazz.seyahapp.model;

import java.io.Serializable;

public class Place implements Serializable {

    private String name;
    private double latitude;
    private double longitude;

    public Place(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
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

}
