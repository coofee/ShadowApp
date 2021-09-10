package android.shadow;

import android.content.Context;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ShadowConfig {
    public final Context baseContext;

    public final Context applicationContext;

    public final Set<String> prefixSet;

    public final boolean interceptAll;

    public final boolean debug;

    public final Map<String, LinkedHashSet<ShadowServiceInterceptor>> interceptorMap;

    public ShadowConfig(Builder builder) {
        this.baseContext = builder.baseContext;
        this.applicationContext = builder.applicationContext;
        this.interceptAll = builder.interceptAll;
        this.debug = builder.debug;

        Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());
        set.addAll(builder.prefixSet);
        this.prefixSet = Collections.unmodifiableSet(set);

        this.interceptorMap = Collections.unmodifiableMap(builder.interceptorMap);
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    public static class Builder {
        private final Context baseContext;

        private final Context applicationContext;

        private final Set<String> prefixSet = new HashSet<>();

        private boolean interceptAll;

        private boolean debug;

        private final Map<String, LinkedHashSet<ShadowServiceInterceptor>> interceptorMap = new LinkedHashMap<>();

        protected Builder(ShadowConfig shadowConfig) {
            this.baseContext = shadowConfig.baseContext;
            this.applicationContext = shadowConfig.applicationContext;
            this.prefixSet.addAll(shadowConfig.prefixSet);
            this.interceptAll = shadowConfig.interceptAll;
            this.debug = shadowConfig.debug;
            this.interceptorMap.putAll(shadowConfig.interceptorMap);
        }

        public Builder(Context baseContext, Context applicationContext) {
            this.baseContext = baseContext;
            this.applicationContext = applicationContext;
        }

        public Builder interceptAll(boolean interceptAll) {
            this.interceptAll = interceptAll;
            return this;
        }

        public Builder addPackageOrClassNamePrefix(String... packageOrClassNamePrefix) {
            if (packageOrClassNamePrefix == null || packageOrClassNamePrefix.length < 1) {
                return this;
            }

            for (String prefix : packageOrClassNamePrefix) {
                if (prefix != null && !prefix.isEmpty()) {
                    this.prefixSet.add(prefix);
                }
            }
            return this;
        }

        public Builder add(ShadowServiceInterceptor interceptor) {
            if (interceptor == null) {
                return this;
            }

            final String serviceName = interceptor.provideInterceptServiceName();
            if (serviceName == null || serviceName.isEmpty()) {
                ShadowLog.e("interceptor service name is null or empty; interceptor=" + interceptor);
                return this;
            }

            LinkedHashSet<ShadowServiceInterceptor> interceptorSet = this.interceptorMap.get(serviceName);
            if (interceptorSet == null) {
                interceptorSet = new LinkedHashSet<>();
                this.interceptorMap.put(serviceName, interceptorSet);
            }

            final boolean interceptAllMethod = interceptor.interceptAllMethod();
            if (interceptAllMethod) {
                interceptorSet.add(interceptor);
                return this;
            }

            final Set<String> methodNames = interceptor.provideInterceptMethodNames();
            if (methodNames == null || methodNames.isEmpty()) {
                ShadowLog.e("interceptor method names is null or empty; interceptor=" + interceptor);
                return this;
            }

            interceptorSet.add(interceptor);
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public ShadowConfig build() {
            return new ShadowConfig(this);
        }
    }
}
