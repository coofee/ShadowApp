package com.coofee.componentmonitor;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.shadow.ReflectUtil;
import android.shadow.ShadowLog;
import android.shadow.ShadowServiceManager;
import android.text.TextUtils;
import com.coofee.componentmonitor.util.IntentUtil;
import com.coofee.componentmonitor.util.ProcessUtil;

import java.lang.reflect.Field;
import java.util.*;

public class ActivityManagerMethods {

    public static final Set<String> ACTIVITY_METHODS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "startActivity",
            "startActivities",
            "startNextMatchingActivity"
    )));

    public static final Set<String> SERVICE_METHODS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "startService",
            "bindService",
            "bindIsolatedService",
            "publishService"
    )));

    public static final Set<String> CONTENT_PROVIDER_METHODS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "getContentProvider",
            "getContentProviderExternal",
            "publishContentProviders"
    )));

    public static final Set<String> BROADCAST_RECEIVER_METHODS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "broadcastIntent",
            "broadcastIntentWithFeature",
            "registerReceiver",
            "registerReceiverWithFeature"
    )));

    public static final Set<String> METHOD_ALL = Collections.unmodifiableSet(union(
            ACTIVITY_METHODS,
            SERVICE_METHODS,
            CONTENT_PROVIDER_METHODS,
            BROADCAST_RECEIVER_METHODS
    ));

    public static <T> Set<T> union(Collection<T>... sets) {
        Set<T> union = new HashSet<>();
        if (sets != null) {
            for (Collection<T> set : sets) {
                union.addAll(set);
            }
        }
        return union;
    }

    public static void resolveActivityIntent(String method, Object[] args, long cost) {
        if (args == null || args.length < 1) {
            return;
        }

        final Context context = ShadowServiceManager.config().applicationContext;
        final List<ComponentObserver.ActivityComponentInfo> activityInfos = new ArrayList<>();
        for (Object arg : args) {
            if (arg instanceof Intent) {
                ResolveInfo resolveInfo = IntentUtil.resolveActivity(context, (Intent) arg);
                if (resolveInfo != null && resolveInfo.activityInfo != null) {
                    activityInfos.add(new ComponentObserver.ActivityComponentInfo((Intent) arg, resolveInfo.activityInfo));
                }
            }

            if (arg instanceof Intent[]) {
                for (Intent intent : (Intent[]) arg) {
                    ResolveInfo resolveInfo = IntentUtil.resolveActivity(context, intent);
                    if (resolveInfo != null && resolveInfo.activityInfo != null) {
                        activityInfos.add(new ComponentObserver.ActivityComponentInfo(intent, resolveInfo.activityInfo));
                    }
                }
            }
        }

        ComponentMonitor.getComponentObserver().onActivity(method, new Throwable(), activityInfos, cost);
    }

    public static void resolveServiceIntent(String method, Object[] args, long cost) {
        if (args == null || args.length < 1) {
            return;
        }

        final Context context = ShadowServiceManager.config().applicationContext;

        for (Object arg : args) {
            if (arg instanceof Intent) {
                ResolveInfo resolveInfo = IntentUtil.resolveService(context, (Intent) arg);
                if (resolveInfo != null && resolveInfo.serviceInfo != null) {
                    ComponentMonitor.getComponentObserver().onService(method, new Throwable(), new ComponentObserver.ServiceComponentInfo((Intent) arg, resolveInfo.serviceInfo), cost);
                }

                break;
            }
        }
    }

    public static void resolveContentProviderIntent(String method, Object[] args, Object returnValue, long cost) {
        if (args == null || args.length < 1) {
            return;
        }

        final List<ComponentObserver.ContentProviderInfo> providerInfos = new ArrayList<>();
        if (returnValue != null) {
            try {
                Field info = ReflectUtil.getField(returnValue.getClass(), "info");
                info.setAccessible(true);
                ProviderInfo providerInfo = (ProviderInfo) info.get(returnValue);
                if (providerInfo != null) {
                    providerInfos.add(new ComponentObserver.ContentProviderInfo(providerInfo));
                    ComponentMonitor.getComponentObserver().onContentProvider(method, new Throwable(), providerInfos, cost);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            return;
        }

        for (Object arg : args) {

            if (arg instanceof List) {
                List providerInfoHolderList = (List) arg;
                for (Object item : providerInfoHolderList) {
                    try {
                        Field info = ReflectUtil.getField(item.getClass(), "info");
                        info.setAccessible(true);
                        ProviderInfo providerInfo = (ProviderInfo) info.get(item);
                        if (providerInfo != null) {
                            providerInfos.add(new ComponentObserver.ContentProviderInfo(providerInfo));
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }

                break;
            }
        }

        ComponentMonitor.getComponentObserver().onContentProvider(method, new Throwable(), providerInfos, cost);
    }

    public static void resolveBroadcastReceiverIntent(String method, Object[] args, Object returnValue, long cost) {
        if (args == null || args.length < 1) {
            return;
        }

        final Context context = ShadowServiceManager.config().applicationContext;
        final List<ComponentObserver.BroadcastReceiverInfo> activityInfos = new ArrayList<>();
        IntentFilter intentFilter = null;

        if ("broadcastIntent".equals(method) || "broadcastIntentWithFeature".equals(method)) {
            for (Object arg : args) {
                if (arg instanceof Intent) {
                    List<ResolveInfo> resolveInfos = IntentUtil.resolveBroadcastReceiver(context, (Intent) arg);
                    if (resolveInfos == null || resolveInfos.isEmpty()) {
                        return;
                    }

                    for (ResolveInfo resolveInfo : resolveInfos) {
                        if (resolveInfo != null && resolveInfo.activityInfo != null) {
                            activityInfos.add(new ComponentObserver.BroadcastReceiverInfo((Intent) arg, resolveInfo.activityInfo));
                        }
                    }

                    break;
                }
            }
        } else if ("registerReceiver".equals(method) || "registerReceiverWithFeature".equals(method)) {

            if (returnValue instanceof Intent) {
                List<ResolveInfo> resolveInfos = IntentUtil.resolveBroadcastReceiver(context, (Intent) returnValue);
                if (resolveInfos == null || resolveInfos.isEmpty()) {
                    return;
                }

                for (ResolveInfo resolveInfo : resolveInfos) {
                    if (resolveInfo != null && resolveInfo.activityInfo != null) {
                        activityInfos.add(new ComponentObserver.BroadcastReceiverInfo((Intent) returnValue, resolveInfo.activityInfo));
                    }
                }
            }

            for (Object arg : args) {
                if (arg instanceof IntentFilter) {
                    intentFilter = (IntentFilter) arg;
                    break;
                }
            }
        }

        ComponentMonitor.getComponentObserver().onReceiver(method, new Throwable(), intentFilter, activityInfos, cost);
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
