package com.example.smartcane.initial_register;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.example.smartcane.R;
import com.example.smartcane.state.StateActivity;
import com.example.smartcane.utils.BaseActivity;

import java.util.Objects;

import static com.example.smartcane.initial_register.InitialRegisterConstants.INITIAL_REGISTER_CONSTANTS_SP_EMERGENCY_NUMBER;
import static com.example.smartcane.initial_register.InitialRegisterConstants.INITIAL_REGISTER_CONSTANTS_SP_FROM_SFA;
import static com.example.smartcane.initial_register.InitialRegisterConstants.INITIAL_REGISTER_CONSTANTS_SP_USER_ADDRESS;
import static com.example.smartcane.initial_register.InitialRegisterConstants.INITIAL_REGISTER_CONSTANTS_SP_USER_HEIGHT;
import static com.example.smartcane.state.StateConstants.STATE_CONSTANTS_SP_NAME;
import static com.example.smartcane.state.StateConstants.STATE_CONSTANTS_SP_SHOW_INITIAL_REGISTER;

public class InitialRegisterActivity extends BaseActivity {

    private SharedPreferences mSharedPreferences;
    private LinearLayout mFields;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getIntent().getBooleanExtra(INITIAL_REGISTER_CONSTANTS_SP_FROM_SFA, false)) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            Objects.requireNonNull(getSupportActionBar()).hide();
        } else {
            Objects.requireNonNull(this.getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.custom_app_bar);
            getSupportActionBar().setElevation(10);
            View view = getSupportActionBar().getCustomView();
            TextView tv = view.findViewById(R.id.tv_actionbar_label);
            tv.setText(getString(R.string.ir_title1));
            ImageView iv = view.findViewById(R.id.iv_back);

            iv.setOnClickListener(v -> finish());
        }
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_initial_register);
        mSharedPreferences = getApplicationContext().getSharedPreferences(STATE_CONSTANTS_SP_NAME, MODE_PRIVATE);
        setupUi();
    }

    private void setupUi() {
        mFields = findViewById(R.id.ll_fields);
        Button continueButton = findViewById(R.id.btn_continue);
        if (getIntent().getBooleanExtra(INITIAL_REGISTER_CONSTANTS_SP_FROM_SFA, false)) {
            TextView tv_title = findViewById(R.id.tv_title);
            tv_title.setText(R.string.ir_title2);
            continueButton.setText(R.string.save_text);
            continueButton.setOnClickListener(v -> {
                if (saveFields())
                    finish();
            });
        } else {

            continueButton.setOnClickListener(v -> {
                if (saveFields())
                    mSharedPreferences.edit().putBoolean(STATE_CONSTANTS_SP_SHOW_INITIAL_REGISTER, false).apply();
                startActivity(new Intent(this, StateActivity.class));
            });
        }
        addListViewItem();
    }

    private boolean saveFields() {
        for (int i = 0; i < mFields.getChildCount(); i++) {
            View view = mFields.getChildAt(i);
            EditText et = view.findViewById(R.id.et_initial_register);
            if (et.getText().toString().isEmpty()) {
                return false;
            } else {
                TextView tv = view.findViewById(R.id.tv_initial_register);
                if (tv.getText().toString().contains("altura")) {
                    mSharedPreferences.edit().putString(INITIAL_REGISTER_CONSTANTS_SP_USER_HEIGHT, et.getText().toString()).apply();
                } else if (tv.getText().toString().contains("SMS")) {
                    mSharedPreferences.edit().putString(INITIAL_REGISTER_CONSTANTS_SP_EMERGENCY_NUMBER, et.getText().toString()).apply();
                } else {
                    mSharedPreferences.edit().putString(INITIAL_REGISTER_CONSTANTS_SP_USER_ADDRESS, et.getText().toString()).apply();
                }
            }
        }
        return true;
    }

    @SuppressLint("InflateParams")
    private void addListViewItem() {
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert li != null;
        View view = li.inflate(R.layout.initial_register_item, null);
        View view2 = li.inflate(R.layout.initial_register_item, null);
        View view3 = li.inflate(R.layout.initial_register_item, null);

        TextView tv1 = view.findViewById(R.id.tv_initial_register);
        EditText et1 = view.findViewById(R.id.et_initial_register);
        TextView tv2 = view2.findViewById(R.id.tv_initial_register);
        EditText et2 = view2.findViewById(R.id.et_initial_register);
        TextView tv3 = view3.findViewById(R.id.tv_initial_register);
        EditText et3 = view3.findViewById(R.id.et_initial_register);

        tv1.setText(getString(R.string.ir_type_height));
        et1.setInputType(InputType.TYPE_CLASS_NUMBER);
        tv2.setText(getString(R.string.ir_type_phone_number));
        et2.setInputType(InputType.TYPE_CLASS_NUMBER);
        tv3.setText(getString(R.string.ir_type_address));
        et3.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);

        String s1 = mSharedPreferences.getString(INITIAL_REGISTER_CONSTANTS_SP_USER_HEIGHT, "");
        String s2 = mSharedPreferences.getString(INITIAL_REGISTER_CONSTANTS_SP_EMERGENCY_NUMBER, "");
        String s3 = mSharedPreferences.getString(INITIAL_REGISTER_CONSTANTS_SP_USER_ADDRESS, "");

        et1.setText(s1);
        et2.setText(s2);
        et3.setText(s3);

        mFields.addView(view);
        mFields.addView(view2);
        mFields.addView(view3);
    }
}
