package com.coofee.componentmonitor.instrument;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.WeakHashMap;

public class MonitorInstrumentation extends InstrumentationWrapper {

    private final InstrumentationHacker hacker;

    public MonitorInstrumentation(InstrumentationHacker hacker) {
        super(hacker.getInstrumentation());
        this.hacker = hacker;
    }

    public void hook() {
        hacker.hook(this);
    }

    public void reset() {
        hacker.reset();
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        fireOnNewActivity(className, intent);
        return super.newActivity(cl, className, intent);
    }

    public interface OnNewActivityListener {
        void onNewActivity(String className, Intent intent);
    }

    private final Map<String, LinkedHashSet<OnNewActivityListener>> mActivityListenerMap = new WeakHashMap<>();

    public MonitorInstrumentation bind(@NonNull Class<?> activityClass, @NonNull OnNewActivityListener listener) {
        bind(keyOf(activityClass), listener);
        return this;
    }

    public MonitorInstrumentation bind(@NonNull String activityFullClassName, @NonNull OnNewActivityListener listener) {
        LinkedHashSet<OnNewActivityListener> listeners = mActivityListenerMap.get(activityFullClassName);
        if (listeners == null) {
            listeners = new LinkedHashSet<>();
            mActivityListenerMap.put(activityFullClassName, listeners);
        }
        listeners.add(listener);
        return this;
    }

    public MonitorInstrumentation unbind(@NonNull Class<?> activityClass, @NonNull OnNewActivityListener listener) {
        unbind(keyOf(activityClass), listener);
        return this;
    }

    public MonitorInstrumentation unbind(@NonNull String activityFullClassName, @NonNull OnNewActivityListener listener) {
        LinkedHashSet<OnNewActivityListener> listeners = mActivityListenerMap.get(activityFullClassName);
        if (listeners != null) {
            listeners.remove(listener);
        }
        return this;
    }

    public MonitorInstrumentation unbindAll(@NonNull Class<?> activityClass) {
        unbindAll(keyOf(activityClass));
        return this;
    }

    public MonitorInstrumentation unbindAll(@NonNull String activityFullClassName) {
        LinkedHashSet<OnNewActivityListener> listeners = mActivityListenerMap.get(activityFullClassName);
        if (listeners != null) {
            listeners.clear();
        }
        return this;
    }

    @NonNull
    public String keyOf(@NonNull Class<?> activityClass) {
        return activityClass.getName();
    }

    private void fireOnNewActivity(String className, Intent intent) {
        LinkedHashSet<OnNewActivityListener> listeners = mActivityListenerMap.get(className);
        if (listeners == null) {
            return;
        }

        for (OnNewActivityListener listener : listeners) {
            listener.onNewActivity(className, intent);
        }
    }
}
