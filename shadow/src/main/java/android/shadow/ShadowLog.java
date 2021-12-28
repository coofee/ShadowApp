package android.shadow;

import androidx.annotation.IntDef;

public class ShadowLog {

    public static final int NO = 0;
    public static final int ERROR = 1;
    public static final int DEBUG = 2;
    public static final int VERBOSE = 3;

    @IntDef(value = {NO, ERROR, DEBUG, VERBOSE})
    public static @interface LogMode {

    }

    @LogMode
    private static int sLogMode = DEBUG;

    public interface ILog {
        void v(String tag, String msg);

        void v(String tag, String msg, Throwable e);

        void d(String tag, String msg);

        void d(String tag, String msg, Throwable e);

        void e(String tag, String msg, Throwable e);

        void e(String tag, String msg);
    }

    public static class AndroidLog implements ILog {

        @Override
        public void v(String tag, String msg) {
            android.util.Log.v(tag, msg);
        }

        @Override
        public void v(String tag, String msg, Throwable e) {
            android.util.Log.v(tag, msg, e);
        }

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
    }

    private static ILog sLogImpl = new AndroidLog();

    static void setLogImpl(ILog logImpl) {
        if (logImpl != null) {
            sLogImpl = logImpl;
        }
    }

    static void setLogMode(@LogMode int logMode) {
        if (logMode > ShadowLog.VERBOSE || logMode < ShadowLog.NO) {
            return;
        }

        sLogMode = logMode;
    }

    @LogMode
    public static int logMode() {
        return sLogMode;
    }

    public static void v(String msg) {
        if (sLogMode >= VERBOSE) {
            sLogImpl.d(ShadowServiceManager.TAG, msg);
        }
    }

    public static void v(String msg, Throwable e) {
        if (sLogMode >= VERBOSE) {
            sLogImpl.d(ShadowServiceManager.TAG, msg, e);
        }
    }

    public static void d(String msg) {
        if (sLogMode >= DEBUG) {
            sLogImpl.d(ShadowServiceManager.TAG, msg);
        }
    }

    public static void d(String msg, Throwable e) {
        if (sLogMode >= DEBUG) {
            sLogImpl.d(ShadowServiceManager.TAG, msg, e);
        }
    }

    public static void e(String msg, Throwable e) {
        if (sLogMode >= ERROR) {
            sLogImpl.e(ShadowServiceManager.TAG, msg, e);
        }
    }

    public static void e(String msg) {
        if (sLogMode >= ERROR) {
            sLogImpl.e(ShadowServiceManager.TAG, msg);
        }
    }

    public static void logThrow(String msg) {
        if (sLogMode >= ERROR) {
            sLogImpl.e(ShadowServiceManager.TAG, msg);
            throw new RuntimeException(msg);
        }
    }
}
