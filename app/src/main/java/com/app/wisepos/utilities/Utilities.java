package com.app.wisepos.utilities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.app.wisepos.R;
import com.app.wisepos.datamodels.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

public class Utilities {

    public static ProgressDialog setupProgressDialog(ProgressDialog progressDialog) {
        progressDialog.setMessage("Please Wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        return progressDialog;
    }

    public static void showAlert(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton(context.getString(R.string.ok_alert),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static String[] getPermissions() {
        final String[] PERMISSIONS = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
        return PERMISSIONS;
    }

    public static Float calculateOrderTotal(List<Product> cartList) {
        float total = 0F;
        for(Product item: cartList) {
            total = total + (item.getPrice() * item.getQuantity());
        }
        return total;
    }

    public static JSONObject convertCatalogListIntoJsonArray(List<Product> catalogList) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();

            for (int i = 0; i < catalogList.size(); i++) {
                Product product = catalogList.get(i);
                JSONObject json = new JSONObject();

                json.put("description", product.getName() + '-' + product.getDescription());
                json.put("amount", product.getPriceInCents());
                json.put("quantity", product.getQuantity());

                jsonArray.put(json);
            }

            jsonObject.put("Products", jsonArray);
            System.out.println("Json Object: " + jsonObject);
        }
        catch(JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;

    }

    // Callbacks

    public interface CatalogCallback {
        void onResult(String message);

        void onResult(String message, String productID, String pictureURL);

        void onResult(String message, List<Product> catalogList);
    }

    public interface ReaderCallback {
        void onResult(String message);
    }

}
