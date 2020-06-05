package com.example.smartcane.smart_functions.api_google;

import android.app.Application;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocalizationUtils implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Context mContext;

    public LocalizationUtils(Context context) {
        mContext = context;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest().create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(location != null){
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

                    List<Address> addresses = null; // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 5);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(addresses != null)
                    {
                        String address = addresses.get(0).getAddressLine(0);
                        System.out.println("Endereço encontrado: "+address);
                    }
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
        System.out.println("Failed "+ connectionResult.getErrorMessage());
    }
}
