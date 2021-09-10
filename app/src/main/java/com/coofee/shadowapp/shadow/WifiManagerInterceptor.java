package com.coofee.shadowapp.shadow;

import android.app.Service;
import android.shadow.ShadowLog;
import android.shadow.ShadowServiceInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WifiManagerInterceptor implements ShadowServiceInterceptor {

    private final Set<String> mInterceptMethodNames = new HashSet<>(Arrays.asList(
            "getConfiguredNetworks",
            "getScanResults",
            "startLocalOnlyHotspot"
    ));

    @Override
    public Object invoke(String serviceName, Object service, Method method, Object[] args) throws Throwable {
        ShadowLog.e("WifiManagerInterceptor intercept method=" + method.getName());
        return null;
    }

    @Override
    public String provideInterceptServiceName() {
        return Service.WIFI_SERVICE;
    }

    @Override
    public Set<String> provideInterceptMethodNames() {
        return mInterceptMethodNames;
    }

}
