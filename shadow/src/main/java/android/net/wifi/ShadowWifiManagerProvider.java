package android.net.wifi;

import android.content.Context;
import android.os.Handler;

import java.util.List;

public interface ShadowWifiManagerProvider {
    List<WifiConfiguration> getConfiguredNetworks(Context context);

    List<ScanResult> getScanResults(Context context);

    void startLocalOnlyHotspot(Context context, WifiManager.LocalOnlyHotspotCallback callback, Handler handler);

    public static class Adapter implements ShadowWifiManagerProvider {

        @Override
        public List<WifiConfiguration> getConfiguredNetworks(Context context) {
            return null;
        }

        @Override
        public List<ScanResult> getScanResults(Context context) {
            return null;
        }

        @Override
        public void startLocalOnlyHotspot(Context context, WifiManager.LocalOnlyHotspotCallback callback, Handler handler) {

        }
    }

    public static class Wrapper {
        private final Context baseContext;

        private final ShadowWifiManagerProvider provider;

        public Wrapper(Context baseContext, ShadowWifiManagerProvider provider) {
            this.baseContext = baseContext;
            this.provider = provider;
        }

        public List<WifiConfiguration> getConfiguredNetworks() {
            return this.provider.getConfiguredNetworks(baseContext);
        }

        public List<ScanResult> getScanResults() {
            return this.provider.getScanResults(baseContext);
        }

        public void startLocalOnlyHotspot(WifiManager.LocalOnlyHotspotCallback callback, Handler handler) {
            this.provider.startLocalOnlyHotspot(baseContext, callback, handler);
        }
    }
}
