package com.app.wisepos.ui.settings;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.wisepos.R;
import com.app.wisepos.adapters.CatalogAdapter;
import com.app.wisepos.databinding.FragmentCatalogBinding;
import com.app.wisepos.databinding.FragmentSettingsBinding;
import com.app.wisepos.datamodels.Product;
import com.app.wisepos.networking.CatalogCalls;
import com.app.wisepos.utilities.Shared_Preferences;
import com.app.wisepos.utilities.Utilities;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    Button applySettingsButton;
    EditText readerIDEditText;
    private FragmentSettingsBinding binding;

    private static final String TAG = "SettingsFragment";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setupViews(root);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void setupViews(View root) {
        applySettingsButton = (Button) root.findViewById(R.id.applySettingsButton);
        readerIDEditText = (EditText) root.findViewById(R.id.readerIDEditText);
        setupOnClickListeners();

        if(Shared_Preferences.getReaderID(getContext()) != null) {
            String readerID = Shared_Preferences.getReaderID(getContext());
            readerIDEditText.setText(readerID);
        }
    }

    void setupOnClickListeners() {
        applySettingsButton.setOnClickListener(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    void setReaderID() {
        String readerID = readerIDEditText.getText().toString();

        if(readerID.length() > 0) {
            Shared_Preferences.saveReaderID(getContext(), readerID);
            Toast.makeText(getContext(), getString(R.string.reader_id_saved), Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getContext(), getString(R.string.provide_reader_id), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.applySettingsButton) {
            setReaderID();
        }
    }
}