package android.shadow;

import android.app.Service;
import android.content.Context;
import android.location.ShadowLocationManager;
import android.location.ShadowLocationManagerProvider;
import android.telephony.ShadowTelephonyManager;
import android.telephony.ShadowTelephonyManagerProvider;
import androidx.annotation.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ShadowConfig {
    public final Context baseContext;

    public final Context applicationContext;

    public final Set<String> prefixSet;

    public final boolean interceptAll;

    public final boolean debug;

    final Map<String, ServiceEntry> serviceEntryMap;

    public ShadowConfig(Builder builder) {
        this.baseContext = builder.baseContext;
        this.applicationContext = builder.applicationContext;
        this.interceptAll = builder.interceptAll;
        this.debug = builder.debug;

        Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());
        set.addAll(builder.prefixSet);
        this.prefixSet = Collections.unmodifiableSet(set);
        this.serviceEntryMap = Collections.unmodifiableMap(new ConcurrentHashMap<>(builder.serviceEntryMap));
    }


    public ShadowTelephonyManagerProvider.Wrapper telephonyManagerProvider() {
        return (ShadowTelephonyManagerProvider.Wrapper) this.serviceEntryMap.get(Service.TELEPHONY_SERVICE).provider;
    }

    public ShadowLocationManagerProvider.Wrapper locationManagerProvider() {
        return (ShadowLocationManagerProvider.Wrapper) this.serviceEntryMap.get(Service.LOCATION_SERVICE).provider;
    }

    public static class ServiceEntry {
        public final String serviceName;

        public final Object service;

        public final Object provider;

        public ServiceEntry(String serviceName, Object service, Object provider) {
            this.serviceName = serviceName;
            this.service = service;
            this.provider = provider;
        }
    }

    public static class Builder {
        private final Context baseContext;

        private final Context applicationContext;

        private final Set<String> prefixSet = new HashSet<>();

        private boolean interceptAll;

        private boolean debug;

        private final Map<String, ServiceEntry> serviceEntryMap = new HashMap<>();

        public Builder(@NonNull Context baseContext, @NonNull Context applicationContext) {
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

        public Builder addService(String serviceName, Object service, Object provider) {
            if (serviceName == null || serviceName.isEmpty() || service == null || provider == null) {
                return this;
            }

            serviceEntryMap.put(serviceName, new ServiceEntry(serviceName, service, provider));
            return this;
        }


        public Builder addLocationManager(ShadowLocationManager locationManager, ShadowLocationManagerProvider provider) {
            addService(Service.LOCATION_SERVICE, locationManager, new ShadowLocationManagerProvider.Wrapper(baseContext, provider));
            return this;
        }

        public Builder addTelephonyManager(ShadowTelephonyManager telephonyManager, ShadowTelephonyManagerProvider provider) {
            addService(Service.TELEPHONY_SERVICE, telephonyManager, new ShadowTelephonyManagerProvider.Wrapper(baseContext, provider));
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
