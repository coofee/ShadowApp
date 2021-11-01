package android.os;

import android.shadow.ShadowLog;

import java.lang.reflect.Field;
import java.util.Map;

public class ServiceManagerBridge {

    private static Class<?> class_ServiceManager;
    private static Field field_sCache;

    static {
        try {
            class_ServiceManager = Class.forName("android.os.ServiceManager");
            field_sCache = class_ServiceManager.getDeclaredField("sCache");
            field_sCache.setAccessible(true);
        } catch (Throwable e) {
            ShadowLog.e("ServiceManagerWrapper fail reflect", e);
        }
    }

    public static Map<String, IBinder> getServiceCache() {
        try {
            return (Map<String, IBinder>) field_sCache.get(null);
        } catch (Throwable e) {
            ShadowLog.e("ServiceManagerWrapper fail get getServiceCache", e);
        }

        return null;
    }

    public static String[] listServices() {
        try {
            String[] services = ServiceManager.listServices();
            if (services == null) {
                return new String[0];
            }

            return services;
        } catch (Throwable e) {
            ShadowLog.e("fail list service", e);
        }

        return new String[0];
    }

    public static IBinder getService(String name) {
        try {
            return ServiceManager.getService(name);
        } catch (Throwable e) {
            ShadowLog.e("fail get service by " + name, e);
        }

        return null;
    }

}
