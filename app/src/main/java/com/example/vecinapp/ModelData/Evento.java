package com.example.vecinapp.ModelData;

import org.osmdroid.util.GeoPoint;

public class Evento {

    private String imagePath;
    public String description;
    public String title;
    public String userName;
    public String userLastName;
    public String category;
    public GeoPoint direction;
    public String comunity;

    public Evento(String imagePath, String description) {
        this.imagePath = imagePath;
        this.description = description;
    }

    public Evento() {}

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



}
