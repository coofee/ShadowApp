package com.coofee.shadowapp.test;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.shadow.ShadowServiceManager;
import android.util.Log;

import java.util.List;

public class TestLocationManager {
    @SuppressLint("MissingPermission")
    public static void test(Context context) {
        Log.d(ShadowServiceManager.TAG, "TestLocationManager.test");
        LocationManager locationManager = (LocationManager) context.getSystemService(Service.LOCATION_SERVICE);

        TestUtil.exec(() -> locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));


        List<String> allProviders = locationManager.getAllProviders();
        if (allProviders != null) {
            for (String provider : allProviders) {
                TestUtil.exec(() -> locationManager.requestLocationUpdates(provider, 200, 100, new LocationListener() {

                    @Override
                    public void onLocationChanged(Location location) {
                        Log.d(ShadowServiceManager.TAG, "location=" + location);
                        locationManager.removeUpdates(this);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                }));
            }
        }
    }
}
