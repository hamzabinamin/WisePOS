package com.app.wisepos.datamodels;

import java.io.Serializable;
import java.util.Random;

public class Product implements Serializable {
    String id;
    String name;
    String description;
    int quantity;
    Float price;
    Float usdRate;
    String pictureURL;

    public Product() {
        this.id = "";
        this.name = "";
        this.description = "";
        this.quantity = 0;
        this.price = Float.valueOf(0);
        this.usdRate = 0.91F;
        this.pictureURL = "";
    }

    public Product(String id, String name, String description, Float price, String pictureURL) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = 0;
        this.price = price;
        this.pictureURL = pictureURL;
    }

    public Product(String id, String name, String description, int quantity, Float price, String pictureURL) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.pictureURL = pictureURL;
    }

    public Product(String id, String name, String description, Float price, Float usdRate, String pictureURL) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = 0;
        this.price = price;
        this.usdRate = usdRate;
        this.pictureURL = pictureURL;
    }

    public Product(String name, String description, Float price, String pictureURL) {
        this.name = name;
        this.description = description;
        this.quantity = 0;
        this.price = price;
        this.pictureURL = pictureURL;
    }

    public Product(String name, String description, Float price) {
        this.name = name;
        this.description = description;
        this.quantity = 0;
        this.price = price;
    }

    public Product(String name, String description, Float price, Float usdRate) {
        this.name = name;
        this.description = description;
        this.quantity = 0;
        this.price = price;
        this.usdRate = usdRate;
    }

    public void setID(String id) { this.id = id; }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public String getID() { return id; }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getQuantity() { return quantity; }

    public Float getPrice() {
        return price;
    }

    public Float getUSDRate() {
        return usdRate;
    }

    public Float getPriceInCents() {
        return price * 100;
    }

    public Float getUSDPrice() {
        return ((1/usdRate) * price);
    }

    public String getPictureURL() {
        return pictureURL;
    }

    @Override
    public boolean equals(Object v) {
        boolean retVal = false;

        if (v instanceof Product){
            Product ptr = (Product) v;
            retVal = ptr.getID().equals(this.getID());
        }
        return retVal;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        Random r = new Random();
        hash = 17 * hash + r.nextInt(10000);
        return hash;
    }
}
