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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderCalls {

    public static void deleteOrder(Order order, Utilities.CatalogCallback callback) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://161.35.209.115/app-scripts/order/deleteOrder.php/")
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

   /* public static void getCOrders(Utilities.CatalogCallback callback) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://161.35.209.115/app-scripts/item/getItems.php/")
                .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = builder.build();

        CatalogInterface catalogInterface = retrofit.create(CatalogInterface.class);
        Call<JsonObject> call = catalogInterface.getItems();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                System.out.println("Response: " + response.body().toString());
                String status = response.body().getAsJsonPrimitive("status").getAsString();

                if (status.equals("success")) {
                    JsonArray productsArray = response.body().getAsJsonArray("items");
                    List<Product> catalogList = new ArrayList<>();

                    for(int i = 0; i < productsArray.size(); i++) {
                        String productID = productsArray.get(i).getAsJsonObject().get("ID").getAsString();
                        String productName = productsArray.get(i).getAsJsonObject().get("Name").getAsString();
                        String productDescription = productsArray.get(i).getAsJsonObject().get("Description").getAsString();
                        Float productPrice = productsArray.get(i).getAsJsonObject().get("Price").getAsFloat();
                        Float usdRate = response.body().getAsJsonPrimitive("usd-rate").getAsFloat();
                        String productImage = productsArray.get(i).getAsJsonObject().get("Image").getAsString();
                        System.out.println("Product ID: " + productID);
                        System.out.println("Product Name: " + productName);
                        System.out.println("Product Description: " + productDescription);
                        System.out.println("Product Image: " + productImage);
                        catalogList.add(new Product(productID, productName, productDescription, productPrice, usdRate, productImage));
                    }
                    callback.onResult(status, catalogList);
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
    } */

}
