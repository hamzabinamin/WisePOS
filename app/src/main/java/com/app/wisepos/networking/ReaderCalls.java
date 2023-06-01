package com.app.wisepos.networking;

import com.app.wisepos.interfaces.ReaderInterface;
import com.app.wisepos.utilities.Utilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReaderCalls {

    public static void createPaymentIntent(int amount, Utilities.ReaderCallback callback) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://www.pantheracomics.com/scripts/stripe/create-payment-intent.php/")
                .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = builder.build();

        ReaderInterface readerInterface = retrofit.create(ReaderInterface.class);
        Call<JsonObject> call = readerInterface.createPaymentIntent(amount);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                System.out.println("Response: " + response.body().toString());
                String status = response.body().getAsJsonPrimitive("status").getAsString();

                if (status.equals("success")) {
                    JsonObject paymentIntent = response.body().getAsJsonObject("payment-intent");
                    callback.onResult(status, paymentIntent);
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

    public static void displayItemsOnReader(String readerID, int total, JSONObject items, Utilities.ReaderCallback callback) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://www.pantheracomics.com/scripts/stripe/set-reader-display.php/")
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

    public static void displayItemsOnReaderSimple(String readerID, int total, Utilities.ReaderCallback callback) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://www.pantheracomics.com/scripts/stripe/set-reader-display-simple.php/")
                .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = builder.build();

        ReaderInterface readerInterface = retrofit.create(ReaderInterface.class);
        Call<JsonObject> call = readerInterface.setReaderDisplaySimple(readerID, total);
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

    public static void processPaymentIntent(String readerID, String paymentIntentID, Utilities.ReaderCallback callback) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://www.pantheracomics.com/scripts/stripe/process-payment-intent.php/")
                .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = builder.build();

        ReaderInterface readerInterface = retrofit.create(ReaderInterface.class);
        Call<JsonObject> call = readerInterface.processPaymentIntent(readerID, paymentIntentID);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                System.out.println("Response: " + response.body().toString());
                String status = response.body().getAsJsonPrimitive("status").getAsString();

                if (status.equals("success")) {
                    JsonObject result = response.body().getAsJsonObject("result");
                    callback.onResult(status, result);
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

    public static void cancelPaymentIntent(String readerID, Utilities.ReaderCallback callback) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://www.pantheracomics.com/scripts/stripe/cancel-payment-intent.php/")
                .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = builder.build();

        ReaderInterface readerInterface = retrofit.create(ReaderInterface.class);
        Call<JsonObject> call = readerInterface.cancelPaymentIntent(readerID);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                System.out.println("Response: " + response.body().toString());
                String status = response.body().getAsJsonPrimitive("status").getAsString();

                if (status.equals("success")) {
                    JsonObject result = response.body().getAsJsonObject("result");
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
