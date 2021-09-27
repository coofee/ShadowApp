package com.coofee.shadowapp;

import android.app.Application;
import android.content.Context;
import android.shadow.ReflectUtil;
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

import java.lang.reflect.Field;

public class App extends Application {

    private static Context sContext;

    private static boolean sIsBackground = false;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        initShadowManager(base);
        sContext = this;
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


        try {
            Class<?> class_ActivityThread = Class.forName("android.app.ActivityThread");
            Field field_sPackageManager = class_ActivityThread.getDeclaredField("sPackageManager");
            field_sPackageManager.setAccessible(true);
            Object originPackageManager = field_sPackageManager.get(null);
            if (originPackageManager != null) {
                Object packageManager = ShadowServiceManager.getService("package");
                if (packageManager != null) {
                    ShadowLog.e("originPackageManager=" + originPackageManager + ", packageManager=" + packageManager);
                    field_sPackageManager.set(null, packageManager);
                }
            }

            Field field_sPermissionManager = class_ActivityThread.getDeclaredField("sPermissionManager");
            field_sPermissionManager.setAccessible(true);
            Object originPermissionManager = field_sPermissionManager.get(null);
            if (originPermissionManager != null) {
                Object permissionmgr = ShadowServiceManager.getService("permissionmgr");
                if (permissionmgr != null) {
                    ShadowLog.e("originPermissionManager=" + originPackageManager + ", permissionmgr=" + permissionmgr);
                    field_sPermissionManager.set(null, permissionmgr);
                }
            }
        } catch (Throwable e) {
            ShadowLog.e("fail replace sPackageManager or sPermissionManager", e);
        }

    }

}
