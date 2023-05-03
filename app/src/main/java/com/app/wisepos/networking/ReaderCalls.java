package com.app.wisepos.networking;

import com.app.wisepos.datamodels.Product;
import com.app.wisepos.interfaces.CatalogInterface;
import com.app.wisepos.interfaces.ReaderInterface;
import com.app.wisepos.utilities.Utilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReaderCalls {

    public static void displayItemsOnReader(String readerID, int total, JSONObject items, Utilities.ReaderCallback callback) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://161.35.209.115/scripts/set-reader-display.php/")
                .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = builder.build();

        ReaderInterface readerInterface = retrofit.create(ReaderInterface.class);
        Call<JsonObject> call = readerInterface.setReaderDisplay(readerID, total, items);
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

}
