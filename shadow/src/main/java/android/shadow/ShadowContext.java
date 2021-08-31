package android.shadow;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

public class ShadowContext extends ContextWrapper {

    public static ShadowContext shadow(Application application) {
        return new ShadowContext(application);
    }

    public final ShadowApplication shadowApplication;

    private final Object lock = new Object();
    private ShadowConfig config = null;

    public ShadowContext(Application application) {
        super(new ShadowApplication(application));
        this.shadowApplication = (ShadowApplication) getBaseContext();
    }

    ShadowContext(ShadowApplication shadowApplication) {
        super(shadowApplication);
        this.shadowApplication = shadowApplication;
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

        return shadowApplication.application.getSystemService(name);
    }

    public ShadowConfig onCreateShadowConfig() {
        return ShadowServiceManager.config().newBuilder().interceptAll(true).build();
    }
}
