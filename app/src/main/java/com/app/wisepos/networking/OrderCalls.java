package com.app.wisepos.networking;

import com.app.wisepos.datamodels.Order;
import com.app.wisepos.datamodels.Product;
import com.app.wisepos.interfaces.CatalogInterface;
import com.app.wisepos.interfaces.OrderInterface;
import com.app.wisepos.interfaces.ReaderInterface;
import com.app.wisepos.utilities.Utilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderCalls {

    public static void createOrder(float total, String paymentIntentID, JSONObject items, Utilities.OrderCallback callback) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://www.pantheracomics.com/app-scripts/order/createOrder.php/")
                .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = builder.build();

        OrderInterface orderInterface = retrofit.create(OrderInterface.class);
        Call<JsonObject> call = orderInterface.createOrder(total, paymentIntentID, items);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                System.out.println("Response: " + response.body().toString());
                String status = response.body().getAsJsonPrimitive("status").getAsString();

                if (status.equals("success")) {
                    callback.onResult(status);
                }
                else {
                    String description = response.body().getAsJsonPrimitive("description").getAsString();
                    callback.onResult(description);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println("Failed: " + t.getMessage());
                callback.onResult(t.getMessage());
            }
        });
    }

    public static void createOrderSimple(float total, String paymentIntentID, Utilities.OrderCallback callback) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://www.pantheracomics.com/app-scripts/order/createOrderSimple.php/")
                .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = builder.build();

        OrderInterface orderInterface = retrofit.create(OrderInterface.class);
        Call<JsonObject> call = orderInterface.createOrderSimple(total, paymentIntentID);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                System.out.println("Response: " + response.body().toString());
                String status = response.body().getAsJsonPrimitive("status").getAsString();

                if (status.equals("success")) {
                    callback.onResult(status);
                }
                else {
                    String description = response.body().getAsJsonPrimitive("description").getAsString();
                    callback.onResult(description);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println("Failed: " + t.getMessage());
                callback.onResult(t.getMessage());
            }
        });
    }

    public static void getOrders(Utilities.OrderCallback callback) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://www.pantheracomics.com/app-scripts/order/getOrders.php/")
                .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = builder.build();

        OrderInterface orderInterface = retrofit.create(OrderInterface.class);
        Call<JsonObject> call = orderInterface.getOrders();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                System.out.println("Response: " + response.body().toString());
                String status = response.body().getAsJsonPrimitive("status").getAsString();

                if (status.equals("success")) {
                    JsonArray orders = response.body().getAsJsonArray("orders");
                    Float usdRate = response.body().getAsJsonPrimitive("usd-rate").getAsFloat();
                    List<Order> orderList = new ArrayList<>();
                    List<Product> itemsList = new ArrayList<>();

                    for(int i = 0; i < orders.size(); i++) {
                        String orderID = orders.get(i).getAsJsonObject().get("order_id").getAsString();
                        String paymentIntentID = orders.get(i).getAsJsonObject().get("payment_id").getAsString();
                        String date = orders.get(i).getAsJsonObject().get("date").getAsString();
                        Float total = orders.get(i).getAsJsonObject().get("total").getAsFloat();
                        JsonArray items = orders.get(i).getAsJsonObject().get("items").getAsJsonArray();

                        for(int j = 0; j < items.size(); j++) {
                            String productID = items.get(j).getAsJsonObject().get("item_id").getAsString();
                            String productName = items.get(j).getAsJsonObject().get("name").getAsString();
                            String productDescription = items.get(j).getAsJsonObject().get("description").getAsString();
                            Float productPrice = items.get(j).getAsJsonObject().get("price").getAsFloat();
                            String productImage = items.get(j).getAsJsonObject().get("image").getAsString();
                            int productQuantity = items.get(j).getAsJsonObject().get("quantity").getAsInt();

                            itemsList.add(new Product(productID, productName, productDescription, productQuantity, productPrice, productImage));
                        }
                        Order order = new Order(orderID, paymentIntentID, Utilities.convertStringToDate(date), itemsList, total, usdRate);
                        orderList.add(order);
                        itemsList.clear();
                    }
                    callback.onResult(status, orderList);
                }
                else {
                    callback.onResult(status);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println("Failed: " + t.getMessage());
                callback.onResult(t.getMessage());
            }
        });
    }

    public static void deleteOrder(Order order, Utilities.CatalogCallback callback) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://www.pantheracomics.com/app-scripts/order/deleteOrder.php/")
                .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = builder.build();

        OrderInterface orderInterface = retrofit.create(OrderInterface.class);
        Call<JsonObject> call = orderInterface.deleteOrder(order.getID());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                System.out.println("Response: " + response.body().toString());
                String status = response.body().getAsJsonPrimitive("status").getAsString();

                if (status.equals("success")) {
                    callback.onResult(status);
                }
                else {
                    String error = response.body().getAsJsonPrimitive("error").getAsString();
                    callback.onResult(error);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println("Failed: " + t.getMessage());
                callback.onResult(t.getMessage());
            }
        });
    }
}
