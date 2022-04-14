package com.coofee.shadowapp;

import android.app.Application;
import android.content.Context;
import android.os.SystemClock;
import android.shadow.ShadowConfig;
import android.shadow.ShadowDevTools;
import android.shadow.ShadowLog;
import android.shadow.ShadowServiceManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.coofee.componentmonitor.ComponentMonitor;
import com.coofee.componentmonitor.util.LogComponentObserver;
import com.coofee.componentmonitor.util.ProcessUtil;
import com.coofee.shadow.stats.ShadowStatsConfig;
import com.coofee.shadow.stats.ShadowStatsListener;
import com.coofee.shadow.stats.ShadowStatsManager;
import com.coofee.shadowapp.shadow.location.ILocationManagerInterceptor;
import com.coofee.shadowapp.shadow.os.OsInterceptor;
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

        if (!TextUtils.equals(base.getPackageName(), ProcessUtil.getProcessName())) {
            return;
        }

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
                Log.e(TAG, "ShadowStatsManager on: type=" + type + ", json=" + json);
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
        final long startTime = SystemClock.elapsedRealtime();
        final long startThreadTime = SystemClock.currentThreadTimeMillis();
        if (Reflection.unseal(base) != 0) {
            Log.e(ShadowServiceManager.TAG, "fail Reflection.unseal().");
            return;
        }
        Log.e(ShadowServiceManager.TAG, "success Reflection.unseal().");

        ShadowConfig.Builder shadowConfigBuilder = new ShadowConfig.Builder(base, this)
                .debug(BuildConfig.DEBUG)
                .devTools(ShadowDevTools.DEFAULT_DEV_TOOLS)
                .interceptAll(true)
//                .addPackageOrClassNamePrefix("com.coofee.shadowapp")
                .logMode(ShadowLog.DEBUG);

        shadowConfigBuilder
                .add(new IWifiManagerInterceptor())
                .add(new ILocationManagerInterceptor())
                .add(new ITelephonyInterceptor())
                .add(new IPhoneSubInfoInterceptor())
                .add(new IPermissionManagerInterceptor())
                .add(new IPackageManagerInterceptor())
                .add(new OsInterceptor())
        ;

        ComponentMonitor.getInstance()
                .add(new LogComponentObserver())
                .attachTo(shadowConfigBuilder)
        ;

        ShadowServiceManager.init(shadowConfigBuilder.build());
        final long endTime = SystemClock.elapsedRealtime();
        final long endThreadTime = SystemClock.currentThreadTimeMillis();
        ShadowLog.d("initShadowManager cost " + (endTime - startTime) + ", thread time " + (endThreadTime - startThreadTime));
    }

}
