package com.app.wisepos.interfaces;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ReaderInterface {

    @FormUrlEncoded
    @POST("create-payment-intent.php")
    Call<JsonObject> createPaymentIntent(@Field("amount") int amount);

    @FormUrlEncoded
    @POST("set-reader-display.php")
    Call<JsonObject> setReaderDisplay(@Field("readerID") String readerID, @Field("total") int total, @Field("items") JSONObject items);

    @FormUrlEncoded
    @POST("set-reader-display-simple.php")
    Call<JsonObject> setReaderDisplaySimple(@Field("readerID") String readerID, @Field("total") int total);

    @FormUrlEncoded
    @POST("process-payment-intent.php")
    Call<JsonObject> processPaymentIntent(@Field("readerID") String readerID, @Field("paymentIntentID") String paymentIntentID);

    @FormUrlEncoded
    @POST("cancel-payment-intent.php")
    Call<JsonObject> cancelPaymentIntent(@Field("readerID") String readerID);

    @POST("getItems.php")
    Call<JsonObject> getItems();
}
