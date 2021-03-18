package com.example.inventoryinsight;

public class Recents {
    private String barcode;
    private String name;
    private String image;
    private String location;
    private String username;
    private String quantity;
    private String last_modified;

    public Recents(String barcode, String name, String image, String location, String username, String quantity, String last_modified) {
        this.barcode = barcode;
        this.name = name;
        this.image = image;
        this.location = location;
        this.username = username;
        this.quantity = quantity;
        this.last_modified = last_modified;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getLocation() {
        return location;
    }

    public String getUsername() {
        return username;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getLast_modified() {
        return last_modified;
    }
}
