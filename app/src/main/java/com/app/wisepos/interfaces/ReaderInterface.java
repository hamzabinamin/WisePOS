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
    @POST("set-reader-display.php")
    Call<JsonObject> setReaderDisplay(@Field("readerID") String readerID, @Field("total") int total, @Field("items") JSONObject items);

    @FormUrlEncoded
    @POST("deleteItem.php")
    Call<JsonObject> deleteItem(@Field("itemID") String itemID, @Field("imageURL") String imageURL);

    @POST("getItems.php")
    Call<JsonObject> getItems();
}
