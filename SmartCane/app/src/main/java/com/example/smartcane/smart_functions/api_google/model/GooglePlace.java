package com.example.smartcane.smart_functions.api_google.model;

import java.io.Serializable;

public class GooglePlace implements Serializable {

    private String name;
    private String category;
    private String rating;
    private String open;
    private String vicinity;

    public GooglePlace() {
        this.name = "";
        this.rating = "";
        this.open = "";
        this.setVicinity("");
        this.setCategory("");
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRating() {
        return rating;
    }

    public void setOpenNow(String open) {
        this.open = open;
    }

    public String getOpenNow() {
        return open;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }
}

