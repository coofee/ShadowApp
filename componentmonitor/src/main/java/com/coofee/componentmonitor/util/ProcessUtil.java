package com.coofee.componentmonitor.util;

import android.app.Application;
import android.os.Process;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

public class ProcessUtil {

    private static volatile String sProcessName;

    public static String getProcessName() {
        if (!TextUtils.isEmpty(sProcessName)) {
            return sProcessName;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            String processName = Application.getProcessName();
            if (!TextUtils.isEmpty(processName)) {
                sProcessName = processName;
                return processName;
            }
        }

        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentProcessName = activityThreadClass.getDeclaredMethod("currentProcessName");
            currentProcessName.setAccessible(true);
            String processName = (String) currentProcessName.invoke(null, null);
            if (!TextUtils.isEmpty(processName)) {
                sProcessName = processName;
                return processName;
            }
        } catch (Throwable e) {
            // ignore
        }

        final int pid = Process.myPid();
        String processName = readProcessNameByPid(pid);
        if (!TextUtils.isEmpty(processName)) {
            sProcessName = processName;
            return processName;
        }

        return String.valueOf(pid);
    }

    private static String readProcessNameByPid(int pid) {
        BufferedReader cmdlineReader = null;

        try {
            cmdlineReader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/" + pid + "/cmdline"), "iso-8859-1"));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = cmdlineReader.readLine()) != null) {
                builder.append(line);
            }

            builder.trimToSize();
            return builder.toString();
        } catch (Throwable e) {
            // ignore
        } finally {
            if (cmdlineReader != null) {
                try {
                    cmdlineReader.close();
                } catch (IOException var10) {
                    // ignore
                }
            }
        }

        return null;
    }

}
