package com.coofee.shadowapp;

import android.content.Context;
import android.location.ShadowLocationManager;
import android.location.ShadowLocationManagerProvider;
import android.net.wifi.ShadowWifiManager;
import android.net.wifi.ShadowWifiManagerProvider;
import android.shadow.ShadowApplication;
import android.shadow.ShadowConfig;
import android.shadow.ShadowLog;
import android.shadow.ShadowServiceManager;
import android.telephony.ShadowTelephonyManager;
import android.telephony.ShadowTelephonyManagerProvider;

public class App extends ShadowApplication {

    @Override
    protected void attachBaseContext(Context base) {
//        if (base == this) {
//            return;
//        }
        super.attachBaseContext(base);
        initShadowManager(base);
    }

    private void initShadowManager(Context base) {
        BootstrapClass.exempt("Landroid");

        ShadowConfig.Builder shadowConfigBuilder = new ShadowConfig.Builder(base, this);

        if (BuildConfig.DEBUG) {
            ShadowLog.debug = true;
            shadowConfigBuilder.interceptAll(true).debug(true);
            ShadowLog.e("debug or test, we will intercept all.");

        } else {
            shadowConfigBuilder.interceptAll(false).debug(false);
        }

        try {
            shadowConfigBuilder.addLocationManager(new ShadowLocationManager(), new ShadowLocationManagerProvider.Adapter());
        } catch (Throwable e) {
            ShadowLog.e("fail create ShadowLocationManager", e);
        }

        try {
            shadowConfigBuilder.addTelephonyManager(new ShadowTelephonyManager(base), new ShadowTelephonyManagerProvider.Adapter());
        } catch (Throwable e) {
            ShadowLog.e("fail create ShadowTelephonyManager", e);
        }

        try {
            shadowConfigBuilder.addWifiManager(ShadowWifiManager.create(base), new ShadowWifiManagerProvider.Adapter());
        } catch (Throwable e) {
            ShadowLog.e("fail create ShadowWifiManager", e);
        }

        ShadowServiceManager.init(shadowConfigBuilder.build());
    }

}
