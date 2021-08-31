package android.shadow;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.MutableContextWrapper;

public class ShadowApplication extends Application {

    public static ShadowApplication shadow(Application application) {
        return new ShadowApplication(application);
    }

    public final Application application;

    public final ShadowContext baseContext;

    public Context originBaseContext;

    public ShadowApplication() {
        this.application = this;
        this.baseContext = new ShadowContext(this);
    }

    public ShadowApplication(Application application) {
        this.application = application;
        this.baseContext = new ShadowContext(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        originBaseContext = base;
    }

    @Override
    public Context getApplicationContext() {
        return this;
    }

    @Override
    public Context getBaseContext() {
        return baseContext;
    }

    @Override
    public Object getSystemService(String name) {
        Object service = ShadowServiceManager.getService(name);
        if (service != null) {
            return service;
        }

        if (this == application) {
            return ShadowServiceManager.sShadowConfig.baseContext.getSystemService(name);
        }

        return application.getSystemService(name);
    }

    @Override
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        this.application.registerComponentCallbacks(callback);
    }

    @Override
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        this.application.unregisterComponentCallbacks(callback);
    }

    @Override
    public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        this.application.registerActivityLifecycleCallbacks(callback);
    }

    @Override
    public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        this.application.unregisterActivityLifecycleCallbacks(callback);
    }

    @Override
    public void registerOnProvideAssistDataListener(OnProvideAssistDataListener callback) {
        this.application.registerOnProvideAssistDataListener(callback);
    }

    @Override
    public void unregisterOnProvideAssistDataListener(OnProvideAssistDataListener callback) {
        this.application.unregisterOnProvideAssistDataListener(callback);
    }
}