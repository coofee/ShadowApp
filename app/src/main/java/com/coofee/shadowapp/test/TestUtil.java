package com.coofee.shadowapp.test;

import android.shadow.ShadowServiceManager;
import android.util.Log;

import java.util.concurrent.Callable;

public class TestUtil {

    public static <V> void exec(Callable<V> callable) {
        try {
            callable.call();
        } catch (Throwable e) {
            Log.e(ShadowServiceManager.TAG, "fail exec", e);
        }
    }
}
