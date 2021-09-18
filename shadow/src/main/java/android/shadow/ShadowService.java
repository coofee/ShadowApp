package android.shadow;


import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ShadowService {
    private static final Map<String, Method> sMethodCache = new ConcurrentHashMap<>();

    public static final ShadowService EMPTY = new ShadowService(null);

    private final ShadowServiceEntry serviceEntry;

    public ShadowService(ShadowServiceEntry serviceEntry) {
        this.serviceEntry = serviceEntry;
    }

    public <T> Invoker<T> method(String methodName, Class<?>... parameterTypes) {
        final String key = keyOf(methodName, parameterTypes);
        if (this.serviceEntry == null) {
            ShadowLog.e("cannot find method for service entry is null; key=" + key);
            return (Invoker<T>) args -> null;
        }

        final Object originInterface = this.serviceEntry.originInterface;
        Method method = sMethodCache.get(key);

        if (method == null) {
            synchronized (sMethodCache) {
                method = sMethodCache.get(key);
                if (method == null) {
                    try {
                        Method declaredMethod = originInterface.getClass().getDeclaredMethod(methodName, parameterTypes);
                        declaredMethod.setAccessible(true);
                        sMethodCache.put(key, declaredMethod);
                        method = declaredMethod;
                    } catch (NoSuchMethodException e) {
                        ShadowLog.e("cannot find method by key=" + key, e);
                    }
                }
            }
        }

        final Method finalMethod = method;
        if (finalMethod == null) {
            return (Invoker<T>) args -> null;
        }

        return args -> {
            try {
                return (T) finalMethod.invoke(originInterface, args);
            } catch (Throwable e) {
                ShadowLog.e("fail execute method by key=" + key, e);
            }

            return null;
        };
    }

    public static interface Invoker<T> {
        T invoke(Object... args);
    }

    private String keyOf(String methodName, Class<?>... parameterTypes) {
        return methodName + "#" + Arrays.toString(parameterTypes);
    }
}
