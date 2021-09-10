package com.coofee.shadowapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.shadow.ShadowLog;

public class Util {
    public static void print(Context context) {
        ShadowLog.e("", new Throwable());
        
        Context baseContext = context;
        while (true) {
            ShadowLog.e("Util.print(); baseContext=" + baseContext);

            if (baseContext instanceof ContextWrapper) {
                baseContext = ((ContextWrapper) baseContext).getBaseContext();
            } else {
                break;
            }
        }
    }
}
