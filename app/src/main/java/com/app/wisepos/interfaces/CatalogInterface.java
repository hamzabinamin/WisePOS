package com.app.wisepos.interfaces;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CatalogInterface {

    @POST("addItem.php")
    @Multipart
    Call<JsonObject> addItem(@Part("name") RequestBody name, @Part("description") RequestBody description, @Part("price") Float price, @Part MultipartBody.Part image);

    @FormUrlEncoded
    @POST("updateItem.php")
    Call<JsonObject> updateItem(@Field("itemID") String itemID, @Field("name") String name, @Field("description") String description, @Field("price") Float price);

    @FormUrlEncoded
    @POST("deleteItem.php")
    Call<JsonObject> deleteItem(@Field("itemID") String itemID, @Field("imageURL") String imageURL);

    @POST("getItems.php")
    Call<JsonObject> getItems();
}
