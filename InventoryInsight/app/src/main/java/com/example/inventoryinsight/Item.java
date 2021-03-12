package com.example.inventoryinsight;

public class Item {
    private int id;
    private String barcode;
    private String name;
    // TODO: WILL NEED TO HAVE AN IMAGE

    public Item(int id, String barcode, String name) {
        this.id = id;
        this.barcode = barcode;
        this.name = name;
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
}
