package com.coofee.shadowapp.shadow.pm;

import android.shadow.ShadowLog;
import android.shadow.ShadowServiceInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public class IPackageManagerInterceptor implements ShadowServiceInterceptor {

    @Override
    public Object invoke(String serviceName, Object service, Method method, Object[] args) throws Throwable {
        ShadowLog.d("IPackageManagerInterceptor intercept method=" + method + ", args=" + Arrays.toString(args));
        return null;
    }

    @Override
    public String provideInterceptServiceName() {
        return null;
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
