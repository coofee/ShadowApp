package com.coofee.componentmonitor.instrument;

import android.app.Instrumentation;
import android.shadow.ShadowLog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class InstrumentationHacker {

    private Object activityThread;

    private Field field_mInstrumentation;

    private Instrumentation instrumentation;

    public InstrumentationHacker() {
        try {
            Class<?> class_activityThread = Class.forName("android.app.ActivityThread");

            Method method_currentActivityThread = class_activityThread.getDeclaredMethod("currentActivityThread");
            method_currentActivityThread.setAccessible(true);
            this.activityThread = method_currentActivityThread.invoke(null, null);

            this.field_mInstrumentation = class_activityThread.getDeclaredField("mInstrumentation");
            this.field_mInstrumentation.setAccessible(true);
            this.instrumentation = (Instrumentation) this.field_mInstrumentation.get(activityThread);
        } catch (Throwable e) {
            ShadowLog.e("fail get instrument from ActivityThread", e);
        }
    }

    public Instrumentation getInstrumentation() {
        return this.instrumentation;
    }

    public void hook(Instrumentation instrumentation) {
        if (this.field_mInstrumentation != null && this.activityThread != null) {
            try {
                this.field_mInstrumentation.set(this.activityThread, instrumentation);
            } catch (IllegalAccessException e) {
                ShadowLog.e("fail replace instrumentation for ActivityThread", e);
            }
        }
    }

    public void reset() {
        hook(this.instrumentation);
    }
}
