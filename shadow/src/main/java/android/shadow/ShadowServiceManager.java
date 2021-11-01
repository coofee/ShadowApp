package android.shadow;

import android.app.Service;
import android.os.Build;
import android.os.IBinder;
import android.os.IInterface;
import android.os.ServiceManagerBridge;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;

public class ShadowServiceManager {
    public static final String TAG = "ShadowServiceManager";

    static ShadowConfig sShadowConfig;

    private static final Map<String, ShadowServiceEntry> sServiceEntryMap = new LinkedHashMap<>();

    public static void init(ShadowConfig shadowConfig) {
        sShadowConfig = shadowConfig;
        intercept(shadowConfig);
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

    public static ShadowService getShadowService(String service) {
        ShadowServiceEntry serviceEntry = sServiceEntryMap.get(service);
        if (serviceEntry != null && serviceEntry.state == ShadowServiceEntry.State.SUCCESS) {
            return new ShadowService(serviceEntry);
        }
        return ShadowService.EMPTY;
    }

    public static Object getService(String service) {
        ShadowServiceEntry serviceEntry = sServiceEntryMap.get(service);
        if (serviceEntry != null && serviceEntry.state == ShadowServiceEntry.State.SUCCESS) {
            return serviceEntry.proxyInterface;
        }
        return null;
    }

    private static void intercept(ShadowConfig shadowConfig) {
        ShadowLog.d("start intercept service...");
        final String[] serviceNames = ServiceManagerBridge.listServices();
        final int serviceNameCount = serviceNames == null ? 0 : serviceNames.length;
        if (serviceNameCount < 1) {
            ShadowLog.d("list service is empty.");
            ShadowLog.d("end intercept service.");
            return;
        }

        ShadowLog.d("service count=" + serviceNameCount);
        final Map<String, ShadowServiceEntry> nameAndServiceMap = new LinkedHashMap<>(serviceNameCount);
        for (int i = 0; i < serviceNameCount; i++) {
            final String serviceName = serviceNames[i];
            final ShadowServiceEntry.Builder serviceEntryBuilder = new ShadowServiceEntry.Builder(serviceName);

            final IBinder originService = ServiceManagerBridge.getService(serviceName);
            if (originService == null) {
                ShadowServiceEntry shadowServiceEntry = serviceEntryBuilder.state(ShadowServiceEntry.State.CANNOT_GET_ORIGIN_SERVICE).build();
                nameAndServiceMap.put(shadowServiceEntry.name, shadowServiceEntry);
                ShadowLog.d("cannot get service; shadowServiceEntry=" + shadowServiceEntry);
                continue;
            }

            serviceEntryBuilder.originService(originService);
            final IBinder originServiceWrapper = (IBinder) Proxy.newProxyInstance(originService.getClass().getClassLoader(), new Class[]{IBinder.class}, new ShadowIBinderInvocationHandler(serviceName, originService));
            serviceEntryBuilder.originServiceWrapper(originServiceWrapper);

            final String serviceInterfaceName;
            final String serviceStubName;
            final String serviceStubProxyName;
            try {
                serviceInterfaceName = originServiceWrapper.getInterfaceDescriptor();
                if (Service.ACTIVITY_SERVICE.equals(serviceName) && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    serviceStubName = "android.app.ActivityManagerNative";
                    serviceStubProxyName = "android.app.ActivityManagerProxy";
                } else {
                    serviceStubName = serviceInterfaceName + "$Stub";
                    serviceStubProxyName = serviceStubName + "$Proxy";
                }

                serviceEntryBuilder.interfaceDescriptor(serviceInterfaceName)
                        .interfaceClassName(serviceInterfaceName)
                        .stubClassName(serviceStubName)
                        .stubProxyClassName(serviceStubProxyName);
            } catch (Throwable e) {
                ShadowServiceEntry shadowServiceEntry = serviceEntryBuilder.state(ShadowServiceEntry.State.CANNOT_GET_INTERFACE_DESCRIPTOR).build();
                nameAndServiceMap.put(shadowServiceEntry.name, shadowServiceEntry);
                ShadowLog.d("cannot get service interface descriptor; shadowServiceEntry=" + shadowServiceEntry, e);
                continue;
            }

            final Class<?> serviceInterfaceClass;
            try {
                serviceInterfaceClass = Class.forName(serviceInterfaceName);
                serviceEntryBuilder.interfaceClass(serviceInterfaceClass);
            } catch (Throwable e) {
                ShadowServiceEntry shadowServiceEntry = serviceEntryBuilder.state(ShadowServiceEntry.State.CANNOT_LOAD_INTERFACE_CLASS).build();
                nameAndServiceMap.put(shadowServiceEntry.name, shadowServiceEntry);
                ShadowLog.d("cannot load service interface class; shadowServiceEntry=" + shadowServiceEntry, e);
                continue;
            }

            final Class<?> serviceStubClass;
            final Class<?> serviceStubProxyClass;
            try {
                serviceStubClass = Class.forName(serviceStubName);
                serviceEntryBuilder.stubClass(serviceStubClass);

                serviceStubProxyClass = Class.forName(serviceStubProxyName);
                serviceEntryBuilder.stubProxyClass(serviceStubProxyClass);
            } catch (Throwable e) {
                ShadowServiceEntry shadowServiceEntry = serviceEntryBuilder.state(ShadowServiceEntry.State.CANNOT_LOAD_STUB_CLASS).build();
                nameAndServiceMap.put(shadowServiceEntry.name, shadowServiceEntry);
                ShadowLog.d("cannot load service stub class; shadowServiceEntry=" + shadowServiceEntry, e);
                continue;
            }

//                android.app.SystemServiceRegistry
//
//                IConnectivityManager service = IResultReceiver.Stub.asInterface(b);
//                public static IResultReceiver asInterface(IBinder obj) {
//                    if (obj == null) {
//                        return null;
//                    } else {
//                        IInterface iin = obj.queryLocalInterface("android.support.v4.os.IResultReceiver");
//                        return (IResultReceiver)(iin != null && iin instanceof IResultReceiver ? (IResultReceiver)iin : new IResultReceiver.Stub.Proxy(obj));
//                    }
//                }

            Object originInterface = null;
            try {
                try {
                    Method method_asInterface = serviceStubClass.getDeclaredMethod("asInterface", IBinder.class);
                    method_asInterface.setAccessible(true);
                    originInterface = method_asInterface.invoke(null, originServiceWrapper);
                } catch (NoSuchMethodException e) {
                    IInterface iInterface = originServiceWrapper.queryLocalInterface(serviceInterfaceName);
                    if (iInterface != null && serviceInterfaceClass.isInstance(iInterface)) {
                        originInterface = iInterface;
                    } else {
                        ReflectUtil.print(serviceStubProxyClass);
                        Constructor<?> constructor = serviceStubProxyClass.getDeclaredConstructor(IBinder.class);
                        constructor.setAccessible(true);
                        originInterface = constructor.newInstance(originServiceWrapper);
                    }
                }

                if (originInterface == null) {
                    throw new NullPointerException("originInterface is null");
                }
                serviceEntryBuilder.originInterface(originInterface);
            } catch (Throwable e) {
                ShadowServiceEntry shadowServiceEntry = serviceEntryBuilder.state(ShadowServiceEntry.State.CANNOT_GET_ORIGIN_INTERFACE).build();
                nameAndServiceMap.put(shadowServiceEntry.name, shadowServiceEntry);
                ShadowLog.d("cannot get origin interface; shadowServiceEntry=" + shadowServiceEntry, e);
                continue;
            }

            try {
                final ClassLoader classLoader = originService.getClass().getClassLoader();
                final ShadowServiceInvocationHandler handler = new ShadowServiceInvocationHandler(serviceName, originInterface);
                final Object proxyInterface = Proxy.newProxyInstance(classLoader, new Class[]{serviceInterfaceClass}, handler);
                final IBinder proxyService = (IBinder) Proxy.newProxyInstance(classLoader, new Class[]{IBinder.class}, new ShadowIBinderInvocationHandler(serviceName, originServiceWrapper, proxyInterface));
                final ShadowServiceEntry serviceEntry = serviceEntryBuilder.proxyService(proxyService)
                        .proxyInterface(proxyInterface)
                        .handler(handler)
                        .state(ShadowServiceEntry.State.SUCCESS)
                        .build();
                nameAndServiceMap.put(serviceEntry.name, serviceEntry);
            } catch (Throwable e) {
                ShadowServiceEntry shadowServiceEntry = serviceEntryBuilder.state(ShadowServiceEntry.State.FAIL_CREATE_PROXY).build();
                nameAndServiceMap.put(shadowServiceEntry.name, shadowServiceEntry);
                ShadowLog.d("fail create proxy; shadowServiceEntry=" + shadowServiceEntry, e);
            }
        }

        final Map<String, IBinder> serviceCache = ServiceManagerBridge.getServiceCache();
        if (serviceCache != null && !nameAndServiceMap.isEmpty()) {
            for (Map.Entry<String, ShadowServiceEntry> entry : nameAndServiceMap.entrySet()) {
                ShadowServiceEntry serviceEntry = entry.getValue();
                if (serviceEntry.state != ShadowServiceEntry.State.SUCCESS) {
                    ShadowLog.e("cannot intercept service=" + serviceEntry.name + ", serviceEntry=" + serviceEntry);
                    continue;
                }

                try {
                    serviceEntry.handler.add(shadowConfig.interceptorMap.get(serviceEntry.name));
                    serviceCache.put(serviceEntry.name, serviceEntry.proxyService);
                    ShadowLog.d("success intercept service=" + serviceEntry.name + ", serviceEntry=" + serviceEntry);
                } catch (Throwable e) {
                    ShadowLog.e("fail intercept service=" + serviceEntry.name, e);
                }
            }
        }

        Class<?> class_activityThread = getActivityThreadClass();
        replacePackageManager(class_activityThread, nameAndServiceMap.get("package"));
        replacePermissionManager(class_activityThread, nameAndServiceMap.get("permissionmgr"));
        replaceActivityManager(nameAndServiceMap.get(Service.ACTIVITY_SERVICE), nameAndServiceMap.get("activity_task"));
        sServiceEntryMap.putAll(nameAndServiceMap);

        ShadowLog.d("end intercept service.");
    }

    private static Class<?> getActivityThreadClass() {
        try {
            return Class.forName("android.app.ActivityThread");
        } catch (Throwable e) {
            ShadowLog.e("cannot found class ActivityThread", e);
        }

        return null;
    }

    private static void replacePackageManager(Class<?> activityThread, ShadowServiceEntry shadowService) {
        if (shadowService == null || shadowService.state != ShadowServiceEntry.State.SUCCESS) {
            ShadowLog.e("fail replacePackageManager for shadow service is null.");
            return;
        }

        try {
            Field field_sPackageManager = activityThread.getDeclaredField("sPackageManager");
            field_sPackageManager.setAccessible(true);
            Object originPackageManager = field_sPackageManager.get(null);
            if (originPackageManager != null) {
                field_sPackageManager.set(null, shadowService.proxyInterface);
                ShadowLog.e("replace originPackageManager=" + originPackageManager + " by packageManager=" + shadowService);
            }
        } catch (Throwable e) {
            ShadowLog.e("fail replacePackageManager", e);
        }
    }

    private static void replacePermissionManager(Class<?> activityThread, ShadowServiceEntry shadowService) {
        if (shadowService == null || shadowService.state != ShadowServiceEntry.State.SUCCESS) {
            ShadowLog.e("fail replacePermissionManager for shadow service is null.");
            return;
        }

        try {
            Field field_sPermissionManager = activityThread.getDeclaredField("sPermissionManager");
            field_sPermissionManager.setAccessible(true);
            Object originPermissionManager = field_sPermissionManager.get(null);
            if (originPermissionManager != null) {
                field_sPermissionManager.set(null, shadowService.proxyInterface);
                ShadowLog.e("replace originPermissionManager=" + originPermissionManager + " by permissionmgr=" + shadowService);
            }
        } catch (Throwable e) {
            ShadowLog.e("fail replacePermissionManager", e);
        }
    }

    private static void replaceActivityManager(ShadowServiceEntry shadowActivityManager, ShadowServiceEntry shadowActivityTaskManager) {
        try {
            Class<?> class_Singleton = Class.forName("android.util.Singleton");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // android 10.0+
//        "android.app.ActivityTaskManager"
//        private static final Singleton<IActivityTaskManager> IActivityTaskManagerSingleton =
//                new Singleton<IActivityTaskManager>() {
//                    @Override
//                    protected IActivityTaskManager create() {
//                        final IBinder b = ServiceManager.getService(Context.ACTIVITY_TASK_SERVICE);
//                        return IActivityTaskManager.Stub.asInterface(b);
//                    }

                Field field_IActivityTaskManagerSingleton = ReflectUtil.getField("android.app.ActivityTaskManager", "IActivityTaskManagerSingleton");
                Object IActivityTaskManagerSingleton = ReflectUtil.getFieldValue(null, field_IActivityTaskManagerSingleton);
                Field field_mInstance = ReflectUtil.getField(class_Singleton, "mInstance");
                Object mInstance = ReflectUtil.getFieldValue(IActivityTaskManagerSingleton, field_mInstance);
                ShadowLog.e("replaceActivityManager; >=29(Q) field_IActivityTaskManagerSingleton, read mInstance=" + mInstance);
                ReflectUtil.setFieldValue(IActivityTaskManagerSingleton, field_mInstance, shadowActivityTaskManager.proxyInterface);
                ShadowLog.e("replaceActivityManager; >=29(Q) field_IActivityTaskManagerSingleton, write mInstance=" + shadowActivityTaskManager.proxyInterface);

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // android 8.0+

//        "android.app.ActivityManager"
//        private static final Singleton<IActivityManager> IActivityManagerSingleton =
//                new Singleton<IActivityManager>() {
//                    @Override
//                    protected IActivityManager create() {
//                        final IBinder b = ServiceManager.getService(Context.ACTIVITY_SERVICE);
//                        final IActivityManager am = IActivityManager.Stub.asInterface(b);
//                        return am;
//                    }
//                };

                Field field_IActivityManagerSingleton = ReflectUtil.getField("android.app.ActivityManager", "IActivityManagerSingleton");
                Object IActivityManagerSingleton = ReflectUtil.getFieldValue(null, field_IActivityManagerSingleton);
                Field field_mInstance = ReflectUtil.getField(class_Singleton, "mInstance");
                Object mInstance = ReflectUtil.getFieldValue(IActivityManagerSingleton, field_mInstance);
                ShadowLog.e("replaceActivityManager; >=26(O) field_IActivityManagerSingleton, read mInstance=" + mInstance);
                ReflectUtil.setFieldValue(IActivityManagerSingleton, field_mInstance, shadowActivityManager.proxyInterface);
                ShadowLog.e("replaceActivityManager; >=26(O) field_IActivityManagerSingleton, write mInstance=" + shadowActivityManager.proxyInterface);

            } else {

//          "android.app.ActivityManagerNative"
//        private static final Singleton<IActivityManager> gDefault = new Singleton<IActivityManager>() {
//            protected IActivityManager create() {
//                IBinder b = ServiceManager.getService("activity");
//                if (false) {
//                    Log.v("ActivityManager", "default service binder = " + b);
//                }
//                IActivityManager am = asInterface(b);
//                if (false) {
//                    Log.v("ActivityManager", "default service = " + am);
//                }
//                return am;
//            }
//        };

//                private static IActivityManager gDefault;
//                static public IActivityManager getDefault(){
//
//                }

                Field field_gDefault = ReflectUtil.getField("android.app.ActivityManagerNative", "gDefault");
                Object gDefault = ReflectUtil.getFieldValue(null, field_gDefault);
                if ("android.app.IActivityManager".equals(field_gDefault.getType().getName())) {
                    ShadowLog.e("replaceActivityManager; <26(O) field_gDefault, read gDefault=" + gDefault);
                    ReflectUtil.setFieldValue(null, field_gDefault, shadowActivityManager.proxyInterface);
                    ShadowLog.e("replaceActivityManager; <26(O) field_gDefault, write gDefault=" + shadowActivityManager.proxyInterface);
                } else {
                    Field field_mInstance = ReflectUtil.getField(class_Singleton, "mInstance");
                    Object mInstance = ReflectUtil.getFieldValue(gDefault, field_mInstance);
                    ShadowLog.e("replaceActivityManager; <26(O) field_gDefault, read mInstance=" + mInstance);
                    ReflectUtil.setFieldValue(gDefault, field_mInstance, shadowActivityManager.proxyInterface);
                    ShadowLog.e("replaceActivityManager; <26(O) field_gDefault, write mInstance=" + shadowActivityManager.proxyInterface);
                }
            }
        } catch (Throwable e) {
            ShadowLog.e("fail replaceActivityManager; shadowActivityManager=" + shadowActivityManager + ", shadowActivityTaskManager=" + shadowActivityTaskManager);
        }
    }
}
