package com.app.wisepos.ui.dashboard;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.wisepos.R;
import com.app.wisepos.adapters.CatalogAdapter;
import com.app.wisepos.adapters.PurchaseHistoryAdapter;
import com.app.wisepos.databinding.FragmentPurchaseHistoryBinding;
import com.app.wisepos.datamodels.Order;
import com.app.wisepos.datamodels.Product;
import com.app.wisepos.networking.CatalogCalls;
import com.app.wisepos.networking.OrderCalls;
import com.app.wisepos.ui.catalog.CatalogFragment;
import com.app.wisepos.ui.order.OrderDetailsFragment;
import com.app.wisepos.utilities.Shared_Preferences;
import com.app.wisepos.utilities.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PurchaseHistoryFragment extends Fragment implements PurchaseHistoryAdapter.ItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    TextView noTransactionsTextView;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    public PurchaseHistoryAdapter adapter;
    public ProgressDialog progressDialog;
    public List<Order> orderList = new ArrayList<Order>();

    private FragmentPurchaseHistoryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPurchaseHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setupViews(root);
        return root;
    }

    void setupViews(View root) {
        noTransactionsTextView = (TextView) root.findViewById(R.id.noTransactionsTextView);
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        setupProgressDialog();
        setupSwipeRefreshLayout();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PurchaseHistoryAdapter(getContext(), PurchaseHistoryFragment.this, orderList);
        adapter.setClickListener(this);
        fetchOrders();
        recyclerView.setAdapter(adapter);
    }

    void setupProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog = Utilities.setupProgressDialog(getContext(), progressDialog);
    }

    void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.purple_500, R.color.purple_200, R.color.purple_500, R.color.purple_700);
    }

    public void updateViews() {
        if(orderList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            noTransactionsTextView.setVisibility(View.INVISIBLE);
        }
        else {
            recyclerView.setVisibility(View.INVISIBLE);
            noTransactionsTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    void fetchOrders() {
        progressDialog.show();
        orderList.clear();
        OrderCalls.getOrders(new Utilities.OrderCallback() {
            @Override
            public void onResult(String message) {
                progressDialog.dismiss();
                updateViews();

                if(swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onResult(String message, List<Order> returnedOrderList) {
                progressDialog.dismiss();
                orderList.addAll(returnedOrderList);
                Collections.reverse(orderList);
                adapter.notifyDataSetChanged();

                if(swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        fetchOrders();
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}