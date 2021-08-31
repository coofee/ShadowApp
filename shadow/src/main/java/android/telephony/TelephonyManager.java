package android.telephony;

import android.content.Context;
import android.telephony.emergency.EmergencyNumber;
import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class TelephonyManager {
    public TelephonyManager(Context context) {

    }

    public String getSubscriberId() {
        throw new RuntimeException("Stub.");
    }

    public String getMeid() {
        throw new RuntimeException("Stub.");
    }

    public String getMeid(int slotIndex) {
        throw new RuntimeException("Stub.");

    }

    public String getDeviceId() {
        throw new RuntimeException("Stub.");

    }

    public String getDeviceId(int slotIndex) {
        throw new RuntimeException("Stub.");

    }

    public String getImei() {
        throw new RuntimeException("Stub.");

    }

    public String getImei(int slotIndex) {
        throw new RuntimeException("Stub.");

    }

    public String getSimSerialNumber() {
        throw new RuntimeException("Stub.");
    }

    public String getSimSerialNumber(int slotIndex) {
        throw new RuntimeException("Stub.");
    }

    public List<CellInfo> getAllCellInfo() {
        throw new RuntimeException("Stub.");

    }

    public CellLocation getCellLocation() {
        throw new RuntimeException("Stub.");

    }

    public String getLine1Number() {
        throw new RuntimeException("Stub.");
    }

    public String getGroupIdLevel1() {
        throw new RuntimeException("Stub.");

    }

    public Map<Integer, List<EmergencyNumber>> getEmergencyNumberList() {
        throw new RuntimeException("Stub.");
    }

    public void requestCellInfoUpdate(@NonNull Executor executor, @NonNull CellInfoCallback callback) {
        throw new RuntimeException("Stub.");
    }

    public List<NeighboringCellInfo> getNeighboringCellInfo() {
        throw new RuntimeException("Stub.");
    }

    public ServiceState getServiceState() {
        throw new RuntimeException("Stub.");

    }

    public abstract static class CellInfoCallback {

    }
}
