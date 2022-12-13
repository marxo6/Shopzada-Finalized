package com.utcc.shopzada.Models;

public class OrderModel {

    private String address, date, id, owner, shipment, time;
    private int total;

    public OrderModel() {

    }

    public OrderModel(String address, String date, String id, String owner, String shipment, String time, int total) {
        this.address = address;
        this.date = date;
        this.id = id;
        this.owner = owner;
        this.shipment = shipment;
        this.time = time;
        this.total = total;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getShipment() {
        return shipment;
    }

    public void setShipment(String shipment) {
        this.shipment = shipment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
