package com.app.wisepos.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.wisepos.R;
import com.app.wisepos.datamodels.Order;
import com.app.wisepos.datamodels.Product;
import com.app.wisepos.networking.CatalogCalls;
import com.app.wisepos.networking.OrderCalls;
import com.app.wisepos.ui.catalog.CatalogFragment;
import com.app.wisepos.ui.dashboard.PurchaseHistoryFragment;
import com.app.wisepos.utilities.Shared_Preferences;
import com.app.wisepos.utilities.Utilities;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PurchaseHistoryAdapter extends RecyclerView.Adapter<PurchaseHistoryAdapter.ViewHolder> {

    private List<Order> orders;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;

    private PurchaseHistoryFragment purchaseHistoryFragment;

    // data is passed into the constructor
    public PurchaseHistoryAdapter(Context context, PurchaseHistoryFragment purchaseHistoryFragment, List<Order> orders) {
        this.inflater = LayoutInflater.from(context);
        this.purchaseHistoryFragment = purchaseHistoryFragment;
        this.orders = orders;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.purchase_history_view, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Order order = orders.get(position);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy- hh:mm:ss a", Locale.US);
        holder.orderIDTextView.setText(order.getID());
        holder.dateTextView.setText(formatter.format(order.getDate()));
        holder.totalTextView.setText( "€" + String.valueOf(order.getTotal()) + " (" + String.format("%.2f", order.getTotal()) + " USD)");

        List<Product> itemList = orders.get(position).getItemsList();
        List<LinearLayout> linearLayoutList = new ArrayList<>();

        for(int i=0; i<itemList.size(); i++) {
            LinearLayout linearLayoutB = new LinearLayout(inflater.getContext());
            LinearLayout.LayoutParams layoutParamsLLB = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsLLB.setMargins(80,5,0,0);
            linearLayoutB.setOrientation(LinearLayout.VERTICAL);
            linearLayoutB.setLayoutParams(layoutParamsLLB);

            Product product = itemList.get(i);

            TextView textView0 = new TextView(inflater.getContext());
            textView0.setText("" + (i+1) + ".");
            textView0.setTextSize(14);
            textView0.setTextColor(inflater.getContext().getResources().getColor(R.color.black));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.2f);
            textView0.setLayoutParams(layoutParams);
            textView0.setWidth(20);


            TextView textView = new TextView(inflater.getContext());
            textView.setText(product.getName());
            textView.setTextSize(14);
            textView.setTextColor(inflater.getContext().getResources().getColor(R.color.black));
            layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2.5f);
            textView.setLayoutParams(layoutParams);
            textView.setSingleLine(true);
    /*        textView.setWidth(55);
            textView.setMinWidth(55);
            textView.setMaxWidth(55); */
            textView.setEllipsize(TextUtils.TruncateAt.END);

            TextView textView2 = new TextView(inflater.getContext());
            textView2.setText("Qty " + product.getQuantity());
            textView2.setTextSize(14);
            textView2.setGravity(Gravity.CENTER);
            textView2.setTextColor(inflater.getContext().getResources().getColor(R.color.black));
            textView2.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f));
           /* textView2.setWidth(20);
            textView2.setMinWidth(20);
            textView2.setMaxWidth(20); */

            TextView textView3 = new TextView(inflater.getContext());
            textView3.setText("€" + product.getPrice());
            textView3.setTextSize(14);
            textView3.setGravity(Gravity.CENTER);
            textView3.setTextColor(inflater.getContext().getResources().getColor(R.color.black));
            textView3.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            LinearLayout linearLayout = new LinearLayout(inflater.getContext());
            LinearLayout.LayoutParams layoutParamsLL = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsLL.setMargins(30,5,0,0);

            linearLayout.setLayoutParams(layoutParamsLL);

            linearLayout.addView(textView0);
            linearLayout.addView(textView);
            linearLayout.addView(textView2);
            linearLayout.addView(textView3);

            linearLayoutList.add(linearLayout);
        }

        for(int i=0; i<linearLayoutList.size(); i++) {
            holder.verticalLinearLayout.addView(linearLayoutList.get(i));
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), holder.relativeLayout);
                popupMenu.getMenuInflater().inflate(R.menu.option_order_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if(menuItem.getItemId() == R.id.update_item) {
                           // catalogFragment.addUpdateDialog(catalogFragment.getString(R.string.update), products.get(position), position);
                        }
                        else {
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            purchaseHistoryFragment.progressDialog.show();
                                            OrderCalls.deleteOrder(order, new Utilities.CatalogCallback() {
                                                @Override
                                                public void onResult(String message) {

                                                    if(message.equals(purchaseHistoryFragment.getString(R.string.success))) {
                                                        purchaseHistoryFragment.progressDialog.dismiss();
                                                        purchaseHistoryFragment.adapter.notifyItemRemoved(position);
                                                        purchaseHistoryFragment.orderList.remove(position);
                                                        purchaseHistoryFragment.updateViews();
                                                    }
                                                }

                                                @Override
                                                public void onResult(String message, String productID, String pictureURL) {

                                                }

                                                @Override
                                                public void onResult(String message, List<Product> returnedCatalogList) {

                                                }
                                            });
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked
                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext());
                            builder.setMessage(inflater.getContext().getString(R.string.are_you_sure_delete)).setPositiveButton(inflater.getContext().getString(R.string.are_you_sure_yes), dialogClickListener)
                                    .setNegativeButton(inflater.getContext().getString(R.string.are_you_sure_no), dialogClickListener).show();
                        }
                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();
                return false;
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return orders.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView orderIDTextView;
        TextView dateTextView;
        TextView totalTextView;
        LinearLayout verticalLinearLayout;
        RelativeLayout relativeLayout;

        ViewHolder(View itemView) {
            super(itemView);
            orderIDTextView = itemView.findViewById(R.id.orderIDTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            totalTextView = itemView.findViewById(R.id.totalTextView);
            verticalLinearLayout = itemView.findViewById(R.id.verticalLinearLayout);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Order getItem(int id) {
        return orders.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}