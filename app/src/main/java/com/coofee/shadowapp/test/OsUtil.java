package com.coofee.shadowapp.test;

import android.shadow.ReflectUtil;
import android.shadow.ShadowLog;
import com.coofee.shadowapp.App;
import com.coofee.shadowapp.Util;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.android.AndroidClassLoadingStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.*;
import java.util.Arrays;

public class OsUtil {

    public static void replaceOsByInvocationHandler() {
        try {
            Class<?> class_Os = Class.forName("libcore.io.Os");
            ReflectUtil.print(class_Os);

//            "libcore.io.ForwardingOs"

            Class<?> class_Libcore = Class.forName("libcore.io.Libcore");

//            Field field_rawOs = class_Libcore.getDeclaredField("rawOs");
//            field_rawOs.setAccessible(true);
//            Object rawOs = field_rawOs.get(null);

            Field field_os = class_Libcore.getDeclaredField("os");
            field_os.setAccessible(true);
            final Object os = field_os.get(null);
            ShadowLog.e("os=" + os);
            ReflectUtil.print(os.getClass());

            Object osProxy = Proxy.newProxyInstance(os.getClass().getClassLoader(), new Class[]{class_Os}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    ShadowLog.e("before invoke os method=" + method + ", args=" + Arrays.toString(args));
//                    if ("access".equals(method.getName())) {
//                        args[1] = 0;
//                    }
                    Object result;
                    try {
                        result = ReflectUtil.wrapReturnValue(method.invoke(os, args), method.getReturnType());
                    } catch (InvocationTargetException e) {
//                        result = ReflectUtil.wrapReturnValue(null, method.getReturnType());
//                        ShadowLog.e("fail exec method=" + method, e);
                        throw e.getCause();
                    }
                    ShadowLog.e("after invoke os method=" + method + ", args=" + Arrays.toString(args) + ", result=" + result);
                    return result;
                }
            });

            field_os.set(null, osProxy);
            ShadowLog.e("osProxy=" + osProxy);
            ReflectUtil.print(osProxy.getClass());

        } catch (Throwable e) {
            ShadowLog.e("fail testOs", e);
        }
    }

    public static void replaceOfByByteBuddy() {
        try {
            Class<?> class_Os = Class.forName("libcore.io.Os");
            ReflectUtil.print(class_Os);

            Class<?> class_Libcore = Class.forName("libcore.io.Libcore");

            Field field_os = class_Libcore.getDeclaredField("os");
            field_os.setAccessible(true);
            final Object os = field_os.get(null);
            ShadowLog.e("os=" + os);
            ReflectUtil.print(os.getClass());

            Class<?> class_forwardingOs = Class.forName("libcore.io.ForwardingOs");
            Object osProxy = new ByteBuddy()
//                    .subclass(os.getClass())
                    .subclass(class_forwardingOs)
                    .method(ElementMatchers.any())
                    .intercept(InvocationHandlerAdapter.of(new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            ShadowLog.e("before invoke os method=" + method + ", args=" + Arrays.toString(args));
                            Object result = ReflectUtil.wrapReturnValue(method.invoke(os, args), method.getReturnType());
                            ShadowLog.e("after invoke os method=" + method + ", args=" + Arrays.toString(args) + ", result=" + result);
                            return result;
                        }
                    }))
                    .make()
                    .load(os.getClass().getClassLoader(), new AndroidClassLoadingStrategy.Wrapping(App.getContext().getCodeCacheDir()))
                    .getLoaded()
                    .getConstructor(class_Os)
                    .newInstance(os);

            field_os.set(null, osProxy);

            ShadowLog.e("osProxy=" + osProxy);
            ReflectUtil.print(osProxy.getClass());

        } catch (Throwable e) {
            ShadowLog.e("fail testOs", e);
        }
    }
}
