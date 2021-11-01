package com.coofee.shadowapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.shadow.ShadowLog;
import android.util.Log;
import dalvik.system.DexFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

public class Util {
    public static void print(Context context) {
        ShadowLog.d("", new Throwable());

        Context baseContext = context;
        while (true) {
            ShadowLog.d("Util.print(); baseContext=" + baseContext);

            if (baseContext instanceof ContextWrapper) {
                baseContext = ((ContextWrapper) baseContext).getBaseContext();
            } else {
                break;
            }
        }
    }

    public static interface Filter<T> {
        boolean filter(T t);
    }

    public static Set<String> findClassBy(String packageCodePath, Filter<String> filter) {
        try {
            DexFile dexFile = new DexFile(packageCodePath);
            Enumeration<String> entries = dexFile.entries();
            if (entries != null) {
                Set<String> classSet = new LinkedHashSet<>();
                while (entries.hasMoreElements()) {
                    String className = entries.nextElement();
                    if (filter.filter(className)) {
                        classSet.add(className);
                    }
                }
                return classSet;
            }

        } catch (IOException e) {
            ShadowLog.e("fail findClassBy", e);
        }

        return null;
    }

    public static boolean copy(InputStream input, OutputStream output) {
        final byte[] data = new byte[8192];
        int len = -1;
        try {
            while ((len = input.read(data)) != -1) {
                output.write(data, 0, len);
            }
            output.flush();
            output.close();
            return true;
        } catch (Throwable e) {
            ShadowLog.e("fail copy input to output", e);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }
}
