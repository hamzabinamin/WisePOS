package com.app.wisepos.interfaces;

import com.google.gson.JsonObject;

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
    @POST("deleteOrder.php")
    Call<JsonObject> deleteOrder(@Field("orderID") String orderID);

    @POST("getOrders.php")
    Call<JsonObject> getOrders();
}
