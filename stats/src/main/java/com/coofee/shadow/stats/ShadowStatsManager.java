package com.coofee.shadow.stats;

import android.util.Log;
import dalvik.system.BaseDexClassLoader;
import org.json.JSONObject;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class ShadowStatsManager {
    public interface OnInitCallback {
        void onSuccess();

        void onError(String msg, Throwable e);
    }

    private static final AtomicReference<ShadowStatsManager> sInstanceRef = new AtomicReference<>();

    private final ShadowStatsConfig mConfig;

    private final Set<ShadowStatsListener> mOnStatsListenerSet = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public ShadowStatsManager(ShadowStatsConfig config) {
        this.mConfig = config;
    }

    public static ShadowStatsManager init(ShadowStatsConfig config, OnInitCallback callback) {
        final ShadowStatsManager shadowStatsManager = new ShadowStatsManager(config);
        if (sInstanceRef.compareAndSet(null, shadowStatsManager)) {
            shadowStatsManager.tryInit(callback);
        }
        return sInstanceRef.get();
    }

    public static ShadowStatsManager getInstance() {
        return sInstanceRef.get();
    }

    static void on(String type, String json) {
        ShadowStatsManager shadowStatsManager = sInstanceRef.get();
        if (shadowStatsManager == null) {
            return;
        }

        for (ShadowStatsListener listener : shadowStatsManager.mOnStatsListenerSet) {
            listener.on(type, json);
        }
    }

    public void addStatsListener(ShadowStatsListener listener) {
        if (listener != null) {
            mOnStatsListenerSet.add(listener);
        }
    }

    public void removeStatsListener(ShadowStatsListener listener) {
        mOnStatsListenerSet.remove(listener);
    }

    public void removeAllStatsListeners() {
        mOnStatsListenerSet.clear();
    }

    public void log(String msg, Throwable e) {
        if (mConfig.openLog) {
            Log.d(mConfig.tag, msg, e);
        }
    }

    public void log(String msg) {
        if (mConfig.openLog) {
            Log.d(mConfig.tag, msg);
        }
    }

    public boolean updateScript(File newScriptFile) {
        if (mConfig.mode == ShadowStatsConfig.MODE_REMOTE) {
            log("MODE_REMOTE not support update script.");
            return false;
        }

        final File currentScriptFile = new File(ShadowStatsUtil.getScriptPath(mConfig));
        final File backupScriptFile = new File(currentScriptFile.getParentFile(), currentScriptFile.getName() + ".bak");
        if (!currentScriptFile.exists()) {
            backupScriptFile.delete();
        }

        try {
            if (!currentScriptFile.renameTo(backupScriptFile)) {
                log("fail update script; cannot backup current script file.");
                return false;
            }

            boolean renameTo = newScriptFile.renameTo(currentScriptFile);
            log((renameTo ? "success" : "fail") + " update script.");
            return renameTo;
        } catch (Throwable e) {
            boolean rollback = backupScriptFile.renameTo(currentScriptFile);
            log("fail update script; rollback current script status=" + rollback, e);
        }

        return false;
    }

    public boolean updateScript(String script) {
        if (mConfig.mode == ShadowStatsConfig.MODE_REMOTE) {
            log("MODE_REMOTE not support update script.");
            return false;
        }

        final File currentScriptFile = new File(ShadowStatsUtil.getScriptPath(mConfig));
        final File backupScriptFile = new File(currentScriptFile.getParentFile(), currentScriptFile.getName() + ".bak");
        if (currentScriptFile.exists()) {
            backupScriptFile.delete();
        }

        final File newScriptFile = new File(currentScriptFile.getParentFile(), currentScriptFile.getName() + ".tmp");
        try {
            ShadowStatsUtil.writeText(newScriptFile, script);
            if (!currentScriptFile.renameTo(backupScriptFile)) {
                log("fail update script; cannot backup current script file.");
                return false;
            }

            boolean renameTo = newScriptFile.renameTo(currentScriptFile);
            log((renameTo ? "success" : "fail") + " update script.");
            return renameTo;
        } catch (Throwable e) {
            boolean rollback = backupScriptFile.renameTo(currentScriptFile);
            log("fail update script; rollback current script status=" + rollback, e);
        }

        return false;
    }

    private boolean tryInit(OnInitCallback callback) {
        ShadowStatsConfig config = mConfig;

        if (config.mode == ShadowStatsConfig.MODE_REMOTE) {
            try {
                System.loadLibrary("shadow");
                callback.onSuccess();
                return true;
            } catch (Throwable e) {
                callback.onError("fail load so shadow", e);
            }
            return false;
        }

        final BaseDexClassLoader classLoader = (BaseDexClassLoader) config.context.getClassLoader();
        final String shadowSoFilePath = classLoader.findLibrary("shadow");
        log("shadowSoFilePath=" + shadowSoFilePath);

        final File shadowstatsDir = ShadowStatsUtil.getShadowDir(config.context);
        if (!shadowstatsDir.exists()) {
            callback.onError("fail create shadowstats dir=" + shadowstatsDir, new Throwable());
            return false;
        }

        final File soFile = ShadowStatsUtil.getShadowFileName(config.context, ShadowStatsUtil.SHADOW_STATS_SO_NAME);
        try {
            ShadowStatsUtil.copy(new File(shadowSoFilePath), soFile);
        } catch (Throwable e) {
            callback.onError("fail copy shadow so from " + shadowSoFilePath + " to " + soFile, e);
            return false;
        }

        final String shadowJsSrcFile = ShadowStatsUtil.SHADOW_STATS_ASSETS_JS_PATH;
        final File jsFile = ShadowStatsUtil.getShadowFileName(config.context, ShadowStatsUtil.SHADOW_STATS_JS_NAME);
        try {
            ShadowStatsUtil.copy(config.context, shadowJsSrcFile, jsFile);
        } catch (Throwable e) {
            callback.onError("fail copy shadow js from assets=" + shadowJsSrcFile + " to file=" + jsFile, e);
            return false;
        }

        final File configFile = ShadowStatsUtil.getShadowFileName(config.context, ShadowStatsUtil.SHADOW_STATS_OS_CONFIG_NAME);
        final String json;
        try {
            JSONObject jsonConfig = ShadowStatsUtil.generateJsonConfig(config);
            json = jsonConfig.toString();
            log("config=" + json);
        } catch (Throwable e) {
            callback.onError("fail generate config", e);
            return false;
        }

        try {
            if (ShadowStatsUtil.writeText(configFile, json)) {
                try {
                    System.load(soFile.getAbsolutePath());
                    callback.onSuccess();
                    return true;
                } catch (Throwable e) {
                    callback.onError("fail load so shadow=" + soFile, e);
                }
            }
        } catch (Throwable e) {
            callback.onError("fail write config to file=" + configFile, e);
            return false;
        }

        return false;
    }

}
