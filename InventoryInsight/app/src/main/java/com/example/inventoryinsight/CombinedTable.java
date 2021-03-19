package com.example.inventoryinsight;

public class CombinedTable {
    private int id;
    private int item_id;
    private int location_id;
    private int user_id;
    private int quantity;

    public CombinedTable(int id, int item_id, int location_id, int user_id, int quantity) {
        this.id = id;
        this.item_id = item_id;
        this.location_id = location_id;
        this.user_id = user_id;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public int getItem_id() {
        return item_id;
    }

    public int getLocation_id() {
        return location_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getQuantity() {
        return quantity;
    }
}
