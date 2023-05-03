package com.app.wisepos.ui.catalog;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.wisepos.R;
import com.app.wisepos.adapters.CatalogAdapter;
import com.app.wisepos.databinding.FragmentCatalogBinding;
import com.app.wisepos.datamodels.Product;
import com.app.wisepos.networking.CatalogCalls;
import com.app.wisepos.networking.ReaderCalls;
import com.app.wisepos.utilities.Shared_Preferences;
import com.app.wisepos.utilities.Utilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.internal.Util;
import retrofit2.Retrofit;

public class CatalogFragment extends Fragment implements CatalogAdapter.ItemClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    Button orderDetailsButton;
    TextView noProductsTextView;
    ImageView imageView;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    public CatalogAdapter adapter;
    public ProgressDialog progressDialog;
    public List<Product> catalogList = new ArrayList<Product>();
    private FragmentCatalogBinding binding;
    Bitmap bitmapImage = null;
    String userChosenTask;
    boolean isImageCaptured;
    boolean isImageChosen;
    private static final String TAG = "CatalogFragment";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCatalogBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setupViews(root);
        return root;
    }

    void setupViews(View root) {
        orderDetailsButton = (Button) root.findViewById(R.id.orderDetailsButton);
        noProductsTextView = (TextView) root.findViewById(R.id.noProductsTextView);
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        setupProgressDialog();
        setupSwipeRefreshLayout();
        setupOnClickListeners();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CatalogAdapter(getContext(), CatalogFragment.this, catalogList);
        adapter.setClickListener(this);
        fetchProducts();
        recyclerView.setAdapter(adapter);
    }

    void setupProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog = Utilities.setupProgressDialog(progressDialog);
    }

    void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.purple_500, R.color.purple_200, R.color.purple_500, R.color.purple_700);

       /* swipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                fetchProducts();
            }
        }); */

    }

    void setupOnClickListeners() {
        orderDetailsButton.setOnClickListener(this);
    }

    public void updateViews() {
        if(catalogList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            noProductsTextView.setVisibility(View.INVISIBLE);
        }
        else {
            recyclerView.setVisibility(View.INVISIBLE);
            noProductsTextView.setVisibility(View.VISIBLE);
        }
    }

    public void updateOrderDetailsButton(List<Product> cartList) {
        Float total = Utilities.calculateOrderTotal(cartList);
        orderDetailsButton.setText(getString(R.string.order_details_button) + " " + "(€" + total + ")");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.three_dots_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.add_menu:
                addUpdateDialog(getString(R.string.add), new Product(), -1);
                return true;

            case R.id.clear_menu:
                clearOrder();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private ActivityResultLauncher<String[]> permissionsResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
            public void onActivityResult(Map<String, Boolean> result) {
                android.util.Log.e(TAG, "request multiple permissions onActivityResult");
                for(Map.Entry<String, Boolean> entry : result.entrySet()) {
                   android.util.Log.e(TAG,entry.getKey() + "/" + entry.getValue());

                   if(!entry.getValue()) {

                   }
                }
                if(result.containsValue(false)) {
                    Utilities.showAlert(getContext(), getString(R.string.permissions_error));
                }
                else {
                    if(userChosenTask != null) {
                        if(userChosenTask.equals("Take Photo")) {
                            cameraIntent();
                        }
                        else if(userChosenTask.equals("Choose from Library")) {
                            galleryIntent();
                        }
                    }
                }
            }
    });

    private ActivityResultLauncher <Intent> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                android.util.Log.e(TAG, result.toString());
                if(result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    if(userChosenTask != null) {
                        if(userChosenTask.equals("Take Photo")) {
                            onCaptureImageResult(result.getData());
                        }
                        else if(userChosenTask.equals("Choose from Library")) {
                            onSelectFromGalleryResult(result.getData());
                        }
                    }
                }
            }
    });

    public void addUpdateDialog(String action, Product productBeingUpdated, int position) {
        final Dialog dialog = new Dialog(getContext());
        int width = (int)(getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int)(getResources().getDisplayMetrics().heightPixels * 0.75);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.add_item_dialog);
        dialog.getWindow().setLayout(width, height);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        Button addUpdateButton = (Button) dialog.findViewById(R.id.addUpdateButton);
        EditText nameEditText = (EditText) dialog.findViewById(R.id.nameEditText);
        EditText descriptionEditText = (EditText) dialog.findViewById(R.id.descriptionEditText);
        EditText priceEditText = (EditText) dialog.findViewById(R.id.priceEditText);
        imageView = (ImageView) dialog.findViewById(R.id.imageView);

        if(action.equals(getString(R.string.add))) {
            addUpdateButton.setText(getString(R.string.add));
        }
        else {
            nameEditText.setText(productBeingUpdated.getName());
            descriptionEditText.setText(productBeingUpdated.getDescription());
            priceEditText.setText(String.valueOf(productBeingUpdated.getPrice()));
            Picasso.get().load(productBeingUpdated.getPictureURL()).into(imageView);
            addUpdateButton.setText(getString(R.string.update));
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        addUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();
                Float price = Float.parseFloat(priceEditText.getText().toString().trim());

                System.out.println("Product Name: " + name);

                if(name.length() > 0 && description.length() > 0 && price > 0) {
                    progressDialog.show();
                    if(action.equals(getString(R.string.add))) {
                        Product product = new Product(name, description, price, 0.91F);
                        addItem(product);
                    }
                    else {
                        productBeingUpdated.setName(name);
                        productBeingUpdated.setDescription(description);
                        productBeingUpdated.setPrice(price);
                        updateItem(productBeingUpdated, dialog, position);
                    }
                }
                else {
                    if(price == 0) {
                        Toast.makeText(getContext(), getString(R.string.price_cant_be_zero), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        dialog.show();
    }

    void addItem(Product product) {
        try {
            File f = new File(getContext().getCacheDir(), "image.jpg");
            f.createNewFile();

            //Convert bitmap to byte array
            Bitmap bitmap = bitmapImage;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            CatalogCalls.addItem(product, f, new Utilities.CatalogCallback() {
                @Override
                public void onResult(String message) {
                    progressDialog.dismiss();
                }

                @Override
                public void onResult(String message, String productID, String pictureURL) {
                    progressDialog.dismiss();
                    if(message.equals(getString(R.string.success))) {
                        product.setID(productID);
                        product.setPictureURL(pictureURL);
                        catalogList.add(product);
                        adapter.notifyItemInserted(catalogList.size() - 1);
                        restoreScrollPositionAfterAdAdded();
                        Toast.makeText(getContext(), getString(R.string.item_added), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onResult(String message, List<Product> catalogList) {

                }
            });
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void restoreScrollPositionAfterAdAdded() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if (firstVisibleItemPosition == 0){
                layoutManager.scrollToPosition(0);
            }
        }
    }

    void updateItem(Product product, Dialog dialog, int position) {
        CatalogCalls.updateItem(product, new Utilities.CatalogCallback() {
            @Override
            public void onResult(String message) {
                if(message.equals(getString(R.string.success))) {
                    progressDialog.dismiss();
                    dialog.dismiss();
                    adapter.notifyItemChanged(position);
                    Toast.makeText(getContext(), getString(R.string.item_updated), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResult(String message, String productID, String pictureURL) {

            }

            @Override
            public void onResult(String message, List<Product> catalogList) {

            }
        });
    }

    void clearOrder() {
        Shared_Preferences.clearCartProducts(getContext());
        orderDetailsButton.setText(getString(R.string.order_details_button));
        adapter.notifyDataSetChanged();
    }

    void cameraIntent() {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            android.util.Log.e(TAG, "Permission already granted");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            permissionLauncher.launch(intent);
        }
        else {
            android.util.Log.e(TAG, "Permission isn't granted yet");
            permissionsResult.launch(Utilities.getPermissions());
        }
    }

    private void galleryIntent() {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            android.util.Log.e(TAG, "Permission already granted");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            permissionLauncher.launch(intent);
        }
        else {
            android.util.Log.e(TAG, "Permission isn't granted yet");
            permissionsResult.launch(Utilities.getPermissions());
        }
    }

    void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.add_product_picture));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.take_picture))) {
                    userChosenTask = getString(R.string.take_picture);
                    cameraIntent();
                }
                else if (items[item].equals(getString(R.string.choose_picture))) {
                    userChosenTask = getString(R.string.choose_picture);
                    galleryIntent();
                }
                else if (items[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                imageView.setImageBitmap(bm);
                imageView.setClipToOutline(true);
                isImageChosen = true;
                bitmapImage = bm;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".png");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Bitmap cropped = getRoundedCroppedImage(thumbnail);
        imageView.setImageBitmap(thumbnail);
        imageView.setClipToOutline(true);
        isImageCaptured = true;
        bitmapImage = thumbnail;
    }


    void fetchProducts() {
        progressDialog.show();
        catalogList.clear();
        CatalogCalls.getCatalog(new Utilities.CatalogCallback() {
            @Override
            public void onResult(String message) {
                progressDialog.dismiss();
                updateViews();

                if(swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onResult(String message, String productID, String pictureURL) {

            }

            @Override
            public void onResult(String message, List<Product> returnedCatalogList) {
                progressDialog.dismiss();
                catalogList.addAll(returnedCatalogList);
                adapter.notifyDataSetChanged();

                if(swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    void goToOrderDetailsFragment() {
        if(Shared_Preferences.getCartProducts(getContext()) != null) {
            List<Product> cartList = Shared_Preferences.getCartProducts(getContext());
            if(cartList.size() > 0) {
                progressDialog.show();
                String readerID = "tmr_E9yOUw568aYcaY";
                int total = Math.round(Utilities.calculateOrderTotal(cartList) * 100);
                JSONObject items = Utilities.convertCatalogListIntoJsonArray(cartList);
                ReaderCalls.displayItemsOnReader(readerID, total, items, new Utilities.ReaderCallback() {
                    @Override
                    public void onResult(String message) {
                        progressDialog.dismiss();
                    }
                });
            }
            else {
                Toast.makeText(getContext(), getString(R.string.select_a_product_first), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getContext(), getString(R.string.select_a_product_first), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.orderDetailsButton:
                goToOrderDetailsFragment();
                break;
        }
    }

    @Override
    public void onRefresh() {
        fetchProducts();
    }
}