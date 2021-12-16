package com.coofee.componentmonitor.util;

import android.content.IntentFilter;
import android.shadow.ShadowLog;
import android.text.TextUtils;
import com.coofee.componentmonitor.ComponentObserver;

import java.util.ArrayList;
import java.util.List;

public class LogComponentObserver implements ComponentObserver {

    @Override
    public void onActivity(String method, Throwable stacktrace, List<ActivityComponentInfo> activityInfos) {
        for (ActivityComponentInfo info : activityInfos) {
            log(info.activityInfo.processName, "BinderHook: ActivityManagerMethods, currentProcess=" + currentProcessName() + ", method=" + method + " start activity=" + info.activityInfo + " in process=" + info.activityInfo.processName + " by intent=" + info.intent, stacktrace);
        }
    }

    @Override
    public void onService(String method, Throwable stacktrace, ServiceComponentInfo serviceInfo) {
        log(serviceInfo.serviceInfo.processName, "BinderHook: ActivityManagerMethods, currentProcess=" + currentProcessName() + ", method=" + method + " start service=" + serviceInfo.serviceInfo + " in process=" + serviceInfo.serviceInfo.processName + " by intent=" + serviceInfo.intent, stacktrace);
    }

    @Override
    public void onContentProvider(String method, Throwable stacktrace, List<ContentProviderInfo> providerInfos) {
        for (ContentProviderInfo providerInfo : providerInfos) {
            log(providerInfo.providerInfo.processName, "BinderHook: ActivityManagerMethods, currentProcess=" + currentProcessName() + ", method=" + method + " content provider=" + providerInfo + " in process=" + providerInfo.providerInfo.processName, stacktrace);
        }
    }

    @Override
    public void onReceiver(String method, Throwable stacktrace, IntentFilter intentFilter, List<BroadcastReceiverInfo> activityInfos) {
        if ("broadcastIntent".equals(method) || "broadcastIntentWithFeature".equals(method)) {
            for (BroadcastReceiverInfo receiverInfo : activityInfos) {
                log(receiverInfo.activityInfo.processName, "ActivityManagerMethods, send broadcast receiver=" + receiverInfo.activityInfo + " in process=" + receiverInfo.activityInfo.processName + " by intent=" + receiverInfo.intent, new Throwable());
            }
            return;
        }

        if ("registerReceiver".equals(method) || "registerReceiverWithFeature".equals(method)) {
            StringBuilder msg = new StringBuilder("BinderHook: ActivityManagerMethods, currentProcess=")
                    .append(currentProcessName())
                    .append(" register broadcast receiver");

            if (intentFilter != null) {
                msg.append(" by filter");
                final int actionSize = intentFilter.countActions();
                if (actionSize > 0) {
                    List<String> actionList = new ArrayList<>(actionSize);
                    for (int i = 0; i < actionSize; i++) {
                        actionList.add(intentFilter.getAction(i));
                    }

                    msg.append(" with action=").append(actionList);
                }

                final int categorySize = intentFilter.countCategories();
                if (categorySize > 0) {
                    List<String> categoryList = new ArrayList<>(categorySize);
                    for (int i = 0; i < categorySize; i++) {
                        categoryList.add(intentFilter.getCategory(i));
                    }
                    msg.append(" with categories=").append(categoryList);
                }
            }

            for (BroadcastReceiverInfo receiverInfo : activityInfos) {
                msg.append(" receiver=").append(receiverInfo.activityInfo)
                        .append(" in process=")
                        .append(receiverInfo.activityInfo.processName).
                        append(" by intent=").append(receiverInfo.intent);
            }

            log("", msg.toString(), stacktrace);
        }
    }

    public static String currentProcessName() {
        return ProcessUtil.getProcessName();
    }

    private static void log(String componentProcessName, String msg, Throwable throwable) {
        if (TextUtils.isEmpty(componentProcessName) || TextUtils.equals(currentProcessName(), componentProcessName)) {
            ShadowLog.d(msg, throwable);
        } else {
            ShadowLog.e(msg, throwable);
        }
    }
}
