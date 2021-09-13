package com.coofee.shadowapp.shadow.location;

import android.app.Service;
import android.shadow.ShadowLog;
import android.shadow.ShadowServiceInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * android.location.ILocationManager
 */
public class ILocationManagerInterceptor implements ShadowServiceInterceptor {

    private final Set<String> mInterceptMethodNames = new HashSet<>(Arrays.asList(
            "getLastLocation",
            "requestLocationUpdates",
            ""
    ));

    @Override
    public Object invoke(String serviceName, Object service, Method method, Object[] args) throws Throwable {
        ShadowLog.d("TelephonyManagerInterceptor intercept method=" + method.getName());
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
