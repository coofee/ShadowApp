package com.coofee.shadowapp.shadow;

import android.app.Service;
import android.shadow.ShadowLog;
import android.shadow.ShadowServiceInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LocationManagerInterceptor implements ShadowServiceInterceptor {

    private final Set<String> mInterceptMethodNames = new HashSet<>(Arrays.asList(
            ""
    ));

    @Override
    public Object invoke(String serviceName, Object service, Method method, Object[] args) throws Throwable {
        ShadowLog.e("TelephonyManagerInterceptor intercept method=" + method.getName());
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
