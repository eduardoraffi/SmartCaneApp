package com.example.smartcane.smart_functions.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.example.smartcane.R;
import com.example.smartcane.bluetooth_connection.connection_utils.SerialListener;
import com.example.smartcane.bluetooth_connection.connection_utils.SerialService;
import com.example.smartcane.bluetooth_connection.connection_utils.SerialSocket;
import com.example.smartcane.initial_register.InitialRegisterActivity;
import com.example.smartcane.smart_functions.SmartFunctionsConstants;
import com.example.smartcane.smart_functions.api_google.GooglePlaces;
import com.example.smartcane.smart_functions.location_preferences.AsyncResponse;
import com.example.smartcane.smart_functions.location_preferences.LocationPreferencesActivity;
import com.example.smartcane.smart_functions.nearby_locations.NearbyActivity;
import com.example.smartcane.utils.BaseActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import static com.example.smartcane.initial_register.InitialRegisterConstants.INITIAL_REGISTER_CONSTANTS_SP_EMERGENCY_NUMBER;
import static com.example.smartcane.initial_register.InitialRegisterConstants.INITIAL_REGISTER_CONSTANTS_SP_FROM_SFA;
import static com.example.smartcane.initial_register.InitialRegisterConstants.INITIAL_REGISTER_CONSTANTS_SP_USER_ADDRESS;
import static com.example.smartcane.initial_register.InitialRegisterConstants.INITIAL_REGISTER_CONSTANTS_SP_USER_HEIGHT;
import static com.example.smartcane.smart_functions.SmartFunctionsConstants.SMART_FUNCTION_IT_NEARBY_LIST;
import static com.example.smartcane.state.StateConstants.STATE_CONSTANTS_SP_NAME;


public class SmartFunctionsActivity extends BaseActivity implements SerialListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, AsyncResponse {

    private enum Connected {False, Pending, True}

    private SharedPreferences mSharedPreferences;

    private TextView tv_connection_state;
    private TextView tv_distance;
    private Button btn_sync_cane;
    private Button btn_open_map;
    private Button btn_send_sms;
    private Button btn_talk_location;
    private Button btn_height_redefinition;
    private Button btn_configure_my_locations;
    private Button btn_arround_me;

    private Context mContext;
    private TextToSpeech textToSpeech;
    private String deviceAddress;
    private Connected connected = Connected.False;
    private SerialSocket socket;
    private SerialService service;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = getApplicationContext().getSharedPreferences(STATE_CONSTANTS_SP_NAME, MODE_PRIVATE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Objects.requireNonNull(getSupportActionBar()).hide();
        deviceAddress = mSharedPreferences.getString(SmartFunctionsConstants.SMART_FUNCTION_SP_BLUETOOTH_ADDRESS, "");
        setContentView(R.layout.activity_smart_functions);
        setupUi();
        setupButtonClicks();
        startBtService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (connected == Connected.False) {
            tv_connection_state.setText("Desconectado. Aperte em Sincronizar bengala.");
            connectionDialog();
        } else {
            tv_connection_state.setText("Bengala Conectada.");
        }
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                    .addApi(LocationServices.API).addOnConnectionFailedListener(this).build();
        }
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    private void connectionDialog() {
        new AlertDialog.Builder(mContext).setTitle("Bengala Desconectada")
                .setMessage("Conecte-se em sua bengala.")
                .setPositiveButton("Sim", (dialog, which) -> {
                    connect();
                }).setNegativeButton("Agora não", (dialog, which) -> {
            dialog.dismiss();
        }).create().show();
    }

    private void startBtService() {
        if (service != null)
            service.attach(this);
        else
            service = new SerialService();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(mContext, SerialService.class));
        } else {
            startService(new Intent(mContext, SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    private void setupUi() {
        mContext = this;

        tv_connection_state = findViewById(R.id.tv_connection_state);
        tv_distance = findViewById(R.id.tv_distance);
        btn_sync_cane = findViewById(R.id.btn_sync_cane);
        btn_open_map = findViewById(R.id.btn_open_map);
        btn_send_sms = findViewById(R.id.btn_send_sms);
        btn_talk_location = findViewById(R.id.btn_talk_location);
        btn_height_redefinition = findViewById(R.id.btn_height_redefinition);
        btn_configure_my_locations = findViewById(R.id.btn_configure_my_locations);
        btn_arround_me = findViewById(R.id.btn_arround_me);

        textToSpeech = new TextToSpeech(mContext, status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.getDefault());
                textToSpeech.setPitch(1.3f);
                textToSpeech.setSpeechRate(1f);
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(mContext, "This language is not supported", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.v("TTS", "onInit succeeded");
                    }
                } else {
                    Toast.makeText(mContext, "Initialization failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupButtonClicks() {
        btn_sync_cane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connected == Connected.False) {
                    syncCane();
                } else
                    Toast.makeText(mContext, "Bengala já está conectada, aproveite.", Toast.LENGTH_SHORT).show();
            }
        });

        btn_open_map.setOnClickListener(v -> openMap());

        btn_send_sms.setOnClickListener(v -> sendSmsMsgFnc());

        btn_talk_location.setOnClickListener(v -> talkLocation(localeString()));

        btn_height_redefinition.setOnClickListener(v -> changeInitialConfigs());

        btn_configure_my_locations.setOnClickListener(v -> setupMyLocations());

        btn_arround_me.setOnClickListener(v -> loadNearbyPlaces());
    }

    private void loadNearbyPlaces() {
        ArrayList<String> urlList = new ArrayList<>();

        if (mLocation != null) {
            for (String place : getResources().getStringArray(R.array.nearbyLocalesForMap)) {
                if (!mSharedPreferences.getString(place, "").equals("")) {
                    urlList.add("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                            + mLocation.getLatitude() + "," + mLocation.getLongitude()
                            + "&radius=500" +
                            "&type=" + place +
                            "&key=AIzaSyDH67wQ5bWs2-nzX7avU9s2L5DbfygIQWA");
                }
            }

        } else return;

        GooglePlaces places = new GooglePlaces(urlList, this);
        places.execute(mContext);
    }

    @Override
    public void processFinish(Object output) {
        dismissProgressDialog();
        if (output instanceof ArrayList
                && ((ArrayList) output).size() > 0) {
            Intent intent = new Intent(mContext, NearbyActivity.class);
            intent.putExtra(SMART_FUNCTION_IT_NEARBY_LIST, (ArrayList) output);
            startActivity(intent);
        }
    }

    @Override
    public void startLoading() {
        showProgressDialog();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (mLocation == null) {
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    mLocation = location;
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("Failed " + connectionResult.getErrorMessage());
    }

    private void syncCane() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
        } else if (connected == Connected.False) {
            connect();
        }
    }

    private void openMap() {
        String uri = "geo:0,0?q=+" + mSharedPreferences.getString(INITIAL_REGISTER_CONSTANTS_SP_USER_ADDRESS, "");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        Intent chooser = Intent.createChooser(intent, "Escolha o mapa preferido");
        startActivity(chooser);
    }

    void sendSmsMsgFnc() {
        String messageToSend = "Estou em perigo: " + localeString();
        String number = mSharedPreferences.getString(INITIAL_REGISTER_CONSTANTS_SP_EMERGENCY_NUMBER, "");
        PendingIntent sentPI;
        String SENT = "SMS_SENT";
        sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                SmsManager smsMgrVar = SmsManager.getDefault();
                smsMgrVar.sendTextMessage(number, null, messageToSend, sentPI, null);
                showAlertDialog("Mensagem enviada", "Que a força esteja com você!");

            } catch (Exception ErrVar) {
                ErrVar.printStackTrace();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 10);
            }
        }

    }

    private String localeString() {
        Geocoder geocoder;
        if (mLocation != null) {
            double longitude = mLocation.getLongitude();
            double latitude = mLocation.getLatitude();
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                return geocoder.getFromLocation(latitude, longitude, 5).get(0).getAddressLine(0); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "Não foi possível encontrar sua localização";
    }

    private void talkObjectDistance(String message) {
        if (!textToSpeech.isSpeaking()) {
            try {
                textToSpeech.setLanguage(new Locale("pt_BR"));

                Bundle bundle = new Bundle();
                bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
                textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, bundle, null);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void talkLocation(String message) {
        try {
            textToSpeech.setLanguage(new Locale("pt_BR"));

            Bundle bundle = new Bundle();
            bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, bundle, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeInitialConfigs() {
        Intent intent = new Intent(this, InitialRegisterActivity.class);
        intent.putExtra(INITIAL_REGISTER_CONSTANTS_SP_FROM_SFA, true);
        startActivity(intent);
    }

    private void setupMyLocations() {
        Intent intent = new Intent(SmartFunctionsActivity.this, LocationPreferencesActivity.class);
        startActivity(intent);
    }

    private void connect() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            String deviceName = device.getName() != null ? device.getName() : device.getAddress();
            status("connecting...");
            connected = Connected.Pending;
            socket = new SerialSocket();
            service.connect(this, "Connected to " + deviceName);
            socket.connect(mContext, service, device);
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    private void disconnect() {
        connected = Connected.False;
        service.disconnect();
        socket.disconnect();
        socket = null;
        tv_connection_state.setText("Dispositivo Desconectado");
    }

    private void send(String userHeight) {
        if (connected != Connected.True) {
            showAlertDialog("Dispositivo Desconectado", "Não foi possível conectar em sua Smart Cane.");
            return;
        }
        try {
            byte[] data = (userHeight).getBytes();
            socket.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    private void receive(byte[] data) {
        String receivedData = new String(data);
        System.out.println(receivedData);
        if (receivedData.equals("a")) {
            talkLocation(localeString());
        } else if (receivedData.equals("b")) {
            sendSmsMsgFnc();
        } else {
            try {
                Integer.parseInt(receivedData);
                tv_distance.setText(receivedData + " CM");
                talkObjectDistance("Objeto a " + receivedData + "centímetros de sua mão");
            } catch (NumberFormatException e) {
                if (receivedData.equals("a")) {
                    talkLocation(localeString());
                } else if (receivedData.equals("b")) {
                    sendSmsMsgFnc();
                }
            }
        }

    }

    private void status(String str) {
        Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
    }

    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(mContext).setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", (dialog, which) -> {
                    dialog.dismiss();
                }).create().show();
    }

    /*
     * SerialListener
     */
    @Override
    public void onSerialConnect() {
        status("connected");
        connected = Connected.True;
        send(mSharedPreferences.getString(INITIAL_REGISTER_CONSTANTS_SP_USER_HEIGHT, "160"));
        tv_connection_state.setText("Conectado");
    }

    @Override
    public void onSerialConnectError(Exception e) {
        status("connection failed: " + e.getMessage());
        disconnect();
        showAlertDialog("Dispositivo Desconectado", "Não foi possível conectar em sua Smart Cane.");
        tv_connection_state.setText("Desconectado. Aperte em Sincronizar bengala");
        connected = Connected.False;
    }

    @Override
    public void onSerialRead(byte[] data) {
        receive(data);
    }

    @Override
    public void onSerialIoError(Exception e) {
        status("connection lost: " + e.getMessage());
        tv_connection_state.setText("Desconectado. Aperte em Sincronizar bengala");
        showAlertDialog("Dispositivo Desconectado", "Não foi possível conectar em sua Smart Cane.");
        disconnect();
        connected = Connected.False;
    }
}
