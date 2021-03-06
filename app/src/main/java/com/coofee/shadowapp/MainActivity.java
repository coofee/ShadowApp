package com.coofee.shadowapp;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.SystemClock;
import android.os.Trace;
import android.shadow.ShadowLog;
import android.shadow.ShadowServiceInterceptor;
import android.shadow.ShadowServiceInvocationHandler;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.coofee.shadowapp.test.BinderProvider;
import com.coofee.shadowapp.test.RuntimeUtil;
import com.coofee.shadowapp.test.TestLocationManager;
import com.coofee.shadowapp.test.TestTelephonyManager;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.android.AndroidClassLoadingStrategy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        android.os.Debug.waitForDebugger();
        setContentView(R.layout.activity_main);

        try {
            Method setAppTracingAllowed = Trace.class.getDeclaredMethod("setAppTracingAllowed", boolean.class);
            setAppTracingAllowed.setAccessible(true);
            setAppTracingAllowed.invoke(null, true);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        Trace.beginSection("MainActivity.onCreate");

        testClassLoader();

        testActivity();

        testGetApplicationContext();

        TestTelephonyManager.test(this);

        TestLocationManager.test(this);

        testObjectMethod();

//        testShadowContext();

//        testShadowApplication();

//        testPackageManager();

        findViewById(R.id.test_get_device_id).setOnClickListener(v -> {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
                ShadowLog.e("MainActivity.test_get_device_id; getDeviceId=" + telephonyManager.getDeviceId());
            } catch (Throwable e) {
                ShadowLog.e("fail test_get_device_id.getDeviceId", e);
            }
        });

        findViewById(R.id.test_start_service).setOnClickListener(v -> {
            ShadowLog.d("click start service...");
            try {
                TestIntentService.startActionBaz(MainActivity.this, "", "");
            } catch (Throwable e) {
                ShadowLog.e("click start service", e);
            }
        });

        findViewById(R.id.test_bind_service).setOnClickListener(v -> {
            ShadowLog.d("click bind service...");

            try {
                bindService(new Intent(MainActivity.this, TestBindService.class), new ServiceConnection() {

                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {

                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {

                    }
                }, BIND_AUTO_CREATE);
            } catch (Throwable e) {
                ShadowLog.e("click bind service", e);
            }
        });

        findViewById(R.id.test_send_broadcast_receiver).setOnClickListener(v -> {
            ShadowLog.d("click send broadcast receiver");
            try {
                BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                    }
                };
                IntentFilter intentFilter = new IntentFilter(TestReceiver.ACTION);
                registerReceiver(broadcastReceiver, intentFilter);

                Intent intent = new Intent(MainActivity.this, TestReceiver.class);
                intent.setAction(TestReceiver.ACTION);
                sendBroadcast(intent);
            } catch (Throwable e) {
                ShadowLog.e("click send broadcast receiver", e);
            }
        });

        findViewById(R.id.test_get_content_provider).setOnClickListener(v -> {
            ShadowLog.d("click get content provider");
            try {
                BinderProvider.getService(MainActivity.this, "test_service");
            } catch (Throwable e) {
                ShadowLog.e("click get content provider", e);
            }
        });


        findViewById(R.id.test_start_activity).setOnClickListener(v -> {
            startActivity(new Intent(this, TestActivity.class));
        });

        findViewById(R.id.test_start_activity_for_result).setOnClickListener(v -> {
            startActivityForResult(new Intent(this, TestActivity.class), 20);
        });

        findViewById(R.id.test_start_activities).setOnClickListener(v -> {
            startActivities(new Intent[]{new Intent(this, TestActivity.class), new Intent(this, TestActivity.class)});
        });

        findViewById(R.id.test_start_next_matching_activity).setOnClickListener(v -> {
            startNextMatchingActivity(new Intent(this, TestActivity.class));
        });

        findViewById(R.id.test_start_activity_if_needed).setOnClickListener(v -> {
            startActivityIfNeeded(new Intent(this, TestActivity.class), 20);
        });


        findViewById(R.id.replace_package_manager).setOnClickListener(view -> {
            testPackageManager();

            String classFullName = MainActivity.class.getName();
            String classPath = classFullName.replace('.', '/') + ".class";
            InputStream resourceAsStream = MainActivity.this.getClassLoader().getResourceAsStream(classPath);
            try {
                File classFile = new File(getExternalCacheDir(), classFullName);
                boolean copy = Util.copy(resourceAsStream, new FileOutputStream(classFile));
                ShadowLog.e("copy to " + classFile + " success? " + copy);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            TestLocationManager.test(this);

//            Object providerProperties = ShadowServiceManager.getShadowService(Service.LOCATION_SERVICE)
//                    .method("getLast", String.class)
//                    .invoke(LocationManager.GPS_PROVIDER);
//            ShadowLog.e("get locationProvider=" + providerProperties + " by shadow service.");

//            try {
//                String s = UUID.randomUUID().toString().replace("-", "");
//                File file = new File("/data/local/tmp/", s);
//                exec("touch " + file.getAbsolutePath());
//            } catch (Throwable e) {
//                ShadowLog.e("touch file fail", e);
//            }
            testActivity();

            startActivity(new Intent(MainActivity.this, TestActivity.class));


            new Thread(new Runnable() {
                @Override
                public void run() {
                    Set<String> classBy = Util.findClassBy(getPackageCodePath(), className -> className.startsWith("com.coofee.shadowapp.shadow"));
                    ShadowLog.e("findClassBy; find all class=" + classBy);

//                    OsUtil.replaceOsByInvocationHandler();
                    getSharedPreferences("my_sp", MODE_PRIVATE).edit()
                            .putLong("last_open_time", System.currentTimeMillis())
                            .commit();
                }
            }).start();
        });

        findViewById(R.id.replace_os).setOnClickListener(v -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
//                    OsUtil.replaceOsByInvocationHandler();
                    getMacAddressFromIp();

                    long currentTime = System.currentTimeMillis();
                    getSharedPreferences("my_sp", MODE_PRIVATE).edit()
                            .putLong("last_open_time", currentTime)
                            .commit();

                    long aLong = getSharedPreferences("my_sp", MODE_PRIVATE).getLong("last_open_time", 0L);
                    assert aLong == currentTime;
                }
            }).start();
        });

        findViewById(R.id.test_TransactionTooLargeException).setOnClickListener(v -> {
            IBinder test_service = BinderProvider.getService(MainActivity.this, "test_service");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this, TestActivity.class);
                    intent.putExtra("bytes_too_large", new byte[1024 * 1024]);
                    Parcel parcel = Parcel.obtain();
                    parcel.writeParcelable(intent, 0);
                    ShadowLog.e("dataSize=" + parcel.dataSize());
                    parcel.recycle();
                    startActivity(intent);
                }
            }, 2000);
        });


        findViewById(R.id.test_replace_Runtime).setOnClickListener(v -> {
            RuntimeUtil.demo();
        });

        findViewById(R.id.test_TelephoneManager).setOnClickListener(v -> {
            TestTelephonyManager.test(this);
        });

        findViewById(R.id.test_replace_Script).setOnClickListener(v -> {
//            ShadowStatsManager.getInstance().updateScript(ScriptUtil.SCRIPT);
        });

        Trace.endSection();
    }

    private void testObjectMethod() {
        final ShadowServiceInterceptor testInterceptor = new ShadowServiceInterceptor() {

            @Override
            public Object invoke(String serviceName, Object service, Method method, Object[] args) throws Throwable {
                return null;
            }

            @Override
            public String provideInterceptServiceName() {
                return "testInterceptor";
            }

            @Override
            public Set<String> provideInterceptMethodNames() {
                return new HashSet<>(Arrays.asList("testInterceptor"));
            }

            @Override
            public boolean interceptAllMethod() {
                return true;
            }

            @NonNull
            @Override
            public String toString() {
                return "testInterceptor";
            }

            @Override
            public int hashCode() {
                return 90;
            }
        };

        ShadowServiceInterceptor interceptorProxy = (ShadowServiceInterceptor) Proxy.newProxyInstance(this.getClassLoader(), new Class[]{ShadowServiceInterceptor.class}, new ShadowServiceInvocationHandler("testInterceptor", testInterceptor));
        interceptorProxy.toString();
        interceptorProxy.hashCode();
        interceptorProxy.interceptAllMethod();
        interceptorProxy.provideInterceptMethodNames();
        interceptorProxy.provideInterceptServiceName();
    }

    private void exec(String command) {
        BufferedReader out = null;
        BufferedReader err = null;
        try {
            String[] commandArray = new String[]{"sh", "-c", command};
            ShadowLog.e("try exec command=" + Arrays.toString(commandArray));
            Process process = Runtime.getRuntime().exec(commandArray);
            out = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = out.readLine()) != null) {
                ShadowLog.e(line);
            }

            err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = err.readLine()) != null) {
                ShadowLog.e(line);
            }
        } catch (Throwable e) {
            ShadowLog.e("fail exec command=" + command, e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }

            if (err != null) {
                try {
                    err.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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

        List<PackageInfo> installedPackages = getPackageManager().getInstalledPackages(0);
        List<ApplicationInfo> installedApplications = getPackageManager().getInstalledApplications(0);
        ShadowLog.e("MainActivity.testPackageManager(); installedPackages=" + installedPackages + ", installedApplications=" + installedApplications);

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
                            try {
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
                            } catch (InvocationTargetException e) {
                                throw e.getCause();
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
            ShadowLog.e("fail testActivity.getScanResults", e);
        }

        try {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
            ShadowLog.e("MainActivity.testActivity; getDeviceId=" + telephonyManager.getDeviceId());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                telephonyManager.requestCellInfoUpdate(Executors.newSingleThreadExecutor(), new TelephonyManager.CellInfoCallback() {
                    @Override
                    public void onCellInfo(@NonNull List<CellInfo> cellInfo) {

                    }
                });
            }
        } catch (Throwable e) {
            ShadowLog.e("fail testActivity.getDeviceId", e);
        }

        try {
            TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
            ShadowLog.e("MainActivity.testGetApplicationContext; getSubscriberId=" + telephonyManager.getSubscriberId());
        } catch (Throwable e) {
            ShadowLog.e("fail testGetApplicationContext.getSubscriberId", e);
        }

        try {
            LocationManager locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
            ShadowLog.e("MainActivity.testActivity; locationManager=" + locationManager);
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (Throwable e) {
            ShadowLog.e("fail testActivity.getLastKnownLocation", e);
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
            ShadowLog.e("fail testGetApplicationContext.getScanResults", e);
        }

        try {
            TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
            ShadowLog.e("MainActivity.testGetApplicationContext; getDeviceId=" + telephonyManager.getDeviceId());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                telephonyManager.requestCellInfoUpdate(Executors.newSingleThreadExecutor(), new TelephonyManager.CellInfoCallback() {
                    @Override
                    public void onCellInfo(@NonNull List<CellInfo> cellInfo) {

                    }
                });
            }
        } catch (Throwable e) {
            ShadowLog.e("fail testGetApplicationContext.getDeviceId", e);
        }

        try {
            TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
            ShadowLog.e("MainActivity.testGetApplicationContext; getSubscriberId=" + telephonyManager.getSubscriberId());
        } catch (Throwable e) {
            ShadowLog.e("fail testGetApplicationContext.getSubscriberId", e);
        }

        try {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Service.LOCATION_SERVICE);
            ShadowLog.e("MainActivity.testGetApplicationContext; locationManager=" + locationManager);
            ShadowLog.e("MainActivity.testGetApplicationContext; lastKnownLocation=" + locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        } catch (Throwable e) {
            ShadowLog.e("fail testGetApplicationContext.getLastKnownLocation", e);
        }

    }

    private static void getMacAddressFromIp() {
        String mac_s = "";
        StringBuilder buf = new StringBuilder();
        try {
            byte[] mac;
            String ip = getIpAddress();
            NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress.getByName(ip));
            mac = ne.getHardwareAddress();
            for (byte b : mac) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            mac_s = buf.toString();
            ShadowLog.e("getMacAddressFromIp is new???" + ip + "#" + mac_s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return "";
    }
}