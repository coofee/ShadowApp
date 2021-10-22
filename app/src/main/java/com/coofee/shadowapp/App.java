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
import com.coofee.shadow.stats.OnStatsListener;
import com.coofee.shadow.stats.ShadowStatsManager;
import com.coofee.shadowapp.shadow.activity.IActivityManagerInterceptor;
import com.coofee.shadowapp.shadow.location.ILocationManagerInterceptor;
import com.coofee.shadowapp.shadow.permission.IPermissionManagerInterceptor;
import com.coofee.shadowapp.shadow.pm.IPackageManagerInterceptor;
import com.coofee.shadowapp.shadow.telephony.IPhoneSubInfoInterceptor;
import com.coofee.shadowapp.shadow.telephony.ITelephonyInterceptor;
import com.coofee.shadowapp.shadow.wifi.IWifiManagerInterceptor;
import dalvik.system.BaseDexClassLoader;
import me.weishu.reflection.Reflection;

import java.io.File;

public class App extends Application {

    private static Context sContext;

    private static boolean sIsBackground = false;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        ShadowStatsManager.init(base, new ShadowStatsManager.OnInitCallback() {
            @Override
            public void onSuccess() {
                Log.e("ShadowStatsManager", "ShadowStatsManager init success.");
            }

            @Override
            public void onError(String msg, Throwable e) {
                Log.e("ShadowStatsManager", "ShadowStatsManager init fail; " + msg, e);
            }
        }).addOnStatsListener(new OnStatsListener() {
            @Override
            public void onAttach() {

            }

            @Override
            public void on(String type, String json) {
                Log.e("ShadowStatsManager", "on: type=" + type + ", json=" + json);
            }

            @Override
            public void onDetach() {

            }
        });


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
