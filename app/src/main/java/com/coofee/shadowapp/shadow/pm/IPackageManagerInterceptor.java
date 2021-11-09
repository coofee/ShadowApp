package com.coofee.shadowapp.shadow.pm;

import android.shadow.ReflectUtil;
import android.shadow.ShadowLog;
import android.shadow.ShadowServiceInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public class IPackageManagerInterceptor implements ShadowServiceInterceptor {

    @Override
    public Object invoke(String serviceName, Object service, Method method, Object[] args) throws Throwable {
        ShadowLog.d("IPackageManagerInterceptor intercept method=" + method + ", args=" + Arrays.toString(args));
        return ReflectUtil.wrapReturnValue(method.invoke(service, args), method.getReturnType());
    }

    @Override
    public String provideInterceptServiceName() {
        return "package";
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
