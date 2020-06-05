package com.example.smartcane.smart_functions.location_preferences;

import com.example.smartcane.smart_functions.api_google.model.GooglePlace;

import java.util.ArrayList;

public interface AsyncResponse {
    void processFinish(Object output);
    void startLoading();
}