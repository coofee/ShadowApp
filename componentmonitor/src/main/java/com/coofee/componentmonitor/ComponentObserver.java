package com.coofee.componentmonitor;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;

import java.util.List;

public interface ComponentObserver {
    void onActivity(String method, Throwable stacktrace, List<ActivityComponentInfo> activityInfos, long cost);

    void onService(String method, Throwable stacktrace, ServiceComponentInfo serviceInfo, long cost);

    void onContentProvider(String method, Throwable stacktrace, List<ContentProviderInfo> providerInfos, long cost);

    void onReceiver(String method, Throwable stacktrace, IntentFilter intentFilter, List<BroadcastReceiverInfo> activityInfos, long cost);

    public static class ActivityComponentInfo {
        public final Intent intent;
        public final ActivityInfo activityInfo;

        public ActivityComponentInfo(Intent intent, ActivityInfo activityInfo) {
            this.intent = intent;
            this.activityInfo = activityInfo;
        }

        @Override
        public String toString() {
            return "ActivityComponentInfo{" +
                    "intent=" + intent +
                    ", activityInfo=" + activityInfo +
                    '}';
        }
    }

    public static class ServiceComponentInfo {
        public final Intent intent;
        public final ServiceInfo serviceInfo;

        public ServiceComponentInfo(Intent intent, ServiceInfo serviceInfo) {
            this.intent = intent;
            this.serviceInfo = serviceInfo;
        }

        @Override
        public String toString() {
            return "ServiceComponentInfo{" +
                    "intent=" + intent +
                    ", serviceInfo=" + serviceInfo +
                    '}';
        }
    }

    public static class ContentProviderInfo {
        public ProviderInfo providerInfo;

        public ContentProviderInfo(ProviderInfo providerInfo) {
            this.providerInfo = providerInfo;
        }

        @Override
        public String toString() {
            return "ContentProviderInfo{" +
                    "providerInfo=" + providerInfo +
                    '}';
        }
    }

    public static class BroadcastReceiverInfo {
        public final Intent intent;

        public final ActivityInfo activityInfo;

        public BroadcastReceiverInfo(Intent intent, ActivityInfo activityInfo) {
            this.intent = intent;
            this.activityInfo = activityInfo;
        }

        @Override
        public String toString() {
            return "BroadcastReceiverInfo{" +
                    "intent=" + intent +
                    ", activityInfo=" + activityInfo +
                    '}';
        }
    }
}