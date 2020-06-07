package com.example.smartcane.bluetooth_connection.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.smartcane.R;
import com.example.smartcane.smart_functions.SmartFunctionsConstants;
import com.example.smartcane.state.StateActivity;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.smartcane.state.StateConstants.STATE_CONSTANTS_SP_NAME;
import static com.example.smartcane.state.StateConstants.STATE_CONSTANTS_SP_SHOW_BLUETOOTH_LIST;

public class BluetoothActivity extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;

    private LinearLayout linearLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Context mContext;

    private ArrayList<BluetoothDevice> listItems = new ArrayList<>();

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = getApplicationContext().getSharedPreferences(STATE_CONSTANTS_SP_NAME, MODE_PRIVATE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_bluetooth);
        mContext = this;
        setupUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        configure();
    }

    private void setupUi() {
        mSwipeRefreshLayout = findViewById(R.id.swipe_to_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this::configure);
        linearLayout = findViewById(R.id.bluetooth_ll_view);
        Button goToBluetoothButton = findViewById(R.id.btn_goto_bluetooth);

        goToBluetoothButton.setOnClickListener(v -> {
            goToBluetoothSettings();
        });

        configure();
    }

    @SuppressWarnings("deprecation")
    private void configure() {
        listItems.clear();
        if ((linearLayout).getChildCount() > 0)
            (linearLayout).removeAllViews();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            listItems.addAll(bluetoothAdapter.getBondedDevices());
        }

        for (BluetoothDevice btArray : listItems) {
            linearLayout.addView(addListViewItem(btArray));
            LinearLayout parent = new LinearLayout(mContext);
            parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
            parent.setBackgroundColor(getResources().getColor(R.color.colorBlack));

            linearLayout.addView(parent);
        }
        mSwipeRefreshLayout.setRefreshing(false);

    }

    @SuppressLint("InflateParams")
    private View addListViewItem(BluetoothDevice device) {
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert li != null;
        View view = li.inflate(R.layout.bluetooth_list_item, null);

        TextView tv1 = view.findViewById(R.id.tv_list_label);
        TextView tv2 = view.findViewById(R.id.tv_list_description);

        tv1.setText(device.getName());
        tv2.setText(device.getAddress());

        view.setOnClickListener(v -> {
            if (device.getName().equals(getString(R.string.cane_name))) {
                mSharedPreferences.edit().putString(SmartFunctionsConstants.SMART_FUNCTION_SP_BLUETOOTH_NAME, device.getName()).apply();
                mSharedPreferences.edit().putString(SmartFunctionsConstants.SMART_FUNCTION_SP_BLUETOOTH_ADDRESS, device.getAddress()).apply();
                mSharedPreferences.edit().putBoolean(STATE_CONSTANTS_SP_SHOW_BLUETOOTH_LIST, false).apply();
                Intent intent = new Intent(mContext, StateActivity.class);
                startActivity(intent);
            } else {
                new AlertDialog.Builder(mContext).setTitle(getString(R.string.bta_goto_configs))
                        .setMessage(getString(R.string.bta_dialog_message))
                        .setPositiveButton(getString(R.string.btn_go), (dialog, which) -> {
                            goToBluetoothSettings();
                        }).setNegativeButton(getString(R.string.common_not_now), (dialog, which) -> {
                    dialog.dismiss();
                }).create().show();
            }
        });

        return view;
    }

    private void goToBluetoothSettings() {
        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentOpenBluetoothSettings);
    }


}
