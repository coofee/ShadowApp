package android.shadow;

import java.lang.reflect.Method;
import java.util.Set;

public interface ShadowServiceInterceptor {
    Object invoke(String serviceName, Object service, Method method, Object[] args) throws Throwable;

    String provideInterceptServiceName();

    Set<String> provideInterceptMethodNames();

    default boolean interceptAllMethod() {
        return false;
    }
}
