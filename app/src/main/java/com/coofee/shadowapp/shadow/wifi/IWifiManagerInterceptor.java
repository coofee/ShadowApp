package com.coofee.shadowapp.shadow.wifi;

import android.app.Service;
import android.shadow.ReflectUtil;
import android.shadow.ShadowLog;
import android.shadow.ShadowServiceInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * android.net.wifi.IWifiManager
 */
public class IWifiManagerInterceptor implements ShadowServiceInterceptor {

    private final Set<String> mInterceptMethodNames = new HashSet<>(Arrays.asList(
            "getConfiguredNetworks",
            "getScanResults",
            "startLocalOnlyHotspot"
    ));

    @Override
    public Object invoke(String serviceName, Object service, Method method, Object[] args) throws Throwable {
        ShadowLog.d("WifiManagerInterceptor intercept method=" + method.getName());
        return ReflectUtil.wrapReturnValue(method.invoke(service, args), method.getReturnType());
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
