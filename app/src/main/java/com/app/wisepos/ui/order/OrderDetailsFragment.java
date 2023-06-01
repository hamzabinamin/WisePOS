package com.app.wisepos.ui.order;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.wisepos.MainActivity;
import com.app.wisepos.R;
import com.app.wisepos.datamodels.Order;
import com.app.wisepos.datamodels.Product;
import com.app.wisepos.interfaces.ReaderInterface;
import com.app.wisepos.networking.OrderCalls;
import com.app.wisepos.networking.ReaderCalls;
import com.app.wisepos.utilities.Shared_Preferences;
import com.app.wisepos.utilities.Utilities;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsFragment extends Fragment implements View.OnClickListener {

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String event = intent.getStringExtra("event");
            String paymentIntentID = intent.getStringExtra("payment-intent-id");
            Log.i("Received Event from Service", event);

            if(event.equals(getString(R.string.terminal_action_succeeded))) {

                if(Shared_Preferences.getCartProducts(getContext()) != null) {
                    List<Product> cartList = Shared_Preferences.getCartProducts(getContext());
                    if (cartList.size() > 0) {
                        progressDialog.show();
                        Float total = Utilities.calculateOrderTotal(cartList);
                        JSONObject items = Utilities.convertCatalogListIntoJsonArray(cartList);
                        createOrder(total, paymentIntentID, items);
                    }
                }
            }
        }
    };

    Button finalizeOrderButton;
    TextView totalTextView;
    RelativeLayout relativeLayout;
    LinearLayout verticalLinearLayout;
    public ProgressDialog progressDialog;
    List<Product> items = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_details, container, false);
        setupViews(view);
        setupProgressDialog();
        setupOnClickListeners();
        populateOrderView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // This registers messageReceiver to receive messages.
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(messageReceiver, new IntentFilter("notification"));
    }

    @Override
    public void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(messageReceiver);
        super.onPause();
    }

    void setupViews(View view) {
        finalizeOrderButton = view.findViewById(R.id.finalizeOrderButton);
        totalTextView = view.findViewById(R.id.totalTextView);
        relativeLayout = view.findViewById(R.id.relativeLayout);
        verticalLinearLayout = view.findViewById(R.id.verticalLinearLayout);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    void setupProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog = Utilities.setupProgressDialog(getContext(), progressDialog);
    }

    void setupOnClickListeners() {
        finalizeOrderButton.setOnClickListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.cancel_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.cancel_menu:
                cancelPaymentIntent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    void populateOrderView() {
        List<LinearLayout> linearLayoutList = new ArrayList<>();

        items = (ArrayList<Product>)getArguments().getSerializable("items");

        for(int i=0; i<items.size(); i++) {
            LinearLayout linearLayoutB = new LinearLayout(getContext());
            LinearLayout.LayoutParams layoutParamsLLB = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsLLB.setMargins(80,5,0,0);
            linearLayoutB.setOrientation(LinearLayout.VERTICAL);
            linearLayoutB.setLayoutParams(layoutParamsLLB);

            Product product = items.get(i);

            TextView textView0 = new TextView(getContext());
            textView0.setText("" + (i+1) + ".");
            textView0.setTextSize(14);
            textView0.setTextColor(getContext().getResources().getColor(R.color.black));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.2f);
            textView0.setLayoutParams(layoutParams);
            textView0.setWidth(20);


            TextView textView = new TextView(getContext());
            textView.setText(product.getName());
            textView.setTextSize(14);
            textView.setTextColor(getContext().getResources().getColor(R.color.black));
            layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2.5f);
            textView.setLayoutParams(layoutParams);
            textView.setSingleLine(true);
            textView.setEllipsize(TextUtils.TruncateAt.END);

            TextView textView2 = new TextView(getContext());
            textView2.setText("Qty " + product.getQuantity());
            textView2.setTextSize(14);
            textView2.setGravity(Gravity.CENTER);
            textView2.setTextColor(getContext().getResources().getColor(R.color.black));
            textView2.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f));

            TextView textView3 = new TextView(getContext());
            textView3.setText("€" + String.format("%.2f", product.getPrice()));
            textView3.setTextSize(14);
            textView3.setGravity(Gravity.CENTER);
            textView3.setTextColor(getContext().getResources().getColor(R.color.black));
            textView3.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            LinearLayout linearLayout = new LinearLayout(getContext());
            LinearLayout.LayoutParams layoutParamsLL = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsLL.setMargins(30,5,0,0);

            linearLayout.setLayoutParams(layoutParamsLL);

            linearLayout.addView(textView0);
            linearLayout.addView(textView);
            linearLayout.addView(textView2);
            linearLayout.addView(textView3);

            linearLayoutList.add(linearLayout);

            Float total = Utilities.calculateOrderTotal(items);
            totalTextView.setText( "€" + String.format("%.2f", total) + " (" + String.format("%.2f", Utilities.orderTotalInUSD(total, product.getUSDRate())) + " USD)");
        }

        for(int i=0; i<linearLayoutList.size(); i++) {
            verticalLinearLayout.addView(linearLayoutList.get(i));
        }
    }

    public void setItems(List<Product> items) {
        this.items = items;
    }

    void clearOrder() {
        Shared_Preferences.clearCartProducts(getContext());
    }

    void cancelPaymentIntent() {
        progressDialog.show();
        if(Shared_Preferences.getReaderID(getContext()) != null) {
            String readerID = Shared_Preferences.getReaderID(getContext());

            ReaderCalls.cancelPaymentIntent(readerID, new Utilities.ReaderCallback() {
                @Override
                public void onResult(String message) {
                    progressDialog.dismiss();

                    if(!message.equals(getString(R.string.success))) {
                        Utilities.showAlert(getContext(), message);
                    }
                    else {
                        Toast.makeText(getContext(), getString(R.string.payment_got_cleared), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onResult(String message, JsonObject result) {

                }
            });
        }
        else {
            Toast.makeText(getContext(), getString(R.string.provide_reader_id), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.finalizeOrderButton) {
            if(Shared_Preferences.getCartProducts(getContext()) != null) {
                List<Product> cartList = Shared_Preferences.getCartProducts(getContext());
                if(cartList.size() > 0) {
                   // String readerID = "tmr_E9yOUw568aYcaY"; // simulated
                    if(Shared_Preferences.getReaderID(getContext()) != null) {
                        progressDialog.show();
                        String readerID = Shared_Preferences.getReaderID(getContext());

                        int total = Math.round(Utilities.calculateOrderTotal(cartList) * 100);
                        JSONObject items = Utilities.convertCatalogListIntoJsonArray(cartList);

                        ReaderCalls.createPaymentIntent(total, new Utilities.ReaderCallback() {
                            @Override
                            public void onResult(String message) {
                                progressDialog.dismiss();

                                if(!message.equals(getString(R.string.success))) {
                                    Utilities.showAlert(getContext(), message);
                                }
                            }

                            @Override
                            public void onResult(String message, JsonObject paymentIntent) {
                                String paymentIntentID = paymentIntent.get("id").getAsString();
                                String paymentIntentStatus = paymentIntent.get("status").getAsString();
                                System.out.println("Payment Intent ID: " + paymentIntentID);
                                System.out.println("Payment Intent Status: " + paymentIntentStatus);
                                ReaderCalls.displayItemsOnReader(readerID, total, items, new Utilities.ReaderCallback() {
                                    @Override
                                    public void onResult(String message) {
                                        progressDialog.dismiss();

                                        if(!message.equals(getString(R.string.success))) {
                                            Utilities.showAlert(getContext(), message);
                                        }
                                        else {
                                            ReaderCalls.processPaymentIntent(readerID, paymentIntentID, new Utilities.ReaderCallback() {
                                                @Override
                                                public void onResult(String message) {
                                                    progressDialog.dismiss();

                                                    if(message.equals(getString(R.string.failure))) {
                                                        Utilities.showAlert(getContext(), message);
                                                    }
                                                }

                                                @Override
                                                public void onResult(String message, JsonObject result) {
                                                    JsonObject action = result.get("action").getAsJsonObject();
                                                    String status = action.get("status").getAsString();

                                                    if(status.equals("in_progress")) {
                                                        Utilities.showAlert(getContext(), getString(R.string.tap_card));
                                                    }
                                                    else {
                                                        Utilities.showAlert(getContext(), getString(R.string.error));
                                                    }
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onResult(String message, JsonObject paymentIntent) {

                                    }
                                });
                            }
                        });
                    }
                    else {
                        Toast.makeText(getContext(), getString(R.string.provide_reader_id), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), getString(R.string.select_a_product_first), Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(getContext(), getString(R.string.select_a_product_first), Toast.LENGTH_SHORT).show();
            }
        }
    }

    void createOrder(float total, String paymentIntentID, JSONObject items) {
        progressDialog.show();
        OrderCalls.createOrder(total, paymentIntentID, items, new Utilities.OrderCallback() {
            @Override
            public void onResult(String message) {
                progressDialog.dismiss();

                if(message.equals(getString(R.string.failure))) {
                    Utilities.showAlert(getContext(), message);
                }
                else {
                    Toast.makeText(getContext(), getString(R.string.payment_successful), Toast.LENGTH_SHORT).show();
                    clearOrder();
                    goToPurchaseHistoryFragment();
                }
            }

            @Override
            public void onResult(String message, List<Order> orderList) {

            }
        });
    }

    void goToPurchaseHistoryFragment() {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.navigation_purchase_history, null, new NavOptions.Builder().setPopUpTo(navController.getGraph().getStartDestinationId(), true).build());
    }
}
