package com.coofee.shadowapp.shadow.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.shadow.ReflectUtil;
import android.shadow.ShadowLog;
import android.shadow.ShadowServiceInterceptor;
import com.coofee.shadowapp.App;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.coofee.shadowapp.shadow.activity.IntentUtil.resolveIntent;

public class IActivityTaskManagerInterceptor implements ShadowServiceInterceptor {

    private final Set<String> mInterceptMethodNames = new HashSet<>(Arrays.asList(
            "startActivity",
            "startActivities",
            "startNextMatchingActivity"
    ));

    @Override
    public Object invoke(String serviceName, Object service, Method method, Object[] args) throws Throwable {
        if (args != null) {
            for (Object arg : args) {
                if (arg instanceof Intent) {
                    resolveIntent((Intent) arg);
                }

                if (arg instanceof Intent[]) {
                    for (Intent intent : (Intent[]) arg) {
                        resolveIntent(intent);
                    }
                }
            }
        }

        return ReflectUtil.wrapReturnValue(method.invoke(service, args), method.getReturnType());
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
