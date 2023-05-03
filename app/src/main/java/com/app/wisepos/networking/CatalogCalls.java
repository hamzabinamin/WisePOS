package com.app.wisepos.networking;
import com.app.wisepos.datamodels.Product;
import com.app.wisepos.interfaces.CatalogInterface;
import com.app.wisepos.utilities.Utilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CatalogCalls {

    public static void addItem(Product product, File image, Utilities.CatalogCallback callback) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://161.35.209.115/app-scripts/item/addItem.php/")
                .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = builder.build();

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), image);

        MultipartBody.Part fileToSend = MultipartBody.Part.createFormData("image", image.getName(), requestBody);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), product.getName());
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), product.getDescription());

        System.out.println("File Name: " + image.getName());

        CatalogInterface catalogInterface = retrofit.create(CatalogInterface.class);
        Call<JsonObject> call = catalogInterface.addItem(name, description, product.getPrice(), fileToSend);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                System.out.println("Response: " + response.body().toString());
                String status = response.body().getAsJsonPrimitive("status").getAsString();

                if (status.equals("success")) {
                    String productID = response.body().getAsJsonPrimitive("product-id").getAsString();
                    String pictureURL = response.body().getAsJsonPrimitive("picture-url").getAsString();
                    callback.onResult(status, productID, pictureURL);
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

    public static void updateItem(Product product, Utilities.CatalogCallback callback) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://161.35.209.115/app-scripts/item/updateItem.php/")
                .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = builder.build();

        CatalogInterface catalogInterface = retrofit.create(CatalogInterface.class);
        Call<JsonObject> call = catalogInterface.updateItem(product.getID(), product.getName(), product.getDescription(), product.getPrice());
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

    public static void deleteItem(Product product, Utilities.CatalogCallback callback) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://161.35.209.115/app-scripts/item/deleteItem.php/")
                .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = builder.build();

        CatalogInterface catalogInterface = retrofit.create(CatalogInterface.class);
        Call<JsonObject> call = catalogInterface.deleteItem(product.getID(), product.getPictureURL());
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

    public static void getCatalog(Utilities.CatalogCallback callback) {

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
    }


}