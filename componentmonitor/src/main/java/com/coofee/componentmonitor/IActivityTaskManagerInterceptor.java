package com.coofee.componentmonitor;

import android.os.SystemClock;
import android.shadow.ReflectUtil;
import android.shadow.ShadowLog;
import android.shadow.ShadowServiceInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public class IActivityTaskManagerInterceptor implements ShadowServiceInterceptor {

    private final Set<String> mInterceptMethodNames = ActivityManagerMethods.ACTIVITY_METHODS;

    @Override
    public Object invoke(String serviceName, Object service, Method method, Object[] args) throws Throwable {
        final long startTime = SystemClock.uptimeMillis();
        final Object result = method.invoke(service, args);
        final long endTime = SystemClock.uptimeMillis();

        ActivityManagerMethods.resolveActivityIntent(method.getName(), args);
        ShadowLog.d("BinderHook: IActivityTaskManagerInterceptor." + method.getName() + " invoke; args=" + Arrays.toString(args) + ", cost=" + (endTime - startTime));
        return ReflectUtil.wrapReturnValue(result, method.getReturnType());
    }

    @Override
    public String provideInterceptServiceName() {
        return "activity_task";
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
