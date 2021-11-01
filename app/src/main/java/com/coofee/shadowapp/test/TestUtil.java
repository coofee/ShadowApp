package com.coofee.shadowapp.test;

import android.shadow.ShadowServiceManager;
import android.util.Log;

import java.util.concurrent.Callable;

public class TestUtil {

    public static <V> void exec(Callable<V> callable) {
        try {
            V result = callable.call();
            Log.d(ShadowServiceManager.TAG, "result=" + result);
        } catch (Throwable e) {
            Log.e(ShadowServiceManager.TAG, "fail exec", e);
        }
    }

    public static void exec(Runnable task) {
        try {
            task.run();
        } catch (Throwable e) {
            Log.e(ShadowServiceManager.TAG, "fail exec", e);
        }
    }

}
