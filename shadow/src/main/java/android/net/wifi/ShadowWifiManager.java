package android.net.wifi;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.shadow.ReflectUtil;
import android.shadow.ShadowLog;
import android.shadow.ShadowServiceManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class ShadowWifiManager extends WifiManager {

    public static ShadowWifiManager create(Context baseContext) {
        Class<?> class_WifiManager = null;
        try {
            class_WifiManager = baseContext.getClassLoader().loadClass(WifiManager.class.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
//
//        ReflectUtil.printConstructor(class_WifiManager);
//        ReflectUtil.printFields(class_WifiManager);
//
//        try {
//            Class<?> clazz = baseContext.getClassLoader().loadClass("android.net.ConnectivityThread");
//            Method getInstanceLooper = clazz.getDeclaredMethod("getInstanceLooper", null);
//            getInstanceLooper.setAccessible(true);
//            Object looper = getInstanceLooper.invoke(null);
//            ShadowLog.e("getInstanceLooper=" + looper);
//        } catch (Throwable e) {
//            ShadowLog.e("fail getInstanceLooper", e);
//        }

        try {
            WifiManager wifiManager = (WifiManager) baseContext.getSystemService(Service.WIFI_SERVICE);
            IWifiManager service = (IWifiManager) ((Field) ReflectUtil.makeAccessible(class_WifiManager.getDeclaredField("mService"))).get(wifiManager);
            Looper looper = (Looper) ((Field) ReflectUtil.makeAccessible(class_WifiManager.getDeclaredField("mLooper"))).get(wifiManager);
            return new ShadowWifiManager(baseContext, service, looper);
        } catch (Throwable e) {
            ShadowLog.e("fail create ShadowWifiManager", e);
        }

        return null;
    }

    public ShadowWifiManager(Context context, IWifiManager service, Looper looper) {
        super(context, service, looper);
    }

    @SuppressLint("MissingPermission")
    public List<WifiConfiguration> getConfiguredNetworks() {
        ShadowLog.e("access getConfiguredNetworks", new Throwable());
        return ShadowServiceManager.config().wifiManagerProvider().getConfiguredNetworks();
    }

    public List<ScanResult> getScanResults() {
        ShadowLog.e("access getScanResults", new Throwable());
        return ShadowServiceManager.config().wifiManagerProvider().getScanResults();
    }

    public void startLocalOnlyHotspot(WifiManager.LocalOnlyHotspotCallback callback, Handler handler) {
        ShadowLog.e("access startLocalOnlyHotspot", new Throwable());
        ShadowServiceManager.config().wifiManagerProvider().startLocalOnlyHotspot(callback, handler);
    }
}
