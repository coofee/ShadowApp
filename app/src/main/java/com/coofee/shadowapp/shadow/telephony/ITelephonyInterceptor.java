package com.coofee.shadowapp.shadow.telephony;

import android.app.Service;
import android.shadow.ReflectUtil;
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

//        final String name = method.getName();
//        if ("getCellLocation".equals(name) || "getAllCellInfo".equals(name)) {
//            // 禁止后台定位
//            if (App.isBackground()) {
//                return null;
//            }
//
//            // 前台时，有权限再执行;
//            if (ContextCompat.checkSelfPermission(App.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                return method.invoke(service, args);
//            }
//        }
//
//        if (ContextCompat.checkSelfPermission(App.getContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
//            return method.invoke(service, args);
//        }

        return ReflectUtil.wrapReturnValue(method.invoke(service, args), method.getReturnType());
    }

    @Override
    public String provideInterceptServiceName() {
        return Service.TELEPHONY_SERVICE;
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
