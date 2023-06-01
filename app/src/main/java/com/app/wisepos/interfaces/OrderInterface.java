package com.app.wisepos.interfaces;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface OrderInterface {


    @FormUrlEncoded
    @POST("createOrder.php")
    Call<JsonObject> createOrder(@Field("total") float total, @Field("paymentIntentID") String paymentIntentID, @Field("items") JSONObject items);

    @FormUrlEncoded
    @POST("createOrderSimple.php")
    Call<JsonObject> createOrderSimple(@Field("total") float total, @Field("paymentIntentID") String paymentIntentID);

    @FormUrlEncoded
    @POST("deleteOrder.php")
    Call<JsonObject> deleteOrder(@Field("orderID") String orderID);

    @POST("getOrders.php")
    Call<JsonObject> getOrders();
}
