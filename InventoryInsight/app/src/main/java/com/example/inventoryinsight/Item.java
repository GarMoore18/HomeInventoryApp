package com.example.inventoryinsight;

public class Item {
    private int id;
    private String barcode;
    private String name;
    private String image;
    // TODO: WILL NEED TO HAVE AN IMAGE

    public Item(int id, String barcode, String name, String image) {
        this.id = id;
        this.barcode = barcode;
        this.name = name;
        this.image = image;
    }

    public int getId() {
        return id;
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
}
