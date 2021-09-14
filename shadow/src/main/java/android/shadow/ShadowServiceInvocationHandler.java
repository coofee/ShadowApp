package android.shadow;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class ShadowServiceInvocationHandler implements InvocationHandler {

    private final String mServiceName;

    private final Object mOriginInterface;

    private final Map<String, ShadowServiceInterceptor> mInterceptMethodMap = new ConcurrentHashMap<>();

    private final AtomicReference<ShadowServiceInterceptor> mInterceptorRef = new AtomicReference<>(null);

    public ShadowServiceInvocationHandler(String serviceName, Object originInterface) {
        this.mServiceName = serviceName;
        this.mOriginInterface = originInterface;
    }

    public ShadowServiceInvocationHandler add(Collection<ShadowServiceInterceptor> interceptors) {
        if (interceptors == null || interceptors.isEmpty()) {
            return this;
        }

        for (ShadowServiceInterceptor interceptor : interceptors) {
            add(interceptor);
        }

        return this;
    }

    public ShadowServiceInvocationHandler add(ShadowServiceInterceptor interceptor) {
        if (interceptor == null) {
            ShadowLog.logThrow("cannot add interceptor=null of service=" + mServiceName);
            return this;
        }

        final String provideInterceptServiceName = interceptor.provideInterceptServiceName();
        if (!mServiceName.equals(provideInterceptServiceName)) {
            ShadowLog.logThrow("cannot add interceptor=" + interceptor + ", service name conflict " +
                    mServiceName + " with " + provideInterceptServiceName);
            return this;
        }

        if (interceptor.interceptAllMethod()) {
            if (mInterceptorRef.compareAndSet(null, interceptor)) {
                ShadowLog.d("add interceptor=" + interceptor + " of service=" + mServiceName + ", and other interceptors=" + mInterceptMethodMap + " make no effect.");
                return this;
            }

            final ShadowServiceInterceptor prev = mInterceptorRef.get();
            ShadowLog.logThrow("service=" + mServiceName + " already have interceptor=" + prev + ", new=" + interceptor);
            return this;
        }

        Set<String> methodNameSet = interceptor.provideInterceptMethodNames();
        if (methodNameSet == null || methodNameSet.isEmpty()) {
            ShadowLog.logThrow("interceptor=" + interceptor + " provideInterceptMethodNames is null or empty, it make no effect.");
            return this;
        }

        for (String methodName : methodNameSet) {
            if (methodName == null || methodName.isEmpty()) {
                continue;
            }

            final ShadowServiceInterceptor prev = mInterceptMethodMap.put(methodName, interceptor);
            if (prev != null) {
                mInterceptMethodMap.put(methodName, prev);

                ShadowLog.logThrow("service=" + mServiceName + " methodName=" + methodName +
                        " already have interceptor; prev=" + prev + ", new=" + interceptor);
            }

            ShadowLog.d("add interceptor=" + interceptor + " of service=" + mServiceName + " for method=" + methodName);
        }

        return this;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (ReflectUtil.isObjectMethod(method)) {
            ShadowLog.d("invoke service=" + mServiceName + " object method=" + method + " by " + mOriginInterface);
            return ReflectUtil.wrapReturnValue(method.invoke(mOriginInterface, args), method.getReturnType());
        }

        ShadowServiceInterceptor interceptor = mInterceptorRef.get();
        if (interceptor != null) {
            final ShadowConfig shadowConfig = ShadowServiceManager.sShadowConfig;
            final boolean intercept = shadowConfig.interceptAll || StackTraceUtil.invokeBy(shadowConfig.prefixSet);

            if (intercept) {
                ShadowLog.d("intercept service=" + mServiceName + " all method by " + interceptor + ", current method=" + method);
                return ReflectUtil.wrapReturnValue(interceptor.invoke(mServiceName, mOriginInterface, method, args), method.getReturnType());
            }

            ShadowLog.d("invoke service=" + mServiceName + " method=" + method + " by " + mOriginInterface);
            return ReflectUtil.wrapReturnValue(method.invoke(mOriginInterface, args), method.getReturnType());
        }

        interceptor = mInterceptMethodMap.get(method.getName());
        if (interceptor != null) {
            final ShadowConfig shadowConfig = ShadowServiceManager.sShadowConfig;
            final boolean intercept = shadowConfig.interceptAll || StackTraceUtil.invokeBy(shadowConfig.prefixSet);

            if (intercept) {
                ShadowLog.d("intercept service=" + mServiceName + " method=" + method + " by " + interceptor);
                return ReflectUtil.wrapReturnValue(interceptor.invoke(mServiceName, mOriginInterface, method, args), method.getReturnType());
            }

            ShadowLog.d("invoke service=" + mServiceName + " method=" + method + " by " + mOriginInterface);
            return ReflectUtil.wrapReturnValue(method.invoke(mOriginInterface, args), method.getReturnType());
        }

        ShadowLog.d("invoke service=" + mServiceName + " method=" + method + " by " + mOriginInterface);
        return ReflectUtil.wrapReturnValue(method.invoke(mOriginInterface, args), method.getReturnType());
    }
}
