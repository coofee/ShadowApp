package com.coofee.shadowapp;

import android.annotation.SuppressLint;
import android.app.Service;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.shadow.ShadowApplication;
import android.shadow.ShadowContext;
import android.shadow.ShadowLog;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testActivity();

        testGetApplicationContext();

        testShadowContext();

        testShadowApplication();
    }

    @SuppressLint("MissingPermission")
    private void testActivity() {
        ShadowLog.e("MainActivity.testActivity()=" + this);

        try {
            WifiManager wifiManager = (WifiManager) getSystemService(Service.WIFI_SERVICE);
            ShadowLog.e("MainActivity.testActivity; wifiManager=" + wifiManager);
            List<ScanResult> scanResults = wifiManager.getScanResults();
        } catch (Throwable e) {
            ShadowLog.e("fail testActivity", e);
        }

        try {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
            ShadowLog.e("MainActivity.testActivity; telephonyManager=" + telephonyManager);
            telephonyManager.getDeviceId();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                telephonyManager.requestCellInfoUpdate(Executors.newSingleThreadExecutor(), new TelephonyManager.CellInfoCallback() {
                    @Override
                    public void onCellInfo(@NonNull List<CellInfo> cellInfo) {

                    }
                });
            }
        } catch (Throwable e) {
            ShadowLog.e("fail testActivity", e);
        }

        try {
            LocationManager locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
            ShadowLog.e("MainActivity.testActivity; locationManager=" + locationManager);
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (Throwable e) {
            ShadowLog.e("fail testActivity", e);
        }

    }

    @SuppressLint("MissingPermission")
    private void testGetApplicationContext() {
        ShadowLog.e("MainActivity.testGetApplicationContext()=" + getApplicationContext());

        try {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Service.WIFI_SERVICE);
            ShadowLog.e("MainActivity.testGetApplicationContext; wifiManager=" + wifiManager);
            List<ScanResult> scanResults = wifiManager.getScanResults();
        } catch (Throwable e) {
            ShadowLog.e("fail testGetApplicationContext", e);
        }

        try {
            TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
            ShadowLog.e("MainActivity.testGetApplicationContext; telephonyManager=" + telephonyManager);
            telephonyManager.getDeviceId();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                telephonyManager.requestCellInfoUpdate(Executors.newSingleThreadExecutor(), new TelephonyManager.CellInfoCallback() {
                    @Override
                    public void onCellInfo(@NonNull List<CellInfo> cellInfo) {

                    }
                });
            }
        } catch (Throwable e) {
            ShadowLog.e("fail testGetApplicationContext", e);
        }

        try {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Service.LOCATION_SERVICE);
            ShadowLog.e("MainActivity.testGetApplicationContext; locationManager=" + locationManager);
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (Throwable e) {
            ShadowLog.e("fail testGetApplicationContext", e);
        }

    }

    @SuppressLint("MissingPermission")
    private void testShadowContext() {
        ShadowContext shadowContext = ShadowContext.shadow(getApplication());
        shadowContext.getApplicationContext();

        ShadowLog.e("MainActivity.testShadowContext; shadowContext=" + shadowContext +
                ", shadowContext.shadowApplication=" + shadowContext.shadowApplication +
                ", shadowContext.getBaseContext=" + shadowContext.getBaseContext() +
                ", getApplicationContext=" + getApplicationContext() +
                ", getApplication=" + getApplication() +
                ", getApplication.getBaseContext=" + getApplication().getBaseContext());

        try {
            WifiManager wifiManager = (WifiManager) shadowContext.getApplicationContext().getSystemService(Service.WIFI_SERVICE);
            ShadowLog.e("MainActivity.testShadowContext; wifiManager=" + wifiManager);
            List<ScanResult> scanResults = wifiManager.getScanResults();
        } catch (Throwable e) {
            ShadowLog.e("fail testShadowContext", e);
        }

        TelephonyManager telephonyManager = (TelephonyManager) shadowContext.getSystemService(Service.TELEPHONY_SERVICE);
        ShadowLog.e("MainActivity.testShadowContext; telephonyManager=" + telephonyManager);
        try {
            telephonyManager.getDeviceId();
        } catch (Throwable e) {
            ShadowLog.e("fail testShadowContext", e);
        }

        try {
            LocationManager locationManager = (LocationManager) shadowContext.getBaseContext().getSystemService(Service.LOCATION_SERVICE);
            ShadowLog.e("MainActivity.testShadowContext; locationManager=" + locationManager);
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (Throwable e) {
            ShadowLog.e("fail testShadowContext", e);
        }
    }

    @SuppressLint("MissingPermission")
    private void testShadowApplication() {
        ShadowApplication shadowApplication = ShadowApplication.shadow(getApplication());
        shadowApplication.getApplicationContext();

        ShadowLog.e("MainActivity.testShadowApplication; shadowApplication=" + shadowApplication +
                ", shadowApplication.application=" + shadowApplication.application +
                ", shadowApplication.getBaseContext=" + shadowApplication.getBaseContext() +
                ", getApplicationContext=" + getApplicationContext() +
                ", getApplication=" + getApplication() +
                ", getApplication.getBaseContext=" + getApplication().getBaseContext());

        try {
            WifiManager wifiManager = (WifiManager) shadowApplication.getApplicationContext().getSystemService(Service.WIFI_SERVICE);
            ShadowLog.e("MainActivity.testShadowApplication; wifiManager=" + wifiManager);
            List<ScanResult> scanResults = wifiManager.getScanResults();
        } catch (Throwable e) {
            ShadowLog.e("fail testShadowApplication", e);
        }

        TelephonyManager telephonyManager = (TelephonyManager) shadowApplication.getSystemService(Service.TELEPHONY_SERVICE);
        ShadowLog.e("MainActivity.testShadowApplication; telephonyManager=" + telephonyManager);
        try {
            telephonyManager.getDeviceId();
        } catch (Throwable e) {
            ShadowLog.e("fail testShadowApplication", e);
        }

        try {
            LocationManager locationManager = (LocationManager) shadowApplication.getBaseContext().getSystemService(Service.LOCATION_SERVICE);
            ShadowLog.e("MainActivity.testShadowApplication; locationManager=" + locationManager);
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (Throwable e) {
            ShadowLog.e("fail testShadowApplication", e);
        }
    }
}