package com.coofee.shadow.stats;

public interface OnStatsListener {
    void onAttach();

    void on(String type, String json);

    void onDetach();
}
