package com.coofee.shadowapp.shadow.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.shadow.ShadowLog;
import com.coofee.shadowapp.App;

public class IntentUtil {
    public static void resolveIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        PackageManager packageManager = App.getContext().getPackageManager();
        ResolveInfo resolveInfo = packageManager.resolveActivity(intent, 0);
        if (resolveInfo != null && resolveInfo.activityInfo != null) {
            CharSequence targetAppName = resolveInfo.loadLabel(packageManager);
            String targetAppPackageName = resolveInfo.activityInfo.packageName;
            String targetProcessName = resolveInfo.activityInfo.processName;
            ShadowLog.e("service=activity, targetAppName=" + targetAppName + ", targetAppPackageName=" + targetAppPackageName + ", targetProcessName=" + targetProcessName);
        }
    }
}
