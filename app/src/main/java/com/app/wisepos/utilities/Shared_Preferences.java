package com.app.wisepos.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.wisepos.datamodels.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class Shared_Preferences {

    public static void saveCartProducts(Context context, List<Product> orderList) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.app.wisepos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String store = gson.toJson(orderList);
        editor.putString("Order Items", store);
        editor.commit();
    }

    public static List<Product> getCartProducts(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.app.wisepos", Context.MODE_PRIVATE);

        if(sharedPreferences.getString("Order Items", null) != null) {
            String store = sharedPreferences.getString("Order Items", null);
            TypeToken<List<Product>> token = new TypeToken<List<Product>>() {};
            Gson gson = new Gson();
            List<Product> orderList = gson.fromJson(store, token.getType());

            return orderList;
        }
        return null;
    }

    public static void clearCartProducts(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.app.wisepos", Context.MODE_PRIVATE);

        if(sharedPreferences.getString("Order Items", null) != null) {
            sharedPreferences.edit().remove("Order Items").commit();
        }
    }

    public static void saveReaderID(Context context, String readerID) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.app.wisepos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Reader ID", readerID);
        editor.commit();
    }

    public static String getReaderID(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.app.wisepos", Context.MODE_PRIVATE);

        if(sharedPreferences.getString("Reader ID", null) != null) {
            String readerID = sharedPreferences.getString("Reader ID", null);

            return readerID;
        }
        return null;
    }
}
