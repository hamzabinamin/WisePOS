package com.app.wisepos.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.app.wisepos.datamodels.Product;
import com.app.wisepos.networking.CatalogCalls;
import com.app.wisepos.ui.catalog.CatalogFragment;
import com.app.wisepos.utilities.Shared_Preferences;
import com.app.wisepos.utilities.Utilities;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.ViewHolder> {

    private List<Product> products;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;

    private CatalogFragment catalogFragment;

    // data is passed into the constructor
    public CatalogAdapter(Context context, CatalogFragment catalogFragment, List<Product> products) {
        this.inflater = LayoutInflater.from(context);
        this.catalogFragment = catalogFragment;
        this.products = products;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.catalog_view, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.nameTextView.setText(product.getName());
        holder.descriptionTextView.setText(product.getDescription());
        holder.priceTextView.setText( "€" + String.valueOf(product.getPrice()) + " (" + String.format("%.2f", product.getUSDPrice()) + " USD)");
        Picasso.get().load(product.getPictureURL()).placeholder(R.drawable.progress_animation).fit().centerInside().into(holder.imageView);
        Picasso.get().setLoggingEnabled(true);

        if(Shared_Preferences.getCartProducts(inflater.getContext()) != null) {
            List<Product> cartList = Shared_Preferences.getCartProducts(inflater.getContext());
            catalogFragment.updateOrderDetailsButton(cartList);
            Product oldProduct = products.get(position);
            if(cartList.contains(oldProduct)) {
                holder.relativeLayout.setBackgroundResource(R.drawable.cardview_selected_border);
                holder.quantityLinearLayout.setVisibility(View.VISIBLE);
                holder.addButton.setVisibility(View.GONE);
                Product p = cartList.get(cartList.indexOf(oldProduct));
                holder.quantityButton.setText("" + p.getQuantity());
            }
            else {
                holder.relativeLayout.setBackgroundResource(R.drawable.cardview_unselected_border);
                holder.quantityLinearLayout.setVisibility(View.GONE);
                holder.addButton.setVisibility(View.VISIBLE);
            }
        }
        else {
            holder.relativeLayout.setBackgroundResource(R.drawable.cardview_unselected_border);
            holder.quantityLinearLayout.setVisibility(View.GONE);
            holder.addButton.setVisibility(View.VISIBLE);
        }

        holder.minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(holder.quantityButton.getText().toString());
               // Product product = products.get(position);
                quantity = quantity - 1;
                product.setQuantity(quantity);

                if(quantity > 0) {
                    holder.quantityButton.setText(String.valueOf(product.getQuantity()));

                    if(Shared_Preferences.getCartProducts(inflater.getContext()) != null) {
                        List<Product> cartList = Shared_Preferences.getCartProducts(inflater.getContext());
                        if (cartList.contains(product)) {
                            int index = cartList.indexOf(product);
                            cartList.set(index, product);
                            Shared_Preferences.saveCartProducts(inflater.getContext(), cartList);
                        }
                        else {
                            cartList.add(product);
                            Shared_Preferences.saveCartProducts(inflater.getContext(), cartList);
                        }
                        catalogFragment.updateOrderDetailsButton(cartList);
                    }
                }
                else if(quantity == 0) {
                    holder.relativeLayout.setBackgroundResource(R.drawable.cardview_unselected_border);
                    holder.addButton.setVisibility(View.VISIBLE);
                    holder.quantityLinearLayout.setVisibility(View.GONE);

                    if(Shared_Preferences.getCartProducts(inflater.getContext()) != null) {
                        List<Product> cartList = Shared_Preferences.getCartProducts(inflater.getContext());
                        if(cartList.contains(product)) {
                            cartList.remove(product);
                            System.out.print("Cart Size: " + cartList.size());
                            Shared_Preferences.saveCartProducts(inflater.getContext(), cartList);
                            catalogFragment.updateOrderDetailsButton(cartList);
                        }
                    }
                }
            }
        });

        holder.plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(holder.quantityButton.getText().toString());
               // Product product = products.get(position);
                quantity = quantity + 1;
                product.setQuantity(quantity);

                holder.quantityButton.setText(String.valueOf(product.getQuantity()));

                if(Shared_Preferences.getCartProducts(inflater.getContext()) != null) {
                    List<Product> cartList = Shared_Preferences.getCartProducts(inflater.getContext());

                    if(cartList.contains(product)) {
                        int index = cartList.indexOf(product);
                        System.out.println("Index: " + index);
                        cartList.set(index, product);
                        Shared_Preferences.saveCartProducts(inflater.getContext(), cartList);
                        System.out.println("Cart Size: " + cartList.size());
                    }
                    else {
                        cartList.add(product);
                        Shared_Preferences.saveCartProducts(inflater.getContext(), cartList);
                    }
                    catalogFragment.updateOrderDetailsButton(cartList);
                }
            }
        });

        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.relativeLayout.setBackgroundResource(R.drawable.cardview_selected_border);
                holder.addButton.setVisibility(View.GONE);
                holder.quantityLinearLayout.setVisibility(View.VISIBLE);
                holder.quantityButton.setText("1");
                product.setQuantity(1);
                List<Product> cartList;

                if(Shared_Preferences.getCartProducts(inflater.getContext()) != null) {
                    cartList = Shared_Preferences.getCartProducts(inflater.getContext());
                    cartList.add(product);
                }
                else {
                    cartList = new ArrayList<>();
                    cartList.add(product);
                }
                System.out.println("Add to Cart Button's Cart Size: " + cartList.size());
                Shared_Preferences.saveCartProducts(inflater.getContext(), cartList);
                catalogFragment.updateOrderDetailsButton(cartList);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), holder.imageView);
                popupMenu.getMenuInflater().inflate(R.menu.option_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if(menuItem.getItemId() == R.id.update_item) {
                            catalogFragment.addUpdateDialog(catalogFragment.getString(R.string.update), products.get(position), position);
                        }
                        else {
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            catalogFragment.progressDialog.show();
                                            CatalogCalls.deleteItem(product, new Utilities.CatalogCallback() {
                                                @Override
                                                public void onResult(String message) {

                                                    if(message.equals(catalogFragment.getString(R.string.success))) {
                                                        catalogFragment.progressDialog.dismiss();
                                                        catalogFragment.adapter.notifyItemRemoved(position);
                                                        catalogFragment.catalogList.remove(position);
                                                        catalogFragment.updateViews();
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
        return products.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Button minusButton;
        Button quantityButton;
        Button plusButton;
        Button addButton;
        TextView nameTextView;
        TextView descriptionTextView;
        TextView priceTextView;
        ImageView imageView;
        LinearLayout quantityLinearLayout;
        RelativeLayout relativeLayout;

        ViewHolder(View itemView) {
            super(itemView);
            minusButton = itemView.findViewById(R.id.minusButton);
            quantityButton = itemView.findViewById(R.id.quantityButton);
            plusButton = itemView.findViewById(R.id.plusButton);
            addButton = itemView.findViewById(R.id.addButton);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            imageView = itemView.findViewById(R.id.imageView);
            quantityLinearLayout = itemView.findViewById(R.id.quantityLinearLayout);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Product getItem(int id) {
        return products.get(id);
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