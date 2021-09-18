package com.coofee.shadowapp.shadow.activity;

import android.app.Service;
import android.shadow.ShadowLog;
import android.shadow.ShadowServiceInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public class IActivityManagerInterceptor implements ShadowServiceInterceptor {

    @Override
    public Object invoke(String serviceName, Object service, Method method, Object[] args) throws Throwable {
        return method.invoke(service, args);
    }

    @Override
    public String provideInterceptServiceName() {
        return Service.ACTIVITY_SERVICE;
    }

    @Override
    public Set<String> provideInterceptMethodNames() {
        return null;
    }

    @Override
    public boolean interceptAllMethod() {
        return true;
    }
}
