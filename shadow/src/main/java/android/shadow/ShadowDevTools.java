package android.shadow;

import java.util.Arrays;

public interface ShadowDevTools {
    ShadowDevTools DEFAULT_DEV_TOOLS = new ShadowDevTools() {

        @Override
        public void onInvoke(String serviceName, Object service, String method, Object[] args) {
            ShadowLog.d("ShadowDevTools; serviceName=" + serviceName + ", service=" + service + ", method=" + method + ", args=" + Arrays.toString(args));
        }
    };

    void onInvoke(String serviceName, Object service, String method, Object[] args);
}
