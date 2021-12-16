package com.coofee.componentmonitor.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;

import java.util.List;

public class IntentUtil {

    public static ResolveInfo resolveActivity(Context context, Intent intent) {
        return context.getPackageManager().resolveActivity(intent, 0);
    }

    public static ResolveInfo resolveService(Context context, Intent intent) {
        return context.getPackageManager().resolveService(intent, 0);
    }

    public static ProviderInfo resolveContentProvider(Context context, String authority) {
        return context.getPackageManager().resolveContentProvider(authority, 0);
    }

    public static List<ResolveInfo> resolveBroadcastReceiver(Context context, Intent intent) {
        return context.getPackageManager().queryBroadcastReceivers(intent, 0);
    }

}
