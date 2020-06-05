package com.example.smartcane.smart_functions.location_preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcane.R;
import com.example.smartcane.state.StateActivity;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.smartcane.state.StateConstants.STATE_CONSTANTS_SP_NAME;
import static com.example.smartcane.state.StateConstants.STATE_CONSTANTS_SP_SHOW_LOCALE_PREFERENCES;

public class LocationPreferencesActivity extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;

    private LinearLayout ll_location_type;
    private Context mContext;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_location_preferences);
        mSharedPreferences = getApplicationContext().getSharedPreferences(STATE_CONSTANTS_SP_NAME, MODE_PRIVATE);
        mContext = this;
        setupUi();
        populateLayout();
    }

    private void setupUi() {
        Objects.requireNonNull(this.getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_app_bar);
        getSupportActionBar().setElevation(10);
        View view = getSupportActionBar().getCustomView();
        TextView tv = view.findViewById(R.id.tv_actionbar_label);
        tv.setText("Preferências");
        ImageView iv = view.findViewById(R.id.iv_back);

        iv.setOnClickListener(v -> finish());
        Button button = findViewById(R.id.btn_continue);

        ll_location_type = findViewById(R.id.ll_location_types);
        if (mSharedPreferences.getBoolean(STATE_CONSTANTS_SP_SHOW_LOCALE_PREFERENCES, true)) {
            iv.setVisibility(View.GONE);

            button.setOnClickListener(v -> {
                mSharedPreferences.edit().putBoolean(STATE_CONSTANTS_SP_SHOW_LOCALE_PREFERENCES, false).apply();
                startActivity(new Intent(mContext, StateActivity.class));
            });
        } else button.setVisibility(View.GONE);
    }

    private void populateLayout() {

        String[] locales = getResources().getStringArray(R.array.nearbyLocales);
        String[] localesMap = getResources().getStringArray(R.array.nearbyLocalesForMap);
        ArrayList<View> mViews = new ArrayList<>();

        for (int i = 0; i < locales.length; i++) {
            int pos = i;
            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = vi.inflate(R.layout.location_preference_item, null);

            TextView tv = view.findViewById(R.id.tv_location_type);
            Switch sw = view.findViewById(R.id.sw_location_type);
            LinearLayout ll = view.findViewById(R.id.ll_separator_view);

            if (i + 1 == locales.length) {
                ll.setVisibility(View.GONE);
            }

            tv.setText(locales[i]);
            sw.setChecked(mSharedPreferences.getBoolean(locales[i], false));
            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mSharedPreferences.edit().putBoolean(locales[pos], isChecked).apply();
                    if (isChecked) {
                        mSharedPreferences.edit().putString(localesMap[pos], localesMap[pos]).apply();
                    } else mSharedPreferences.edit().putString(localesMap[pos], "").apply();
                }
            });

            //this array will save all views created using types from google response (every request)
            mViews.add(view);

            ll_location_type.addView(view);
        }


    }
}
