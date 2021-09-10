package com.coofee.shadowapp.shadow;

import android.app.Service;
import android.shadow.ShadowLog;
import android.shadow.ShadowServiceInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TelephonyManagerInterceptor implements ShadowServiceInterceptor {

    private final Set<String> mInterceptMethodNames = new HashSet<>(Arrays.asList(

    ));

    //    @SuppressLint("MissingPermission")
//    @Override
//    public String getSubscriberId() {
//        ShadowLog.e("access getSubscriberId", new Throwable());
//        return ShadowServiceManager.config().telephonyManagerProvider().getSubscriberId();
//    }
//
//    @SuppressLint("MissingPermission")
//    @Override
//    public String getMeid() {
//        ShadowLog.e("access getMeid", new Throwable());
//        return ShadowServiceManager.config().telephonyManagerProvider().getMeid();
//    }
//
//    @SuppressLint("MissingPermission")
//    @Override
//    public String getMeid(int slotIndex) {
//        ShadowLog.e("access getMeid", new Throwable());
//        return ShadowServiceManager.config().telephonyManagerProvider().getMeid(slotIndex);
//    }
//
//    @SuppressLint({"HardwareIds", "MissingPermission"})
//    @Override
//    public String getDeviceId() {
//        ShadowLog.e("access getDeviceId", new Throwable());
//        return ShadowServiceManager.config().telephonyManagerProvider().getDeviceId();
//    }
//
//    @SuppressLint({"MissingPermission", "HardwareIds"})
//    @Override
//    public String getDeviceId(int slotIndex) {
//        ShadowLog.e("access getDeviceId", new Throwable());
//        return ShadowServiceManager.config().telephonyManagerProvider().getDeviceId(slotIndex);
//    }
//
//    @SuppressLint("MissingPermission")
//    @Override
//    public String getImei() {
//        ShadowLog.e("access getImei", new Throwable());
//        return ShadowServiceManager.config().telephonyManagerProvider().getImei();
//    }
//
//    @SuppressLint("MissingPermission")
//    @Override
//    public String getImei(int slotIndex) {
//        ShadowLog.e("access getImei", new Throwable());
//        return ShadowServiceManager.config().telephonyManagerProvider().getImei(slotIndex);
//    }
//
//    @SuppressLint("MissingPermission")
//    @Override
//    public String getSimSerialNumber() {
//        ShadowLog.e("access getSimSerialNumber", new Throwable());
//        return ShadowServiceManager.config().telephonyManagerProvider().getSimSerialNumber();
//    }
//
//    @SuppressLint("MissingPermission")
//    @Override
//    public String getSimSerialNumber(int slotIndex) {
//        ShadowLog.e("access getSimSerialNumber", new Throwable());
//        return ShadowServiceManager.config().telephonyManagerProvider().getSimSerialNumber(slotIndex);
//    }
//
//    @SuppressLint("MissingPermission")
//    @Override
//    public List<CellInfo> getAllCellInfo() {
//        ShadowLog.e("access getAllCellInfo", new Throwable());
//        return ShadowServiceManager.config().telephonyManagerProvider().getAllCellInfo();
//    }
//
//    @SuppressLint("MissingPermission")
//    @Override
//    public CellLocation getCellLocation() {
//        ShadowLog.e("access getCellLocation", new Throwable());
//        return ShadowServiceManager.config().telephonyManagerProvider().getCellLocation();
//    }
//
//    @SuppressLint({"MissingPermission", "HardwareIds"})
//    @Override
//    public String getLine1Number() {
//        ShadowLog.e("access getLine1Number", new Throwable());
//        return ShadowServiceManager.config().telephonyManagerProvider().getLine1Number();
//    }
//
//    @SuppressLint("MissingPermission")
//    @Override
//    public String getGroupIdLevel1() {
//        ShadowLog.e("access getGroupIdLevel1", new Throwable());
//        return ShadowServiceManager.config().telephonyManagerProvider().getGroupIdLevel1();
//    }
//
//    @SuppressLint("MissingPermission")
//    @NonNull
//    @Override
//    public Map<Integer, List<EmergencyNumber>> getEmergencyNumberList() {
//        ShadowLog.e("access getEmergencyNumberList", new Throwable());
//        return ShadowServiceManager.config().telephonyManagerProvider().getEmergencyNumberList();
//    }
//
//    @SuppressLint("MissingPermission")
//    @Override
//    public void requestCellInfoUpdate(@NonNull Executor executor, @NonNull CellInfoCallback callback) {
//        ShadowLog.e("access requestCellInfoUpdate", new Throwable());
//        ShadowServiceManager.config().telephonyManagerProvider().requestCellInfoUpdate(executor, callback);
//    }
//
//    @SuppressLint("MissingPermission")
////    @Override
//    public List<NeighboringCellInfo> getNeighboringCellInfo() {
//        ShadowLog.e("access getNeighboringCellInfo", new Throwable());
//        return ShadowServiceManager.config().telephonyManagerProvider().getNeighboringCellInfo();
//    }
//
//    @SuppressLint("MissingPermission")
//    @Override
//    public ServiceState getServiceState() {
//        ShadowLog.e("access getServiceState", new Throwable());
//        return ShadowServiceManager.config().telephonyManagerProvider().getServiceState();
//    }
//
    @Override
    public Object invoke(String serviceName, Object service, Method method, Object[] args) throws Throwable {
        ShadowLog.e("TelephonyManagerInterceptor intercept method=" + method.getName());
        return null;
    }

    @Override
    public boolean interceptAllMethod() {
        return true;
    }

    @Override
    public String provideInterceptServiceName() {
        return Service.TELEPHONY_SERVICE;
    }

    @Override
    public Set<String> provideInterceptMethodNames() {
        return mInterceptMethodNames;
    }
}
