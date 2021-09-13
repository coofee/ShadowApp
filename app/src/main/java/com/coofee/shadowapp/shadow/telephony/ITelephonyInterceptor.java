package com.coofee.shadowapp.shadow.telephony;

import android.app.Service;
import android.shadow.ShadowLog;
import android.shadow.ShadowServiceInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * com.android.internal.telephony.ITelephony
 */
public class ITelephonyInterceptor implements ShadowServiceInterceptor {

    private final Set<String> mInterceptMethodNames = new HashSet<>(Arrays.asList(
            "getDeviceId",
            "getDeviceIdWithFeature",

            "getImei",
            "getImeiForSlot",

            "getMeid",
            "getMeidForSlot",

            "getCellLocation",

            "getAllCellInfo",

            "getLine1NumberForDisplay",

            "getEmergencyNumberList",

            "requestCellInfoUpdate"
    ));

    @Override
    public Object invoke(String serviceName, Object service, Method method, Object[] args) throws Throwable {
        ShadowLog.d("ITelephonyInterceptor intercept method=" + method.getName());
        return null;
    }

    @Override
    public String provideInterceptServiceName() {
        return Service.TELEPHONY_SERVICE;
    }

    @Override
    public Set<String> provideInterceptMethodNames() {
        return mInterceptMethodNames;
    }
}
