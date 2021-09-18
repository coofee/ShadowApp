package com.coofee.shadowapp.shadow.permission;

import android.shadow.ShadowLog;
import android.shadow.ShadowServiceInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public class IPermissionManagerInterceptor implements ShadowServiceInterceptor {
    @Override
    public Object invoke(String serviceName, Object service, Method method, Object[] args) throws Throwable {
        ShadowLog.d("IPermissionManagerInterceptor intercept method=" + method + ", args=" + Arrays.toString(args));
        return method.invoke(service, args);
    }

    @Override
    public String provideInterceptServiceName() {
        return "permissionmgr";
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
