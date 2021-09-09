package android.shadow;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;

public class ShadowApplication extends Application {

    public static ShadowApplication shadow(Application application) {
        if (application instanceof ShadowApplication) {
            return (ShadowApplication) application;
        }

        return new ShadowApplication(application);
    }

    public final Application application;

    public final ShadowContext baseContext;

    public Context originBaseContext;

    public ShadowApplication() {
        this.application = this;
        this.baseContext = new ShadowContext(this);
    }

    private ShadowApplication(Application application) {
        this.application = application;
        this.baseContext = new ShadowContext(this);
        super.attachBaseContext(this.baseContext);
        this.originBaseContext = application;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        originBaseContext = base;
    }

    @Override
    public Context getApplicationContext() {
        return application;
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
            return super.getSystemService(name);
        } else {
            return this.application.getSystemService(name);
        }
    }

    @Override
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        if (this == application) {
            super.registerComponentCallbacks(callback);
        } else {
            this.application.registerComponentCallbacks(callback);
        }
    }

    @Override
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        if (this == application) {
            super.unregisterComponentCallbacks(callback);
        } else {
            this.application.unregisterComponentCallbacks(callback);
        }
    }

    @Override
    public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        if (this == application) {
            super.registerActivityLifecycleCallbacks(callback);
        } else {
            this.application.registerActivityLifecycleCallbacks(callback);
        }
    }

    @Override
    public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        if (this == application) {
            super.unregisterActivityLifecycleCallbacks(callback);
        } else {
            this.application.unregisterActivityLifecycleCallbacks(callback);
        }
    }

    @Override
    public void registerOnProvideAssistDataListener(OnProvideAssistDataListener callback) {
        if (this == application) {
            super.registerOnProvideAssistDataListener(callback);
        } else {
            this.application.registerOnProvideAssistDataListener(callback);
        }
    }

    @Override
    public void unregisterOnProvideAssistDataListener(OnProvideAssistDataListener callback) {
        if (this == application) {
            super.unregisterOnProvideAssistDataListener(callback);
        } else {
            this.application.unregisterOnProvideAssistDataListener(callback);
        }
    }
}