package android.shadow;

import android.os.IBinder;
import android.os.ServiceManagerBridge;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;

public class ShadowServiceManager {
    public static final String TAG = "ShadowServiceManager";

    static ShadowConfig sShadowConfig;

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

    private static void intercept(ShadowConfig shadowConfig) {
        ShadowLog.e("start intercept service...");
        final String[] serviceNames = ServiceManagerBridge.listServices();
        final int serviceNameCount = serviceNames == null ? 0 : serviceNames.length;
        if (serviceNameCount < 1) {
            ShadowLog.e("list service is empty.");
            ShadowLog.e("end intercept service.");
            return;
        }

        ShadowLog.e("service count=" + serviceNameCount);
        final Map<String, ShadowServiceEntry> nameAndServiceMap = new LinkedHashMap<>(serviceNameCount);
        for (int i = 0; i < serviceNameCount; i++) {
            final String serviceName = serviceNames[i];
            final ShadowServiceEntry.Builder serviceEntryBuilder = new ShadowServiceEntry.Builder(serviceName);

            final IBinder originService = ServiceManagerBridge.getService(serviceName);
            serviceEntryBuilder.originService(originService);
            if (originService == null) {
                ShadowServiceEntry shadowServiceEntry = serviceEntryBuilder.state(ShadowServiceEntry.State.CANNOT_GET_ORIGIN_SERVICE).build();
                nameAndServiceMap.put(shadowServiceEntry.name, shadowServiceEntry);
                ShadowLog.e("cannot get service; shadowServiceEntry=" + shadowServiceEntry);
                continue;
            }

            final String serviceInterfaceName;
            final String serviceStubName;
            try {
                serviceInterfaceName = originService.getInterfaceDescriptor();
                serviceStubName = serviceInterfaceName + "$Stub";
                serviceEntryBuilder.interfaceDescriptor(serviceInterfaceName)
                        .interfaceClassName(serviceInterfaceName)
                        .stubClassName(serviceStubName);
            } catch (Throwable e) {
                ShadowServiceEntry shadowServiceEntry = serviceEntryBuilder.state(ShadowServiceEntry.State.CANNOT_GET_INTERFACE_DESCRIPTOR).build();
                nameAndServiceMap.put(shadowServiceEntry.name, shadowServiceEntry);
                ShadowLog.e("cannot get service interface descriptor; shadowServiceEntry=" + shadowServiceEntry, e);
                continue;
            }

            final Class<?> serviceInterfaceClass;
            try {
                serviceInterfaceClass = Class.forName(serviceInterfaceName);
                serviceEntryBuilder.interfaceClass(serviceInterfaceClass);
            } catch (Throwable e) {
                ShadowServiceEntry shadowServiceEntry = serviceEntryBuilder.state(ShadowServiceEntry.State.CANNOT_LOAD_INTERFACE_CLASS).build();
                nameAndServiceMap.put(shadowServiceEntry.name, shadowServiceEntry);
                ShadowLog.e("cannot load service interface class; shadowServiceEntry=" + shadowServiceEntry, e);
                continue;
            }

            final Class<?> serviceStubClass;
            try {
                serviceStubClass = Class.forName(serviceStubName);
                serviceEntryBuilder.stubClass(serviceStubClass);
            } catch (Throwable e) {
                ShadowServiceEntry shadowServiceEntry = serviceEntryBuilder.state(ShadowServiceEntry.State.CANNOT_LOAD_STUB_CLASS).build();
                nameAndServiceMap.put(shadowServiceEntry.name, shadowServiceEntry);
                ShadowLog.e("cannot load service stub class; shadowServiceEntry=" + shadowServiceEntry, e);
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

            final Object originInterface;
            try {
                Method method_asInterface = serviceStubClass.getDeclaredMethod("asInterface", IBinder.class);
                method_asInterface.setAccessible(true);
                originInterface = method_asInterface.invoke(null, originService);
                if (originInterface == null) {
                    throw new NullPointerException("originInterface is null");
                }
                serviceEntryBuilder.originInterface(originInterface);
            } catch (Throwable e) {
                ShadowServiceEntry shadowServiceEntry = serviceEntryBuilder.state(ShadowServiceEntry.State.CANNOT_GET_ORIGIN_INTERFACE).build();
                nameAndServiceMap.put(shadowServiceEntry.name, shadowServiceEntry);
                ShadowLog.e("cannot get origin interface; shadowServiceEntry=" + shadowServiceEntry, e);
                continue;
            }

            try {
                final ClassLoader classLoader = originService.getClass().getClassLoader();
                final ShadowServiceInvocationHandler handler = new ShadowServiceInvocationHandler(serviceName, originInterface);
                final Object proxyInterface = Proxy.newProxyInstance(classLoader, new Class[]{serviceInterfaceClass}, handler);
                final IBinder proxyService = (IBinder) Proxy.newProxyInstance(classLoader, new Class[]{IBinder.class},
                        (proxy, method, args) -> "queryLocalInterface".equals(method.getName()) ? proxyInterface : method.invoke(originService, args)
                );
                final ShadowServiceEntry serviceEntry = serviceEntryBuilder.proxyService(proxyService)
                        .proxyInterface(proxyInterface)
                        .handler(handler)
                        .state(ShadowServiceEntry.State.SUCCESS)
                        .build();
                nameAndServiceMap.put(serviceEntry.name, serviceEntry);
            } catch (Throwable e) {
                ShadowServiceEntry shadowServiceEntry = serviceEntryBuilder.state(ShadowServiceEntry.State.FAIL_CREATE_PROXY).build();
                nameAndServiceMap.put(shadowServiceEntry.name, shadowServiceEntry);
                ShadowLog.e("fail create proxy; shadowServiceEntry=" + shadowServiceEntry, e);
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
                    ShadowLog.e("success intercept service=" + serviceEntry.name);
                } catch (Throwable e) {
                    ShadowLog.e("fail intercept service=" + serviceEntry.name, e);
                }
            }
        }

        ShadowLog.e("end intercept service.");
    }
}
