package android.shadow;

public class ShadowLog {

    public interface ILog {
        void e(String tag, String msg, Throwable e);

        void e(String tag, String msg);
    }

    private static ILog sLogImpl = new ILog() {
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

    public static void e(String msg, Throwable e) {
        if (ShadowServiceManager.debug()) {
            sLogImpl.e(ShadowServiceManager.TAG, msg, e);
        }
    }

    public static void e(String msg) {
        if (ShadowServiceManager.debug()) {
            sLogImpl.e(ShadowServiceManager.TAG, msg);
        }
    }
}
