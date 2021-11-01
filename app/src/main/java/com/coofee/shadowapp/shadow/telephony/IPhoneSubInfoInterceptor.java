package com.coofee.shadowapp.shadow.telephony;

import android.shadow.ShadowLog;
import android.shadow.ShadowServiceInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * com.android.internal.telephony.IPhoneSubInfo
 */
public class IPhoneSubInfoInterceptor implements ShadowServiceInterceptor {
    private final Set<String> mInterceptMethodNames = new HashSet<>(Arrays.asList(
            "getDeviceId",
            "getDeviceIdWithFeature",
            "getDeviceIdForPhone",

            "getImeiForSubscriber",

            "getSubscriberIdWithFeature",
            "getSubscriberIdForSubscriber",

            "getIccSerialNumberWithFeature",
            "getIccSerialNumberForSubscriber",

            "getLine1NumberForSubscriber",
            "getGroupIdLevel1ForSubscriber"
    ));

    @Override
    public Object invoke(String serviceName, Object service, Method method, Object[] args) throws Throwable {
        ShadowLog.d("IPhoneSubInfoInterceptor intercept method=" + method.getName());
        return null;
    }

    @Override
    public String provideInterceptServiceName() {
        return "iphonesubinfo";
    }

    @Override
    public Set<String> provideInterceptMethodNames() {
        return mInterceptMethodNames;
    }
}
