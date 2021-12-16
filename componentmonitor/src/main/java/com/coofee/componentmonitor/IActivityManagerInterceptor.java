package com.coofee.componentmonitor;

import android.app.Service;
import android.os.SystemClock;
import android.shadow.ReflectUtil;
import android.shadow.ShadowLog;
import android.shadow.ShadowServiceInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public class IActivityManagerInterceptor implements ShadowServiceInterceptor {
    private final Set<String> mInterceptMethodNames = ActivityManagerMethods.METHOD_ALL;

    @Override
    public Object invoke(String serviceName, Object service, Method method, Object[] args) throws Throwable {
        final long startTime = SystemClock.uptimeMillis();
        final Object result = method.invoke(service, args);
        final long endTime = SystemClock.uptimeMillis();

        final String methodName = method.getName();
        if (ActivityManagerMethods.ACTIVITY_METHODS.contains(methodName)) {
            ActivityManagerMethods.resolveActivityIntent(methodName, args);

        } else if (ActivityManagerMethods.SERVICE_METHODS.contains(methodName)) {
            ActivityManagerMethods.resolveServiceIntent(methodName, args);

        } else if (ActivityManagerMethods.BROADCAST_RECEIVER_METHODS.contains(methodName)) {
            ActivityManagerMethods.resolveBroadcastReceiverIntent(methodName, args, result);

        }

        if (ActivityManagerMethods.CONTENT_PROVIDER_METHODS.contains(methodName)) {
            ActivityManagerMethods.resolveContentProviderIntent(methodName, args, result);

        }

//        Throwable stacktrace = null;
//        if ("getContentProvider".equals(methodName)) {
//            stacktrace = new Throwable();
//        }
//
//        ShadowLog.d("BinderHook: IActivityManagerInterceptor; invoke method=" + method.getName() + ", args=" + Arrays.toString(args) + ", cost " + (endTime - startTime), stacktrace);
        ShadowLog.d("BinderHook: IActivityManagerInterceptor; invoke method=" + method.getName() + ", args=" + Arrays.toString(args) + ", cost " + (endTime - startTime));
        return ReflectUtil.wrapReturnValue(result, method.getReturnType());
    }


    @Override
    public String provideInterceptServiceName() {
        return Service.ACTIVITY_SERVICE;
    }

    @Override
    public Set<String> provideInterceptMethodNames() {
        return mInterceptMethodNames;
    }

    @Override
    public boolean interceptAllMethod() {
        return false;
    }

}
