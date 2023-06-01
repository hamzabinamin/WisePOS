package com.app.wisepos.ui.home;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.app.wisepos.R;
import com.app.wisepos.databinding.FragmentHomeBinding;
import com.app.wisepos.datamodels.Order;
import com.app.wisepos.datamodels.Product;
import com.app.wisepos.networking.OrderCalls;
import com.app.wisepos.networking.ReaderCalls;
import com.app.wisepos.utilities.Shared_Preferences;
import com.app.wisepos.utilities.Utilities;
import com.google.gson.JsonObject;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String event = intent.getStringExtra("event");
            String paymentIntentID = intent.getStringExtra("payment-intent-id");
            Log.i("Received Event from Service", event);

            if(event.equals(getString(R.string.terminal_action_succeeded))) {
                progressDialog.show();
                String value = valueEditText.getText().toString();
                float valueFloat = Float.parseFloat(value);
                int total = Math.round(valueFloat);
                createOrder(total, paymentIntentID);
            }
        }
    };

    Button backspaceButton;
    Button oneButton;
    Button twoButton;
    Button threeButton;
    Button fourButton;
    Button fiveButton;
    Button sixButton;
    Button sevenButton;
    Button eightButton;
    Button nineButton;
    Button doubleZeroButton;
    Button plusButton;
    Button zeroButton;
    Button sendToReaderButton;
    TextView calculationTextView;
    EditText valueEditText;
    public ProgressDialog progressDialog;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setupViews(root);
        setupProgressDialog();
        setupOnClickListeners();
        return root;
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

    void setupViews(View root) {
        backspaceButton = root.findViewById(R.id.backspaceButton);
        oneButton = root.findViewById(R.id.oneButton);
        twoButton = root.findViewById(R.id.twoButton);
        threeButton = root.findViewById(R.id.threeButton);
        fourButton = root.findViewById(R.id.fourButton);
        fiveButton = root.findViewById(R.id.fiveButton);
        sixButton = root.findViewById(R.id.sixButton);
        sevenButton = root.findViewById(R.id.sevenButton);
        eightButton = root.findViewById(R.id.eightButton);
        nineButton = root.findViewById(R.id.nineButton);
        doubleZeroButton = root.findViewById(R.id.doubleZeroButton);
        plusButton = root.findViewById(R.id.plusButton);
        zeroButton = root.findViewById(R.id.zeroButton);
        sendToReaderButton = root.findViewById(R.id.sendToReaderButton);
        calculationTextView = root.findViewById(R.id.calculationTextView);
        valueEditText = root.findViewById(R.id.valueEditText);

        valueEditText.setEnabled(false);
    }

    void setupProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog = Utilities.setupProgressDialog(getContext(), progressDialog);
    }


    void setupOnClickListeners() {
        oneButton.setOnClickListener(this);
        twoButton.setOnClickListener(this);
        threeButton.setOnClickListener(this);
        fourButton.setOnClickListener(this);
        fiveButton.setOnClickListener(this);
        sixButton.setOnClickListener(this);
        sevenButton.setOnClickListener(this);
        eightButton.setOnClickListener(this);
        nineButton.setOnClickListener(this);
        doubleZeroButton.setOnClickListener(this);
        plusButton.setOnClickListener(this);
        zeroButton.setOnClickListener(this);
        sendToReaderButton.setOnClickListener(this);
        backspaceButton.setOnClickListener(this);
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
        String text = valueEditText.getText().toString().trim();
        String calculationText = calculationTextView.getText().toString().trim();
        DecimalFormat format = new DecimalFormat("0.##");

        if(view.getId() == R.id.backspaceButton) {
            int length = valueEditText.getText().length();
            if (length > 0) {
                valueEditText.getText().delete(length - 1, length);
            }
            calculationText = valueEditText.getText().toString();
            calculationTextView.setText(calculationText);
        }
        else if(view.getId() == R.id.oneButton) {
            String one = getString(R.string.one);

            if(checkIfLastCharacterIsPlus(calculationText)) {
                calculationText = calculationText + " " + one;
            }
            else {
                calculationText = calculationText + one;
            }

            calculationTextView.setText(calculationText);
            Expression calc = new ExpressionBuilder(calculationText).build();
            Double result = calc.evaluate();
            text = format.format(result);
            valueEditText.setText(text);
        }
        else if(view.getId() == R.id.twoButton) {
            String two = getString(R.string.two);

           /* if(checkIfLastCharacterIsPlus(calculationText)) {
                calculationText = calculationText + " " + two;
                Expression calc = new ExpressionBuilder(calculationText).build();
                Double result = calc.evaluate();
                text = String.valueOf(result);
            }
            else {
                calculationText = calculationText + two;
                System.out.println("Text in Two: " + text);
                if(!calculationText.contains("+")) {
                    text =  format.format(Float.parseFloat(calculationText));
                }
                else {
                    Expression calc = new ExpressionBuilder(calculationText).build();
                    Double result = calc.evaluate();
                    text = format.format(result);
                }
            } */
            if(checkIfLastCharacterIsPlus(calculationText)) {
                calculationText = calculationText + " " + two;
            }
            else {
                calculationText = calculationText + two;
            }
            calculationTextView.setText(calculationText);
            Expression calc = new ExpressionBuilder(calculationText).build();
            Double result = calc.evaluate();
            text = format.format(result);
            valueEditText.setText(text);
        }
        else if(view.getId() == R.id.threeButton) {
            String three = getString(R.string.three);

            if(checkIfLastCharacterIsPlus(calculationText)) {
                calculationText = calculationText + " " + three;
            }
            else {
                calculationText = calculationText + three;
            }
            calculationTextView.setText(calculationText);
            Expression calc = new ExpressionBuilder(calculationText).build();
            Double result = calc.evaluate();
            text = format.format(result);
            valueEditText.setText(text);
        }
        else if(view.getId() == R.id.fourButton) {
            String four = getString(R.string.four);

            if(checkIfLastCharacterIsPlus(calculationText)) {
                calculationText = calculationText + " " + four;
            }
            else {
                calculationText = calculationText + four;
            }

            calculationTextView.setText(calculationText);
            Expression calc = new ExpressionBuilder(calculationText).build();
            Double result = calc.evaluate();
            text = format.format(result);
            valueEditText.setText(text);
        }
        else if(view.getId() == R.id.fiveButton) {
            String five = getString(R.string.five);

            if(checkIfLastCharacterIsPlus(calculationText)) {
                calculationText = calculationText + " " + five;
            }
            else {
                calculationText = calculationText + five;
            }

            calculationTextView.setText(calculationText);
            Expression calc = new ExpressionBuilder(calculationText).build();
            Double result = calc.evaluate();
            text = format.format(result);
            valueEditText.setText(text);
        }
        else if(view.getId() == R.id.sixButton) {
            String six = getString(R.string.six);

            if(checkIfLastCharacterIsPlus(calculationText)) {
                calculationText = calculationText + " " + six;
            }
            else {
                calculationText = calculationText + six;
            }

            calculationTextView.setText(calculationText);
            Expression calc = new ExpressionBuilder(calculationText).build();
            Double result = calc.evaluate();
            text = format.format(result);
            valueEditText.setText(text);
        }
        else if(view.getId() == R.id.sevenButton) {
            String seven = getString(R.string.seven);

            if(checkIfLastCharacterIsPlus(calculationText)) {
                calculationText = calculationText + " " + seven;
            }
            else {
                calculationText = calculationText + seven;
            }

            calculationTextView.setText(calculationText);
            Expression calc = new ExpressionBuilder(calculationText).build();
            Double result = calc.evaluate();
            text = format.format(result);
            valueEditText.setText(text);
        }
        else if(view.getId() == R.id.eightButton) {
            String eight = getString(R.string.eight);

            if(checkIfLastCharacterIsPlus(calculationText)) {
                calculationText = calculationText + " " + eight;
            }
            else {
                calculationText = calculationText + eight;
            }

            calculationTextView.setText(calculationText);
            Expression calc = new ExpressionBuilder(calculationText).build();
            Double result = calc.evaluate();
            text = format.format(result);
            valueEditText.setText(text);
        }
        else if(view.getId() == R.id.nineButton) {
            String nine = getString(R.string.nine);

            if(checkIfLastCharacterIsPlus(calculationText)) {
                calculationText = calculationText + " " + nine;
            }
            else {
                calculationText = calculationText + nine;
            }

            calculationTextView.setText(calculationText);
            Expression calc = new ExpressionBuilder(calculationText).build();
            Double result = calc.evaluate();
            text = format.format(result);
            valueEditText.setText(text);
        }
        else if(view.getId() == R.id.doubleZeroButton) {
            String dot = getString(R.string.dot);

            if(!text.contains(".")) {
                calculationText = calculationText + dot;
                text = calculationText;
            }
            else {
                String secondNumber = StringUtils.substringAfterLast(calculationText, "+").trim();
                System.out.println("Second Number: " + secondNumber);
                if(!secondNumber.contains(dot)) {
                    calculationText = calculationText + dot;
                }
            }
            calculationTextView.setText(calculationText);
            valueEditText.setText(text);
        }
        else if(view.getId() == R.id.zeroButton) {
            String zero = getString(R.string.zero);

            if(checkIfLastCharacterIsPlus(calculationText)) {
                calculationText = calculationText + " " + zero;
            }
            else {
                calculationText = calculationText + zero;
            }

            calculationTextView.setText(calculationText);
            Expression calc = new ExpressionBuilder(calculationText).build();
            Double result = calc.evaluate();
            text = format.format(result);
            valueEditText.setText(text);
        }
        else if(view.getId() == R.id.plusButton) {
            String plus = getString(R.string.plus);
            Float textFloat = Float.parseFloat(text);

            if(textFloat > 0) {
                if(!checkIfLastCharacterIsPlus(calculationText)) {
                    calculationTextView.setText(String.valueOf(textFloat) + " " + plus);
                    calculationTextView.setVisibility(View.VISIBLE);
                }
            }
        }
        else if(view.getId() == R.id.sendToReaderButton) {
            sendToReader();
        }
    }

    boolean checkIfLastCharacterIsPlus(String string) {
        if(string.length() > 0) {
            if (string.substring(string.length() - 1).equals("+")) {
                return true;
            }
        }
        return false;
    }

    void sendToReader() {
        String value = valueEditText.getText().toString();

        if(value.length() > 0) {
            if(Shared_Preferences.getReaderID(getContext()) != null) {
                progressDialog.show();
                String readerID = Shared_Preferences.getReaderID(getContext());
                float valueFloat = Float.parseFloat(value);
                int total = Math.round(valueFloat * 100);
                ReaderCalls.createPaymentIntent(total, new Utilities.ReaderCallback() {
                    @Override
                    public void onResult(String message) {
                        progressDialog.dismiss();

                        if(message.equals(getString(R.string.failure))) {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onResult(String message, JsonObject paymentIntent) {
                        String paymentIntentID = paymentIntent.get("id").getAsString();
                        String paymentIntentStatus = paymentIntent.get("status").getAsString();
                        System.out.println("Payment Intent ID: " + paymentIntentID);
                        System.out.println("Payment Intent Status: " + paymentIntentStatus);
                        ReaderCalls.displayItemsOnReaderSimple(readerID, total, new Utilities.ReaderCallback() {
                            @Override
                            public void onResult(String message) {
                                progressDialog.dismiss();

                                if(message.equals(getString(R.string.failure))) {
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    ReaderCalls.processPaymentIntent(readerID, paymentIntentID, new Utilities.ReaderCallback() {
                                        @Override
                                        public void onResult(String message) {
                                            progressDialog.dismiss();

                                            if(!message.equals(getString(R.string.success))) {
                                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), getString(R.string.provide_an_appropriate_value), Toast.LENGTH_SHORT).show();
        }
    }

    void createOrder(float total, String paymentIntentID) {
        progressDialog.show();
        OrderCalls.createOrderSimple(total, paymentIntentID, new Utilities.OrderCallback() {
            @Override
            public void onResult(String message) {
                progressDialog.dismiss();

                if(message.equals(getString(R.string.failure))) {
                    Utilities.showAlert(getContext(), message);
                }
                else {
                    Toast.makeText(getContext(), getString(R.string.payment_successful), Toast.LENGTH_SHORT).show();
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