package com.utcc.shopzada.Models;

public class ReceiptDisplayModel {

    private String amount, name;
    private int price;

    public ReceiptDisplayModel() {

    }

    public ReceiptDisplayModel(String amount, String name, int price) {
        this.amount = amount;
        this.name = name;
        this.price = price;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
