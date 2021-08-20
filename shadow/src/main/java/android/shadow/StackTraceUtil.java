package android.shadow;

import java.util.Collection;

public class StackTraceUtil {

    public static boolean invokeBy(String... packageOrClassName) {
        if (packageOrClassName == null) {
            return false;
        }

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace != null && stackTrace.length > 0) {
            for (StackTraceElement traceElement : stackTrace) {

                for (String prefix : packageOrClassName) {
                    if (traceElement.getClassName().startsWith(prefix)) {
                        return true;
                    }
                }

            }
        }

        return false;
    }

    public static boolean invokeBy(Collection<String> packageOrClassName) {
        if (packageOrClassName == null || packageOrClassName.isEmpty()) {
            return false;
        }

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace != null && stackTrace.length > 0) {
            for (StackTraceElement traceElement : stackTrace) {

                for (String prefix : packageOrClassName) {
                    if (traceElement.getClassName().startsWith(prefix)) {
                        return true;
                    }
                }

            }
        }

        return false;
    }
}
