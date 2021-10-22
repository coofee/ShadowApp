package com.coofee.shadow.stats;

import androidx.annotation.Keep;

@Keep
public class ShadowStatsBridge {

    public static void on(String type, String json) {
        ShadowStatsManager.on(type, json);
    }

}
