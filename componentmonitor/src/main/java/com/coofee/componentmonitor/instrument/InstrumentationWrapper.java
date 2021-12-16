package com.coofee.componentmonitor.instrument;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.shadow.ShadowLog;

public class InstrumentationWrapper extends Instrumentation {
    private static final String TAG = "InstrumentationWrapper";

    protected final Instrumentation mBase;

    public InstrumentationWrapper(Instrumentation base) {
        this.mBase = base;
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        ShadowLog.d("newActivity; className=" + className);
        return mBase.newActivity(cl, className, intent);
    }

}
