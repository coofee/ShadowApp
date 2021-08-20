package android.telephony;

import android.annotation.SuppressLint;
import android.content.Context;
import android.shadow.ShadowLog;
import android.shadow.ShadowServiceManager;
import android.telephony.emergency.EmergencyNumber;
import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;

public class ShadowTelephonyManager extends TelephonyManager {

    public ShadowTelephonyManager(Context context) {
        super(context);
    }

    @SuppressLint("MissingPermission")
    @Override
    public String getSubscriberId() {
        ShadowLog.e("access getSubscriberId", new Throwable());
        return ShadowServiceManager.config().telephonyManagerProvider().getSubscriberId();
    }

    @SuppressLint("MissingPermission")
    @Override
    public String getMeid() {
        ShadowLog.e("access getMeid", new Throwable());
        return ShadowServiceManager.config().telephonyManagerProvider().getMeid();
    }

    @SuppressLint("MissingPermission")
    @Override
    public String getMeid(int slotIndex) {
        ShadowLog.e("access getMeid", new Throwable());
        return ShadowServiceManager.config().telephonyManagerProvider().getMeid(slotIndex);
    }

    @SuppressLint({"HardwareIds", "MissingPermission"})
    @Override
    public String getDeviceId() {
        ShadowLog.e("access getDeviceId", new Throwable());
        return ShadowServiceManager.config().telephonyManagerProvider().getDeviceId();
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    @Override
    public String getDeviceId(int slotIndex) {
        ShadowLog.e("access getDeviceId", new Throwable());
        return ShadowServiceManager.config().telephonyManagerProvider().getDeviceId(slotIndex);
    }

    @SuppressLint("MissingPermission")
    @Override
    public String getImei() {
        ShadowLog.e("access getImei", new Throwable());
        return ShadowServiceManager.config().telephonyManagerProvider().getImei();
    }

    @SuppressLint("MissingPermission")
    @Override
    public String getImei(int slotIndex) {
        ShadowLog.e("access getImei", new Throwable());
        return ShadowServiceManager.config().telephonyManagerProvider().getImei(slotIndex);
    }

    @SuppressLint("MissingPermission")
    @Override
    public String getSimSerialNumber() {
        ShadowLog.e("access getSimSerialNumber", new Throwable());
        return ShadowServiceManager.config().telephonyManagerProvider().getSimSerialNumber();
    }

    @SuppressLint("MissingPermission")
    @Override
    public String getSimSerialNumber(int slotIndex) {
        ShadowLog.e("access getSimSerialNumber", new Throwable());
        return ShadowServiceManager.config().telephonyManagerProvider().getSimSerialNumber(slotIndex);
    }

    @SuppressLint("MissingPermission")
    @Override
    public List<CellInfo> getAllCellInfo() {
        ShadowLog.e("access getAllCellInfo", new Throwable());
        return ShadowServiceManager.config().telephonyManagerProvider().getAllCellInfo();
    }

    @SuppressLint("MissingPermission")
    @Override
    public CellLocation getCellLocation() {
        ShadowLog.e("access getCellLocation", new Throwable());
        return ShadowServiceManager.config().telephonyManagerProvider().getCellLocation();
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    @Override
    public String getLine1Number() {
        ShadowLog.e("access getLine1Number", new Throwable());
        return ShadowServiceManager.config().telephonyManagerProvider().getLine1Number();
    }

    @SuppressLint("MissingPermission")
    @Override
    public String getGroupIdLevel1() {
        ShadowLog.e("access getGroupIdLevel1", new Throwable());
        return ShadowServiceManager.config().telephonyManagerProvider().getGroupIdLevel1();
    }

    @SuppressLint("MissingPermission")
    @NonNull
    @Override
    public Map<Integer, List<EmergencyNumber>> getEmergencyNumberList() {
        ShadowLog.e("access getEmergencyNumberList", new Throwable());
        return ShadowServiceManager.config().telephonyManagerProvider().getEmergencyNumberList();
    }
}
