package com.example.inventoryinsight;

public class Location {

    private Integer id;
    private String location;

    public Location(Integer id, String location) {
        this.id = id;
        this.location = location;
    }

    public Integer getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }
}
