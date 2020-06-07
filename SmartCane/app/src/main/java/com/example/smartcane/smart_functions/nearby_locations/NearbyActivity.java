package com.example.smartcane.smart_functions.nearby_locations;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcane.R;
import com.example.smartcane.smart_functions.SmartFunctionsConstants;
import com.example.smartcane.smart_functions.api_google.model.GooglePlace;

import java.util.ArrayList;
import java.util.Objects;

public class NearbyActivity extends AppCompatActivity {

    private ArrayList<GooglePlace> mPlacesArray;

    @SuppressWarnings("unchecked")
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_nearby);
        Objects.requireNonNull(this.getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_app_bar);
        getSupportActionBar().setElevation(10);
        View view = getSupportActionBar().getCustomView();
        TextView tv = view.findViewById(R.id.tv_actionbar_label);
        tv.setText(getString(R.string.nearby_title));
        ImageView iv = view.findViewById(R.id.iv_back);

        iv.setOnClickListener(v -> finish());
        RecyclerView mRecyclerView = findViewById(R.id.rv_nearby);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mPlacesArray = (ArrayList<GooglePlace>) getIntent().getSerializableExtra(SmartFunctionsConstants.SMART_FUNCTION_IT_NEARBY_LIST);

        NearbyAdapter mAdapter = new NearbyAdapter(mPlacesArray);
        mAdapter.setOnItemClickListener(position -> {
            String uri = "geo:0,0?q=+" + mPlacesArray.get(position).getVicinity();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            Intent chooser = Intent.createChooser(intent, getString(R.string.favorite_map));
            startActivity(chooser);
        });
        mRecyclerView.setAdapter(mAdapter);
    }
}
