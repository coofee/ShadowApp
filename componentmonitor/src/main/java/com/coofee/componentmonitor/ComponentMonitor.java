package com.coofee.componentmonitor;

import android.content.IntentFilter;
import android.shadow.ShadowConfig;
import com.coofee.componentmonitor.util.ProcessUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

public class ComponentMonitor {

    private static final ComponentMonitor INSTANCE = new ComponentMonitor();

    private final CopyOnWriteArraySet<ComponentObserver> mObserverSet = new CopyOnWriteArraySet<>();

    private final ComponentObserver mComponentObserver = new ComponentObserver() {

        @Override
        public void onActivity(String method, Throwable stacktrace, List<ActivityComponentInfo> activityInfos) {
            for (ComponentObserver observer : mObserverSet) {
                observer.onActivity(method, stacktrace, activityInfos);
            }
        }

        @Override
        public void onService(String method, Throwable stacktrace, ServiceComponentInfo serviceInfo) {
            for (ComponentObserver observer : mObserverSet) {
                observer.onService(method, stacktrace, serviceInfo);
            }
        }

        @Override
        public void onContentProvider(String method, Throwable stacktrace, List<ContentProviderInfo> providerInfos) {
            for (ComponentObserver observer : mObserverSet) {
                observer.onContentProvider(method, stacktrace, providerInfos);
            }
        }

        @Override
        public void onReceiver(String method, Throwable stacktrace, IntentFilter intentFilter, List<BroadcastReceiverInfo> activityInfos) {
            for (ComponentObserver observer : mObserverSet) {
                observer.onReceiver(method, stacktrace, intentFilter, activityInfos);
            }
        }
    };

    public static String currentProcessName() {
        return ProcessUtil.getProcessName();
    }

    public static ComponentMonitor getInstance() {
        return INSTANCE;
    }

    public static ComponentObserver getComponentObserver() {
        return getInstance().mComponentObserver;
    }

    public ComponentMonitor add(ComponentObserver observer) {
        if (observer != null) {
            mObserverSet.add(observer);
        }
        return this;
    }

    public ComponentMonitor remove(ComponentObserver observer) {
        if (observer != null) {
            mObserverSet.remove(observer);
        }
        return this;
    }

    public ComponentMonitor removeAll() {
        mObserverSet.clear();
        return this;
    }

    public ComponentMonitor attachTo(ShadowConfig.Builder config) {
        config.add(new IActivityManagerInterceptor())
                .add(new IActivityTaskManagerInterceptor())
        ;
        return this;
    }

}
