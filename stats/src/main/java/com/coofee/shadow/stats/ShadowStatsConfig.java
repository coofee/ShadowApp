package com.coofee.shadow.stats;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import androidx.annotation.IntDef;

public class ShadowStatsConfig {

    public static final int MODE_LOCAL_FILE = 1;

    public static final int MODE_REMOTE = 2;

    @IntDef(value = {MODE_LOCAL_FILE, MODE_REMOTE})
    public @interface Mode {

    }

    public final Context context;

    public final boolean debuggable;

    public final boolean openLog;

    public final String tag;

    public final int mode;

    public final String localFilePath;

    public ShadowStatsConfig(Builder builder) {
        this.context = builder.context;
        this.debuggable = builder.debuggable;
        this.openLog = builder.openLog;
        this.tag = builder.tag;
        this.mode = builder.mode;
        this.localFilePath = builder.localFilePath;
    }

    public static class Builder {
        public final Context context;

        public boolean debuggable;

        public boolean openLog;

        public String tag;

        public int mode;

        public String localFilePath;

        public Builder(Context context) {
            this.context = context;
            this.debuggable = (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE;
            this.openLog = this.debuggable;
            this.tag = "ShadowStatsManager";
            this.mode = MODE_LOCAL_FILE;
        }

        public Builder debuggable(boolean debuggable) {
            this.debuggable = debuggable;
            return this;
        }

        public Builder openLog(boolean openLog) {
            this.openLog = openLog;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder mode(@Mode int mode) {
            this.mode = mode;
            return this;
        }

        public Builder localFilePath(String localFilePath) {
            this.localFilePath = localFilePath;
            return this;
        }

        public ShadowStatsConfig build() {
            return new ShadowStatsConfig(this);
        }

    }
}
