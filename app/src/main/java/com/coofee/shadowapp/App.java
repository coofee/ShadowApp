package com.coofee.shadowapp;

import android.app.Application;
import android.content.Context;
import android.shadow.ShadowConfig;
import android.shadow.ShadowLog;
import android.shadow.ShadowServiceManager;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import com.coofee.shadow.BuildConfig;
import com.coofee.shadowapp.shadow.activity.IActivityManagerInterceptor;
import com.coofee.shadowapp.shadow.location.ILocationManagerInterceptor;
import com.coofee.shadowapp.shadow.permission.IPermissionManagerInterceptor;
import com.coofee.shadowapp.shadow.pm.IPackageManagerInterceptor;
import com.coofee.shadowapp.shadow.telephony.IPhoneSubInfoInterceptor;
import com.coofee.shadowapp.shadow.telephony.ITelephonyInterceptor;
import com.coofee.shadowapp.shadow.wifi.IWifiManagerInterceptor;
import me.weishu.reflection.Reflection;

public class App extends Application {

    private static Context sContext;

    private static boolean sIsBackground = false;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // readelf -a frida-gadget-15.1.6-android-arm64.so | grep NAME
        // readelf -a frida-gadget-15.1.6-android-arm64.so | grep NEED
        // logcat TAG: Frida
        // clone PythonStudy 仓库获取代码，链接该程序进行测试。
        // $ python3 frida_shadow_test.py
        // $ frida -U Gadget -l frida_hook.js
        System.loadLibrary("frida-gadget");

        sContext = this;
        initShadowManager(base);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new LifecycleObserver() {

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            public void onAppForeground() {
                sIsBackground = false;
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            public void onAppBackground() {
                sIsBackground = true;
            }
        });

    }

    public static boolean isBackground() {
        return sIsBackground;
    }

    public static Context getContext() {
        return sContext;
    }

    private void initShadowManager(Context base) {
        if (Reflection.unseal(base) == 0) {
            Log.e(ShadowServiceManager.TAG, "success Reflection.unseal().");
        } else {
            Log.e(ShadowServiceManager.TAG, "fail Reflection.unseal().");
        }

        ShadowConfig.Builder shadowConfigBuilder = new ShadowConfig.Builder(base, this);

        if (BuildConfig.DEBUG) {
            ShadowLog.debug = true;
            shadowConfigBuilder.interceptAll(true).debug(true);
            ShadowLog.d("debug or test, we will intercept all.");

        } else {
            shadowConfigBuilder.interceptAll(false).debug(false);
        }

        shadowConfigBuilder
                .add(new IWifiManagerInterceptor())
                .add(new ILocationManagerInterceptor())
                .add(new ITelephonyInterceptor())
                .add(new IPhoneSubInfoInterceptor())
                .add(new IActivityManagerInterceptor())
                .add(new IPermissionManagerInterceptor())
                .add(new IPackageManagerInterceptor())
        ;

        ShadowServiceManager.init(shadowConfigBuilder.build());
    }

}
