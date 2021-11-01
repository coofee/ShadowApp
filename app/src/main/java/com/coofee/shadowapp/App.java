package com.coofee.shadowapp;

import android.app.Application;
import android.content.Context;
import android.os.SystemClock;
import android.shadow.ShadowConfig;
import android.shadow.ShadowLog;
import android.shadow.ShadowServiceManager;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import com.coofee.shadow.BuildConfig;
import com.coofee.shadow.stats.ShadowStatsConfig;
import com.coofee.shadow.stats.ShadowStatsListener;
import com.coofee.shadow.stats.ShadowStatsManager;
import com.coofee.shadowapp.shadow.activity.IActivityManagerInterceptor;
import com.coofee.shadowapp.shadow.activity.IActivityTaskManagerInterceptor;
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

        final String TAG = "ShadowApp";
        final long startTime = SystemClock.uptimeMillis();
        ShadowStatsManager.init(
                new ShadowStatsConfig.Builder(base)
                        .tag(TAG)
                        .mode(ShadowStatsConfig.MODE_LOCAL_FILE)
                        .openLog(false)
//                        .localFilePath("/data/local/tmp/shadow.js")
                        .build(),
                new ShadowStatsManager.OnInitCallback() {
                    @Override
                    public void onSuccess() {
                        final long endTime = SystemClock.uptimeMillis();
                        Log.e(TAG, "ShadowStatsManager init success; consume=" + (endTime - startTime));
                    }

                    @Override
                    public void onError(String msg, Throwable e) {
                        final long endTime = SystemClock.uptimeMillis();
                        Log.e(TAG, "ShadowStatsManager init fail; consume=" + (endTime - startTime) + ", msg=" + msg, e);
                    }
                }).addStatsListener(new ShadowStatsListener() {
            @Override
            public void on(String type, String json) {
                Log.e(TAG, "on: type=" + type + ", json=" + json);
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

        ShadowLog.debug = BuildConfig.DEBUG;
        ShadowConfig.Builder shadowConfigBuilder = new ShadowConfig.Builder(base, this)
                .interceptAll(true)
                .debug(BuildConfig.DEBUG);

        shadowConfigBuilder
                .add(new IWifiManagerInterceptor())
                .add(new ILocationManagerInterceptor())
                .add(new ITelephonyInterceptor())
                .add(new IPhoneSubInfoInterceptor())
                .add(new IActivityManagerInterceptor())
                .add(new IActivityTaskManagerInterceptor())
                .add(new IPermissionManagerInterceptor())
                .add(new IPackageManagerInterceptor())
        ;

        ShadowServiceManager.init(shadowConfigBuilder.build());
    }

}
