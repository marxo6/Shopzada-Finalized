package com.utcc.shopzada.Models;

public class ProductModel {

    private String description, id, image, name, owner;
    private int amount, price, rating, views;

    public ProductModel() {

    }

    public ProductModel(String description, String id, String image, String name, String owner, int amount, int price, int rating, int views) {
        this.description = description;
        this.id = id;
        this.image = image;
        this.name = name;
        this.owner = owner;
        this.amount = amount;
        this.price = price;
        this.rating = rating;
        this.views = views;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

}
