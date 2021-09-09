package com.coofee.shadowapp;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.shadow.ShadowApplication;
import android.shadow.ShadowContext;
import android.shadow.ShadowLog;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.android.AndroidClassLoadingStrategy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        android.os.Debug.waitForDebugger();
        setContentView(R.layout.activity_main);

        testClassLoader();

        testActivity();

        testGetApplicationContext();

        testShadowContext();

        testShadowApplication();

//        testPackageManager();

        findViewById(R.id.replace_package_manager).setOnClickListener(view -> {
            testPackageManager();

            startService(new Intent(this, TestIntentService.class));
        });
    }

    private void testClassLoader() {
        PackageManager packageManager = getPackageManager();
        ClassLoader frameworkClassLoader = PackageManager.class.getClassLoader();
        ClassLoader myClassLoader = MainActivity.this.getClass().getClassLoader();
        ShadowLog.e("MainActivity.testClassLoader(); PackageManager.class.getClassLoader()=" + frameworkClassLoader + ", MainActivity.this.getClass().getClassLoader()=" + myClassLoader);
    }

    private void testPackageManager() {
        long startTime = SystemClock.elapsedRealtime();

        final PackageManager packageManager = getPackageManager();
        ShadowLog.e("MainActivity.testPackageManager()=" + packageManager +
                ", class=" + packageManager.getClass() +
                ", superClass=" + packageManager.getClass().getSuperclass() +
                ", interfaces=" + Arrays.toString(packageManager.getClass().getInterfaces()));

        try {
            Class<?> class_ApplicationPackageManager = Class.forName("android.app.ApplicationPackageManager");
            Field field_mContext = class_ApplicationPackageManager.getDeclaredField("mContext");
            field_mContext.setAccessible(true);
            Context contextImpl = (Context) field_mContext.get(packageManager);
            ShadowLog.e("MainActivity.testPackageManager(), contextImpl=" + contextImpl);

            Field field_mPM = class_ApplicationPackageManager.getDeclaredField("mPM");
            field_mPM.setAccessible(true);
            Object iPackageManager = field_mPM.get(packageManager);
            ShadowLog.e("MainActivity.testPackageManager(), iPackageManager=" + iPackageManager);

            Class<?> class_ContextImpl = Class.forName("android.app.ContextImpl");
            Class<?> class_IPackageManager = Class.forName("android.content.pm.IPackageManager");

            long createClassStartTime = SystemClock.elapsedRealtime();
            DynamicType.Unloaded<?> make = new ByteBuddy()
//            new ByteBuddy(ClassFileVersion.JAVA_V6)
                    .subclass(class_ApplicationPackageManager)
                    .method(ElementMatchers.namedOneOf("getInstalledPackages", "getInstalledApplications"))
//                    .method(ElementMatchers.any())
                    .intercept(InvocationHandlerAdapter.of(new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            String name = method.getName();
                            ShadowLog.e("MainActivity.testPackageManager; access " + name);
                            switch (name) {
                                case "getInstalledPackages":
                                    return null;
                                case "getInstalledApplications":
                                    return null;
                                default:
                                    return method.invoke(packageManager, args);
                            }
                        }
                    }))
                    .make();
            byte[] bytes = make.getBytes();
            ShadowLog.e("MainActivity.testPackageManager(); createClass.make consume " + (SystemClock.elapsedRealtime() - createClassStartTime) + ", bytes.length=" + bytes.length);

            Class<?> class_sub_packageManager = make.load(this.getClassLoader(), new AndroidClassLoadingStrategy.Wrapping(getCodeCacheDir()))
                    .getLoaded();
            long createClassEndTime = SystemClock.elapsedRealtime();
            ShadowLog.e("MainActivity.testPackageManager(); createClass consume " + (createClassEndTime - createClassStartTime));

            Object intercept_packageManager = class_sub_packageManager.getConstructor(class_ContextImpl, class_IPackageManager).newInstance(contextImpl, iPackageManager);
            Field field_mPackageManager = class_ContextImpl.getDeclaredField("mPackageManager");
            field_mPackageManager.setAccessible(true);
            field_mPackageManager.set(contextImpl, intercept_packageManager);

            PackageManager newPackageManager = getPackageManager();
            ShadowLog.e("MainActivity.testPackageManager(); packageManager=" + packageManager + ", newPackageManager=" + newPackageManager + ", (packageManager == newPackageManager)=" + (packageManager == newPackageManager));
            newPackageManager.getInstalledPackages(0);
            newPackageManager.queryIntentActivities(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0);
        } catch (Throwable e) {
            ShadowLog.e("MainActivity.testPackageManager() fail", e);
        }

        long endTime = SystemClock.elapsedRealtime();
        ShadowLog.e("MainActivity.testPackageManager() consume " + (endTime - startTime));
    }

    @SuppressLint("MissingPermission")
    private void testActivity() {
        ShadowLog.e("MainActivity.testActivity()=" + this + ", getBaseContext()=" + getBaseContext());
        Util.print(this);

        try {
            WifiManager wifiManager = (WifiManager) getSystemService(Service.WIFI_SERVICE);
            ShadowLog.e("MainActivity.testActivity; wifiManager=" + wifiManager);
            List<ScanResult> scanResults = wifiManager.getScanResults();
        } catch (Throwable e) {
            ShadowLog.e("fail testActivity", e);
        }

        try {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
            ShadowLog.e("MainActivity.testActivity; telephonyManager=" + telephonyManager);
            telephonyManager.getDeviceId();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                telephonyManager.requestCellInfoUpdate(Executors.newSingleThreadExecutor(), new TelephonyManager.CellInfoCallback() {
                    @Override
                    public void onCellInfo(@NonNull List<CellInfo> cellInfo) {

                    }
                });
            }
        } catch (Throwable e) {
            ShadowLog.e("fail testActivity", e);
        }

        try {
            LocationManager locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
            ShadowLog.e("MainActivity.testActivity; locationManager=" + locationManager);
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (Throwable e) {
            ShadowLog.e("fail testActivity", e);
        }

    }

    @SuppressLint("MissingPermission")
    private void testGetApplicationContext() {
        ShadowLog.e("MainActivity.testGetApplicationContext()=" + getApplicationContext());

        try {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Service.WIFI_SERVICE);
            ShadowLog.e("MainActivity.testGetApplicationContext; wifiManager=" + wifiManager);
            List<ScanResult> scanResults = wifiManager.getScanResults();
        } catch (Throwable e) {
            ShadowLog.e("fail testGetApplicationContext", e);
        }

        try {
            TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
            ShadowLog.e("MainActivity.testGetApplicationContext; telephonyManager=" + telephonyManager);
            telephonyManager.getDeviceId();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                telephonyManager.requestCellInfoUpdate(Executors.newSingleThreadExecutor(), new TelephonyManager.CellInfoCallback() {
                    @Override
                    public void onCellInfo(@NonNull List<CellInfo> cellInfo) {

                    }
                });
            }
        } catch (Throwable e) {
            ShadowLog.e("fail testGetApplicationContext", e);
        }

        try {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Service.LOCATION_SERVICE);
            ShadowLog.e("MainActivity.testGetApplicationContext; locationManager=" + locationManager);
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (Throwable e) {
            ShadowLog.e("fail testGetApplicationContext", e);
        }

    }

    @SuppressLint("MissingPermission")
    private void testShadowContext() {
        ShadowContext shadowContext = ShadowContext.shadow(getApplication());
        shadowContext.getApplicationContext();

        ShadowLog.e("MainActivity.testShadowContext; shadowContext=" + shadowContext +
                ", shadowContext.shadowApplication=" + shadowContext.shadowApplication +
                ", shadowContext.getBaseContext=" + shadowContext.getBaseContext() +
                ", getApplicationContext=" + getApplicationContext() +
                ", getApplication=" + getApplication() +
                ", getApplication.getBaseContext=" + getApplication().getBaseContext());

        try {
            WifiManager wifiManager = (WifiManager) shadowContext.getApplicationContext().getSystemService(Service.WIFI_SERVICE);
            ShadowLog.e("MainActivity.testShadowContext; wifiManager=" + wifiManager);
            List<ScanResult> scanResults = wifiManager.getScanResults();
        } catch (Throwable e) {
            ShadowLog.e("fail testShadowContext", e);
        }

        TelephonyManager telephonyManager = (TelephonyManager) shadowContext.getSystemService(Service.TELEPHONY_SERVICE);
        ShadowLog.e("MainActivity.testShadowContext; telephonyManager=" + telephonyManager);
        try {
            telephonyManager.getDeviceId();
        } catch (Throwable e) {
            ShadowLog.e("fail testShadowContext", e);
        }

        try {
            LocationManager locationManager = (LocationManager) shadowContext.getBaseContext().getSystemService(Service.LOCATION_SERVICE);
            ShadowLog.e("MainActivity.testShadowContext; locationManager=" + locationManager);
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (Throwable e) {
            ShadowLog.e("fail testShadowContext", e);
        }
    }

    @SuppressLint("MissingPermission")
    private void testShadowApplication() {
        ShadowApplication shadowApplication = ShadowApplication.shadow(getApplication());
        shadowApplication.getApplicationContext();

        ShadowLog.e("MainActivity.testShadowApplication; shadowApplication=" + shadowApplication +
                ", shadowApplication.application=" + shadowApplication.application +
                ", shadowApplication.getBaseContext=" + shadowApplication.getBaseContext() +
                ", getApplicationContext=" + getApplicationContext() +
                ", getApplication=" + getApplication() +
                ", getApplication.getBaseContext=" + getApplication().getBaseContext());

        try {
            WifiManager wifiManager = (WifiManager) shadowApplication.getApplicationContext().getSystemService(Service.WIFI_SERVICE);
            ShadowLog.e("MainActivity.testShadowApplication; wifiManager=" + wifiManager);
            List<ScanResult> scanResults = wifiManager.getScanResults();
        } catch (Throwable e) {
            ShadowLog.e("fail testShadowApplication", e);
        }

        TelephonyManager telephonyManager = (TelephonyManager) shadowApplication.getSystemService(Service.TELEPHONY_SERVICE);
        ShadowLog.e("MainActivity.testShadowApplication; telephonyManager=" + telephonyManager);
        try {
            telephonyManager.getDeviceId();
        } catch (Throwable e) {
            ShadowLog.e("fail testShadowApplication", e);
        }

        try {
            LocationManager locationManager = (LocationManager) shadowApplication.getBaseContext().getSystemService(Service.LOCATION_SERVICE);
            ShadowLog.e("MainActivity.testShadowApplication; locationManager=" + locationManager);
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (Throwable e) {
            ShadowLog.e("fail testShadowApplication", e);
        }
    }
}