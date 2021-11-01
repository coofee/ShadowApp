package com.coofee.shadowapp.shadow.location;

import android.Manifest;
import android.app.Service;
import android.content.pm.PackageManager;
import android.shadow.ShadowLog;
import android.shadow.ShadowServiceInterceptor;
import androidx.core.content.ContextCompat;
import com.coofee.shadowapp.App;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * android.location.ILocationManager
 */
public class ILocationManagerInterceptor implements ShadowServiceInterceptor {

    private final Set<String> mInterceptMethodNames = new HashSet<>(Arrays.asList(
//            "getLastLocation",
//            "requestLocationUpdates",
//            ""
    ));

    @Override
    public Object invoke(String serviceName, Object service, Method method, Object[] args) throws Throwable {
        ShadowLog.d("ILocationManagerInterceptor intercept method=" + method.getName());

        // 禁止后台定位
        if (App.isBackground()) {
            return null;
        }

        // 前台时，有权限再执行;
        if (ContextCompat.checkSelfPermission(App.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return method.invoke(service, args);
        }

        return null;
    }

    @Override
    public String provideInterceptServiceName() {
        return Service.LOCATION_SERVICE;
    }

    @Override
    public Set<String> provideInterceptMethodNames() {
        return mInterceptMethodNames;
    }

    @Override
    public boolean interceptAllMethod() {
        return true;
    }
}
