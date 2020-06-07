package com.example.smartcane.utils;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcane.R;


public class BaseActivity extends AppCompatActivity {

    protected AlertDialog.Builder mDialogBuilder;
    protected Dialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialogBuilder = new AlertDialog.Builder(this);
        mDialogBuilder.setView(R.layout.progress_bar);
        mDialog = mDialogBuilder.create();
        mDialog.setCancelable(false);
    }

    public void showProgressDialog() {
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
