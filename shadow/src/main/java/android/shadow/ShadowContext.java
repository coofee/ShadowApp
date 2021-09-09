package android.shadow;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

public class ShadowContext extends ContextWrapper {

    public static ShadowContext shadow(Application application) {
        return new ShadowContext(application);
    }

    public static ShadowContext shadowContext(Application application, Context context) {
        return new ShadowContext(application, context);
    }

    public final ShadowApplication shadowApplication;

    private final Object lock = new Object();
    private ShadowConfig config = null;

    public ShadowContext(Application application) {
        super(ShadowApplication.shadow(application));
        this.shadowApplication = (ShadowApplication) getBaseContext();
    }

    public ShadowContext(Application application, Context context) {
        super(context);
        this.shadowApplication = ShadowApplication.shadow(application);
    }

    @Override
    public Context getApplicationContext() {
        return this.shadowApplication;
    }

    @Override
    public Object getSystemService(String name) {
        ShadowConfig localConfig = config;
        if (localConfig == null) {
            synchronized (lock) {
                if (localConfig == null) {
                    localConfig = onCreateShadowConfig();
                    config = localConfig;
                }
            }
        }

        Object service = ShadowServiceManager.getService(name, localConfig);
        if (service != null) {
            return service;
        }

        return super.getSystemService(name);
    }

    public ShadowConfig onCreateShadowConfig() {
        return ShadowServiceManager.config().newBuilder().interceptAll(true).build();
    }
}
