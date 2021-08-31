package android.net.wifi;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.List;

public class WifiManager {
    IWifiManager mService;
    Looper mLooper;

    public WifiManager(Context context, IWifiManager service, Looper looper) {

    }

    public List<WifiConfiguration> getConfiguredNetworks() {
        throw new RuntimeException("Stub.");
    }

    public List<ScanResult> getScanResults() {
        throw new RuntimeException("Stub.");
    }

    public void startLocalOnlyHotspot(WifiManager.LocalOnlyHotspotCallback callback, Handler handler) {
        throw new RuntimeException("Stub.");
    }

    public static class LocalOnlyHotspotCallback {

        public void onStarted(LocalOnlyHotspotReservation reservation) {
        }

        public void onStopped() {
        }

        public void onFailed(int reason) {
        }

    }

    public class LocalOnlyHotspotReservation implements AutoCloseable {

        private final WifiConfiguration mConfig;

        public LocalOnlyHotspotReservation(WifiConfiguration config) {
            this.mConfig = config;
        }

        public WifiConfiguration getWifiConfiguration() {
            return null;
        }

        @Override
        public void close() {

        }

        @Override
        protected void finalize() throws Throwable {

        }
    }

}
