package android.shadow;

public class ShadowLog {

    public static boolean debug = false;

    public interface ILog {
        void d(String tag, String msg);

        void d(String tag, String msg, Throwable e);

        void e(String tag, String msg, Throwable e);

        void e(String tag, String msg);
    }

    private static ILog sLogImpl = new ILog() {
        @Override
        public void d(String tag, String msg) {
            android.util.Log.d(tag, msg);
        }

        @Override
        public void d(String tag, String msg, Throwable e) {
            android.util.Log.d(tag, msg, e);
        }

        @Override
        public void e(String tag, String msg, Throwable e) {
            android.util.Log.e(tag, msg, e);
        }

        @Override
        public void e(String tag, String msg) {
            android.util.Log.e(tag, msg);
        }
    };

    public static void setLogImpl(ILog logImpl) {
        if (logImpl != null) {
            sLogImpl = logImpl;
        }
    }

    public static void d(String msg) {
        if (debug || ShadowServiceManager.debug()) {
            sLogImpl.d(ShadowServiceManager.TAG, msg);
        }
    }

    public static void d(String msg, Throwable e) {
        if (debug || ShadowServiceManager.debug()) {
            sLogImpl.d(ShadowServiceManager.TAG, msg, e);
        }
    }

    public static void e(String msg, Throwable e) {
        if (debug || ShadowServiceManager.debug()) {
            sLogImpl.e(ShadowServiceManager.TAG, msg, e);
        }
    }

    public static void e(String msg) {
        if (debug || ShadowServiceManager.debug()) {
            sLogImpl.e(ShadowServiceManager.TAG, msg);
        }
    }

    public static void logThrow(String msg) {
        if (debug || ShadowServiceManager.debug()) {
            sLogImpl.e(ShadowServiceManager.TAG, msg);
            throw new RuntimeException(msg);
        }
    }
}
