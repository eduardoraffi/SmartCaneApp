package com.example.smartcane.bluetooth_connection.connection_utils;

import com.example.smartcane.BuildConfig;

class BluetoothConstants {

    // values have to be globally unique
    static final String INTENT_ACTION_DISCONNECT = BuildConfig.APPLICATION_ID + ".Disconnect";
    static final String NOTIFICATION_CHANNEL = BuildConfig.APPLICATION_ID + ".Channel";
    static final String INTENT_CLASS_MAIN_ACTIVITY = BuildConfig.APPLICATION_ID + ".MainActivity";
    static final String SHARED_PREFERENCES_BLUETOOTH_NAME = "smart_cane_bt_name";
    static final String SHARED_PREFERENCES_EMERGENCY_NUMBER = "emergency_number";

    // values have to be unique within each app
    static final int NOTIFY_MANAGER_START_FOREGROUND_SERVICE = 1001;

    private BluetoothConstants() {
    }
}
