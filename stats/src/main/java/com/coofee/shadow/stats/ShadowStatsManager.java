package com.coofee.shadow.stats;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ShadowStatsManager {

    public interface OnInitCallback {
        void onSuccess();

        void onError(String msg, Throwable e);
    }

    private static final ShadowStatsManager INSTANCE = new ShadowStatsManager();

    private final Set<OnStatsListener> mOnStatsListenerSet = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static ShadowStatsManager init(Context context, OnInitCallback callback) {
        final File shadowstatsDir = new File(context.getCodeCacheDir(), "shadowstats");
        if (!shadowstatsDir.exists()) {
            shadowstatsDir.mkdirs();
        }
        if (!shadowstatsDir.exists()) {
            callback.onError("fail create shadowstats dir=" + shadowstatsDir, new Throwable());
            return INSTANCE;
        }

        final File soFile = new File(shadowstatsDir, "libshadow.so");
        final File configFile = new File(shadowstatsDir, "libshadow.config.so");
        final File jsFile = new File(shadowstatsDir, "shadowstats.js");

        final String cpuArch = guessArch();
        Log.e("ShadowStatsManager", "cpuArch=" + cpuArch);

        if (copy(context, "shadowstats/" + cpuArch + "/libshadow.so", soFile, callback)
                && writeConfig(configFile, jsFile, callback)
                && copy(context, "shadowstats/shadowstats.js", jsFile, callback)
        ) {
            try {
                System.load(soFile.getAbsolutePath());
                callback.onSuccess();
            } catch (Throwable e) {
                callback.onError("fail load so shadow=" + soFile, e);
            }
        }
        return INSTANCE;
    }

    public void addOnStatsListener(OnStatsListener listener) {
        if (listener != null) {
            mOnStatsListenerSet.add(listener);
        }
    }

    public void removeOnStatsListener(OnStatsListener listener) {
        mOnStatsListenerSet.remove(listener);
    }

    public void removeAllOnStatsListeners() {
        mOnStatsListenerSet.clear();
    }

    static void on(String type, String json) {
        for (OnStatsListener listener : INSTANCE.mOnStatsListenerSet) {
            listener.on(type, json);
        }
    }

    private static boolean writeConfig(File configFile, File jsFile, OnInitCallback callback) {
        BufferedWriter writer = null;
        try {
            String config = new JSONObject().put("interaction", new JSONObject()
                    .put("type", "script")
                    .put("path", jsFile.getAbsolutePath())
                    .put("on_change", "reload")
            ).toString();

            Log.e("ShadowStatsManager", "config=" + config);
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8));
            writer.write(config);
            writer.flush();
            return true;
        } catch (Throwable e) {
            callback.onError("fail write config to file=" + configFile, e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private static String guessArch() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (Process.is64Bit()) {
//                return "arm64-v8a";
//            }
//        }

        final String[] supportAbis = Build.SUPPORTED_ABIS;
        Log.e("ShadowStatsManager", "support_abis=" + Arrays.toString(supportAbis));
        if (supportAbis != null && supportAbis.length > 0) {
            return supportAbis[0];
        }

        return "armeabi";
    }

    private static boolean copy(Context context, String assetFile, File file, OnInitCallback callback) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(context.getAssets().open(assetFile));
            out = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buf = new byte[8192];
            int len = 0;
            while ((len = in.read(buf, 0, buf.length)) != -1) {
                out.write(buf, 0, len);
            }
            out.flush();
            return true;
        } catch (Throwable e) {
            callback.onError("fail copy from assets=shadowstats.js to file=" + file, e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                // ignore
            }

            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                // ignore
            }
        }

        return false;
    }
}
