package android.shadow;

public class ShadowServiceManager {
    public static final String TAG = "ShadowServiceManager";

    static ShadowConfig sShadowConfig;

    public static void init(ShadowConfig shadowConfig) {
        sShadowConfig = shadowConfig;
    }

    public static boolean debug() {
        ShadowConfig config = ShadowServiceManager.sShadowConfig;
        if (config == null) {
            return false;
        }

        return config.debug;
    }

    public static ShadowConfig config() {
        return sShadowConfig;
    }

    public static Object getService(String serviceName) {
        return getService(serviceName, ShadowServiceManager.sShadowConfig);
    }

    public static Object getService(String serviceName, ShadowConfig config) {
        if (config == null) {
            config = ShadowServiceManager.sShadowConfig;
        }

        if (config == null) {
            return null;
        }

        final ShadowConfig.ServiceEntry serviceEntry = config.serviceEntryMap.get(serviceName);
        if (serviceEntry == null || serviceEntry.service == null || serviceEntry.provider == null) {
            return null;
        }

        if (config.interceptAll) {
            ShadowLog.e("intercept service=" + serviceName, new Throwable());
            return serviceEntry.service;
        }

        if (StackTraceUtil.invokeBy(config.prefixSet)) {
            ShadowLog.e("intercept service=" + serviceName, new Throwable());
            return serviceEntry.service;
        }

        return null;
    }
}
