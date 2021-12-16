package com.coofee.shadowapp;

import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.os.SystemClock;
import android.shadow.ShadowConfig;
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
import com.coofee.shadow.BuildConfig;
import com.coofee.shadow.stats.ShadowStatsConfig;
import com.coofee.shadow.stats.ShadowStatsListener;
import com.coofee.shadow.stats.ShadowStatsManager;
import com.coofee.shadowapp.shadow.location.ILocationManagerInterceptor;
import com.coofee.shadowapp.shadow.permission.IPermissionManagerInterceptor;
import com.coofee.shadowapp.shadow.pm.IPackageManagerInterceptor;
import com.coofee.shadowapp.shadow.telephony.IPhoneSubInfoInterceptor;
import com.coofee.shadowapp.shadow.telephony.ITelephonyInterceptor;
import com.coofee.shadowapp.shadow.wifi.IWifiManagerInterceptor;
import me.weishu.reflection.Reflection;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class App extends Application {

    private static Context sContext;

    private static boolean sIsBackground = false;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        if (!ProcessUtil.isMainProcess(base)) {
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
                .add(new IPermissionManagerInterceptor())
                .add(new IPackageManagerInterceptor())
        ;

        ComponentMonitor.getInstance()
                .add(new LogComponentObserver())
                .attachTo(shadowConfigBuilder)
        ;

        ShadowServiceManager.init(shadowConfigBuilder.build());
    }

    public static class ProcessUtil {
        private static final String TAG = ProcessUtil.class.getSimpleName();
        private static String processName;

        public ProcessUtil() {
        }

        public static String getProcessName() {
            if (TextUtils.isEmpty(processName)) {
                processName = "";
                BufferedReader cmdlineReader = null;

                try {
                    cmdlineReader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/" + Process.myPid() + "/cmdline"), "iso-8859-1"));
                    StringBuilder builder = new StringBuilder();

                    int c;
                    while ((c = cmdlineReader.read()) > 0) {
                        builder.append((char) c);
                    }

                    builder.trimToSize();
                    processName = builder.toString();
                    Log.d(TAG, "current process name is: " + processName);
                } catch (Exception var11) {
                    Log.e(TAG, "read process name error", var11);
                } finally {
                    if (cmdlineReader != null) {
                        try {
                            cmdlineReader.close();
                        } catch (IOException var10) {
                            Log.e(TAG, "close stream error", var10);
                        }
                    }

                }
            }

            return processName;
        }

        public static boolean isMainProcess(Context context) {
            return context.getPackageName().equals(getProcessName());
        }
    }

}
