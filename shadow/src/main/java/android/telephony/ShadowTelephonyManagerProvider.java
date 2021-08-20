package android.telephony;

import android.content.Context;
import android.telephony.emergency.EmergencyNumber;

import java.util.List;
import java.util.Map;

public interface ShadowTelephonyManagerProvider {

    String getSubscriberId(Context context);

    String getMeid(Context context);

    String getMeid(Context context, int slotIndex);

    String getDeviceId(Context context);

    String getDeviceId(Context context, int slotIndex);

    String getImei(Context context);

    String getImei(Context context, int slotIndex);

    String getSimSerialNumber(Context context);

    String getSimSerialNumber(Context context, int slotIndex);

    List<CellInfo> getAllCellInfo(Context context);

    CellLocation getCellLocation(Context context);

    String getLine1Number(Context context);

    String getGroupIdLevel1(Context context);

    Map<Integer, List<EmergencyNumber>> getEmergencyNumberList(Context context);

    public static class Adapter implements ShadowTelephonyManagerProvider {

        @Override
        public String getSubscriberId(Context context) {
            return null;
        }

        @Override
        public String getMeid(Context context) {
            return null;
        }

        @Override
        public String getMeid(Context context, int slotIndex) {
            return null;
        }

        @Override
        public String getDeviceId(Context context) {
            return null;
        }

        @Override
        public String getDeviceId(Context context, int slotIndex) {
            return null;
        }

        @Override
        public String getImei(Context context) {
            return null;
        }

        @Override
        public String getImei(Context context, int slotIndex) {
            return null;
        }

        @Override
        public String getSimSerialNumber(Context context) {
            return null;
        }

        @Override
        public String getSimSerialNumber(Context context, int slotIndex) {
            return null;
        }

        @Override
        public List<CellInfo> getAllCellInfo(Context context) {
            return null;
        }

        @Override
        public CellLocation getCellLocation(Context context) {
            return null;
        }

        @Override
        public String getLine1Number(Context context) {
            return null;
        }

        @Override
        public String getGroupIdLevel1(Context context) {
            return null;
        }

        @Override
        public Map<Integer, List<EmergencyNumber>> getEmergencyNumberList(Context context) {
            return null;
        }
    }

    public static class Wrapper {

        private final Context baseContext;

        private final ShadowTelephonyManagerProvider provider;

        public Wrapper(Context baseContext, ShadowTelephonyManagerProvider provider) {
            this.baseContext = baseContext;
            this.provider = provider;
        }

        public String getSubscriberId() {
            return this.provider.getSubscriberId(baseContext);
        }

        public String getMeid() {
            return this.provider.getMeid(baseContext);
        }

        public String getMeid(int slotIndex) {
            return this.provider.getMeid(baseContext, slotIndex);
        }

        public String getDeviceId() {
            return this.provider.getDeviceId(baseContext);
        }

        public String getDeviceId(int slotIndex) {
            return this.provider.getDeviceId(baseContext, slotIndex);
        }

        public String getImei() {
            return this.provider.getImei(baseContext);
        }

        public String getImei(int slotIndex) {
            return this.provider.getImei(baseContext, slotIndex);
        }

        public String getSimSerialNumber() {
            return this.provider.getSimSerialNumber(baseContext);
        }

        public String getSimSerialNumber(int slotIndex) {
            return this.provider.getSimSerialNumber(baseContext, slotIndex);
        }

        public List<CellInfo> getAllCellInfo() {
            return this.provider.getAllCellInfo(baseContext);
        }

        public CellLocation getCellLocation() {
            return this.provider.getCellLocation(baseContext);
        }

        public String getLine1Number() {
            return this.provider.getLine1Number(baseContext);
        }

        public String getGroupIdLevel1() {
            return this.provider.getGroupIdLevel1(baseContext);
        }

        public Map<Integer, List<EmergencyNumber>> getEmergencyNumberList() {
            return this.provider.getEmergencyNumberList(baseContext);
        }
    }

}
