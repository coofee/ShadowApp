package com.coofee.shadowapp.test;

import android.shadow.ReflectUtil;
import android.shadow.ShadowLog;
import com.coofee.shadowapp.App;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.android.AndroidClassLoadingStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class RuntimeUtil {
    private static final String TAG = "RuntimeUtil";

    private static final Objenesis OBJENESIS = new ObjenesisStd();

    public static void demo() {
        if (!replaceRuntimeIfNeed()) {
            return;
        }

        try {
            System.loadLibrary("fake_so");
        } catch (UnsatisfiedLinkError e) {
            ShadowLog.e("RuntimeTime; fail invoke loadLibrary ", e);
        }

        try {
            Runtime.getRuntime().loadLibrary("fake_so");
        } catch (UnsatisfiedLinkError e) {
            ShadowLog.e("RuntimeTime; fail invoke loadLibrary ", e);
        }

        try {
            System.load("/lib/libfake_so.so");
        } catch (UnsatisfiedLinkError e) {
            ShadowLog.e("RuntimeTime; fail invoke load ", e);
        }

        try {
            Runtime.getRuntime().load("/lib/libfake_so.so");
        } catch (UnsatisfiedLinkError e) {
            ShadowLog.e("RuntimeTime; fail invoke loadLibrary ", e);
        }

    }


    private static volatile boolean sReplaced = false;

    private synchronized static boolean replaceRuntimeIfNeed() {
        if (sReplaced) {
            return sReplaced;
        }

        final Runtime originRuntime = Runtime.getRuntime();
        ShadowLog.e("RuntimeUtil; originRuntime=" + originRuntime);


        Class<? extends Runtime> loaded = new ByteBuddy()
                .subclass(Runtime.class)
                .name("java.lang.Runtime$Proxy")
                .method(ElementMatchers.any())
                .intercept(InvocationHandlerAdapter.of(new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        try {
                            ShadowLog.e("RuntimeUtil; invoke method=" + method.getName() + ", args=" + Arrays.toString(args));
                            Object result = ReflectUtil.wrapReturnValue(method.invoke(originRuntime, args), method.getReturnType());
                            ShadowLog.e("RuntimeUtil; invoke method=" + method.getName() + ", args=" + Arrays.toString(args) + ", result=" + result);
                            return result;
                        } catch (InvocationTargetException e) {
                            throw e.getCause();
                        }
                    }
                }))
                .make()
                .load(Runtime.class.getClassLoader(), new AndroidClassLoadingStrategy.Wrapping(App.getContext().getCodeCacheDir()))
                .getLoaded();


        ReflectUtil.print(Runtime.class);
        final Runtime proxyRuntime = OBJENESIS.newInstance(loaded);
        ReflectUtil.print(proxyRuntime.getClass());
        try {
            Field field_currentRuntime = Runtime.class.getDeclaredField("currentRuntime");
            field_currentRuntime.setAccessible(true);
            field_currentRuntime.set(null, proxyRuntime);
            ShadowLog.e("RuntimeUtil; replace originRuntime=" + originRuntime + " by proxyRuntime=" + proxyRuntime);
            sReplaced = true;
            return true;

        } catch (Throwable e) {
            ShadowLog.e("RuntimeUtil fail replaceRuntime", e);
        }

        return false;
    }


}
