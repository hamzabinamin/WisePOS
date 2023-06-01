package com.app.wisepos.datamodels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Order {

    String id;
    String paymentIntentID;
    Date date;
    List<Product> itemsList = new ArrayList<>();
    Float total;
    Float usdRate;

    Order() {
        id = "";
        paymentIntentID = "";
        date = new Date();
        itemsList = new ArrayList<>();
        total = 0F;
        usdRate = 0.91F;
    }

    public Order(String id, String paymentIntentID, Date date, List<Product> itemsList, Float total, Float usdRate) {
        this.id = id;
        this.paymentIntentID = paymentIntentID;
        this.date = date;
        this.itemsList.addAll(itemsList);
        this.total = total;
        this.usdRate = usdRate;
    }

    public String getID() {
        return id;
    }

    public String getPaymentIntentID() {
        return paymentIntentID;
    }

    public Date getDate() {
        return date;
    }

    public List<Product> getItemsList() {
        return itemsList;
    }

    public Float getTotal() {
        return total;
    }

    public Float getTotalInUSD() {
        return ((1/usdRate) * total);
    }
}
