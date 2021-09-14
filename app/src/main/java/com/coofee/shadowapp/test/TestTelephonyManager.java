package com.coofee.shadowapp.test;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.shadow.ShadowServiceManager;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.annotation.NonNull;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;

public class TestTelephonyManager {

    @SuppressLint({"NewApi", "HardwareIds", "MissingPermission", "DiscouragedPrivateApi"})
    public static void test(Context context) {
        Log.d(ShadowServiceManager.TAG, "TestTelephonyManager.test");

        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);

        // com.android.internal.telephony.ITelephony
        TestUtil.exec(() -> telephonyManager.getDeviceId()); // getDeviceId

        TestUtil.exec(() -> telephonyManager.getImei()); // getImei
        TestUtil.exec(() -> telephonyManager.getImei(0)); // getImeiForSlot

        TestUtil.exec(() -> telephonyManager.getMeid()); // getMeid
        TestUtil.exec(() -> telephonyManager.getMeid(0)); // getMeidForSlot

        TestUtil.exec(() -> telephonyManager.getCellLocation()); // getCellLocation

        TestUtil.exec(() -> telephonyManager.getAllCellInfo()); // getAllCellInfo

        TestUtil.exec(() -> telephonyManager.getLine1Number()); // getLine1NumberForDisplay

        TestUtil.exec(() -> telephonyManager.getEmergencyNumberList()); // getEmergencyNumberList

        // com.android.internal.telephony.IPhoneSubInfo
        TestUtil.exec(() -> telephonyManager.getDeviceId(0)); // getDeviceIdForPhone

        TestUtil.exec(() -> telephonyManager.getSubscriberId()); // getSubscriberIdForSubscriber

        TestUtil.exec(() -> telephonyManager.getSimSerialNumber()); // getIccSerialNumberForSubscriber

        TestUtil.exec(() -> telephonyManager.getLine1Number()); // getLine1NumberForSubscriber

        TestUtil.exec(() -> telephonyManager.getGroupIdLevel1()); // getGroupIdLevel1ForSubscriber


        TestUtil.exec(new Callable<Object>() {
            @Override
            public Object call() throws Exception {

                telephonyManager.requestCellInfoUpdate(command -> {
                }, new TelephonyManager.CellInfoCallback() {
                    @Override
                    public void onCellInfo(@NonNull List<CellInfo> cellInfo) {

                    }
                });

                return null;
            }
        });

        TestUtil.exec(() -> {
            Method getNeighboringCellInfo = TelephonyManager.class.getDeclaredMethod("getNeighboringCellInfo");
            getNeighboringCellInfo.setAccessible(true);
            return getNeighboringCellInfo.invoke(telephonyManager);
        });

        TestUtil.exec(() -> telephonyManager.getServiceState());

    }


}
