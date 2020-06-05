package com.example.smartcane.state;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smartcane.R;
import com.example.smartcane.initial_register.InitialRegisterActivity;
import com.example.smartcane.intro.activity.IntroActivity;
import com.example.smartcane.smart_functions.activity.SmartFunctionsActivity;
import com.example.smartcane.bluetooth_connection.activity.BluetoothActivity;
import com.example.smartcane.smart_functions.location_preferences.LocationPreferencesActivity;

import java.util.Objects;

import static com.example.smartcane.state.StateConstants.*;

public class StateActivity extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.empty_activity);
        mSharedPreferences = getApplicationContext().getSharedPreferences(STATE_CONSTANTS_SP_NAME, MODE_PRIVATE);
        runApp();
    }

    private void runApp() {
        Intent intent = null;
        if (!checkAcceptedPermissions()) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                askForPermissions(Manifest.permission.ACCESS_FINE_LOCATION);
            } else askForPermissions(Manifest.permission.SEND_SMS);
        } else {
            if (mSharedPreferences.getBoolean(StateConstants.STATE_CONSTANTS_SP_SHOW_INTRO, true)) {
                intent = new Intent(this, IntroActivity.class);
            } else if (mSharedPreferences.getBoolean(StateConstants.STATE_CONSTANTS_SP_SHOW_BLUETOOTH_LIST, true)) {
                intent = new Intent(this, BluetoothActivity.class);
            } else if (mSharedPreferences.getBoolean(StateConstants.STATE_CONSTANTS_SP_SHOW_INITIAL_REGISTER, true)) {
                intent = new Intent(this, InitialRegisterActivity.class);
            } else if(mSharedPreferences.getBoolean(STATE_CONSTANTS_SP_SHOW_LOCALE_PREFERENCES, true)){
                intent = new Intent(this, LocationPreferencesActivity.class);
            } else intent = new Intent(this, SmartFunctionsActivity.class);
        }
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private boolean checkAcceptedPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void askForPermissions(String permission) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                permission)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permissões de " + ((permission.equals(Manifest.permission.SEND_SMS)) ? "SMS" : "Localização") + " necessárias")
                    .setMessage("As permissões são necessárias para o funcionamento correto do aplicativo. Não enviamos seus dados para locais externos.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        ActivityCompat.requestPermissions(StateActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STATE_CONSTANTS_STORAGE_PERMISSION_CODE);
                        runApp();
                    })
                    .setNegativeButton("FECHAR", (dialog, which) -> {
                        dialog.dismiss();
                        runApp();
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION}, STATE_CONSTANTS_STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STATE_CONSTANTS_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                runApp();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
