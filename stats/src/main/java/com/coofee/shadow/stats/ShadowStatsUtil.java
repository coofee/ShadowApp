package com.coofee.shadow.stats;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ShadowStatsUtil {
    public static final String SHADOW_STATS_DIR_NAME = "shadowstats";
    public static final String SHADOW_STATS_SO_NAME = "libshadow.so";
    public static final String SHADOW_STATS_OS_CONFIG_NAME = "libshadow.config.so";
    public static final String SHADOW_STATS_JS_NAME = "shadowstats.js";

    public static final String SHADOW_STATS_ASSETS_JS_PATH = "shadowstats/shadowstats.js";

    public static File getShadowDir(Context context) {
        final File shadowstatsDir = new File(context.getCodeCacheDir(), SHADOW_STATS_DIR_NAME);
        if (!shadowstatsDir.exists()) {
            shadowstatsDir.mkdirs();
        }
        return shadowstatsDir;
    }

    public static File getShadowFileName(Context context, String name) {
        final File shadowstatsDir = new File(context.getCodeCacheDir(), SHADOW_STATS_DIR_NAME);
        if (!shadowstatsDir.exists()) {
            shadowstatsDir.mkdirs();
        }

        return new File(shadowstatsDir, name);
    }

    public static String getScriptPath(ShadowStatsConfig config) {
        if (TextUtils.isEmpty(config.localFilePath)) {
            return getShadowFileName(config.context, SHADOW_STATS_JS_NAME).getAbsolutePath();
        }

        File localFilePath = new File(config.localFilePath);
        if (localFilePath.exists() && localFilePath.canRead()) {
            return localFilePath.getAbsolutePath();
        } else {
            return getShadowFileName(config.context, SHADOW_STATS_JS_NAME).getAbsolutePath();
        }
    }

    public static JSONObject generateJsonConfig(ShadowStatsConfig config) throws Throwable {
        final String path;
        if (TextUtils.isEmpty(config.localFilePath)) {
            path = SHADOW_STATS_JS_NAME;
        } else {
            File localFilePath = new File(config.localFilePath);
            if (localFilePath.exists() && localFilePath.canRead()) {
                path = localFilePath.getAbsolutePath();
            } else {
                path = SHADOW_STATS_JS_NAME;
            }
        }

        return new JSONObject().put("interaction", new JSONObject()
                .put("type", "script")
                .put("on_change", "reload")
                .put("path", path)
                .put("parameters", new JSONObject()
                        .put("is_release", !config.debuggable)
                        .put("open_log", config.openLog)
                        .put("tag", config.tag)
                )
        );
    }

    public static String guessArch() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (Process.is64Bit()) {
//                return "arm64-v8a";
//            }
//        }

        final String[] supportAbis = Build.SUPPORTED_ABIS;
        if (supportAbis != null && supportAbis.length > 0) {
            String cpuArch = supportAbis[0];
            if (cpuArch.contains("x86")) {
                return "armeabi";
            } else {
                return cpuArch;
            }
        }

        return "armeabi";
    }

    public static boolean writeText(File file, String text) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            writer.write(text);
            writer.flush();
            return true;
        } finally {
            closeQuiet(writer);
        }
    }

    public static void copy(InputStream in, OutputStream out) throws Throwable {
        byte[] buf = new byte[8192];
        int len = 0;
        while ((len = in.read(buf, 0, buf.length)) != -1) {
            out.write(buf, 0, len);
        }
        out.flush();
    }

    public static boolean copy(File src, File dest) throws Throwable {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(src));
            out = new BufferedOutputStream(new FileOutputStream(dest));
            copy(in, out);
            return true;
        } finally {
            closeQuiet(in);
            closeQuiet(out);
        }
    }

    public static boolean copy(Context context, String assetFile, File file) throws Throwable {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(context.getAssets().open(assetFile));
            out = new BufferedOutputStream(new FileOutputStream(file));
            copy(in, out);
            return true;
        } finally {
            closeQuiet(in);
            closeQuiet(out);
        }
    }

    public static boolean copy(Context context, String assetFile, File file, ShadowStatsManager.OnInitCallback callback) {
        try {
            copy(context, assetFile, file);
            return true;
        } catch (Throwable e) {
            callback.onError("fail copy from assets=" + assetFile + " to file=" + file, e);
        }

        return false;
    }

    public static void closeQuiet(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
}
