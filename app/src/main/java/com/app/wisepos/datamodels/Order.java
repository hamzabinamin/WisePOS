package com.app.wisepos.datamodels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Order {

    String id;
    Date date;
    List<Product> itemsList;
    Float total;

    Order() {
        id = "";
        date = new Date();
        itemsList = new ArrayList<>();
        total = 0F;
    }

    public Order(String id, Date date, List<Product> itemsList, Float total) {
        this.id = id;
        this.date = date;
        this.itemsList = itemsList;
        this.total = total;
    }

    public String getID() {
        return id;
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
        return  total;
    }
}
