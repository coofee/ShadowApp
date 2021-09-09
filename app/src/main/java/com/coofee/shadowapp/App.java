package com.coofee.shadowapp;

import android.app.Activity;
import android.content.Context;
import android.location.ShadowLocationManager;
import android.location.ShadowLocationManagerProvider;
import android.net.wifi.ShadowWifiManager;
import android.net.wifi.ShadowWifiManagerProvider;
import android.os.Build;
import android.os.Bundle;
import android.shadow.*;
import android.telephony.ShadowTelephonyManager;
import android.telephony.ShadowTelephonyManagerProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class App extends ShadowApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        initShadowManager(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // intercept activity context
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                Context baseContext = activity.getBaseContext();
                ShadowContext shadowContext = new ShadowContext(application, baseContext);
                ReflectUtil.setContextWrapper(activity, shadowContext);
                ShadowLog.e(Build.VERSION.SDK_INT + ", replace activity=" + activity + " baseContext=" + baseContext + " by shadowContext=" + shadowContext);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
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
