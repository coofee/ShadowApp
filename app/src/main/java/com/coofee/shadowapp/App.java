package com.coofee.shadowapp;

import android.app.Application;
import android.content.Context;
import android.shadow.*;
import com.coofee.shadowapp.shadow.LocationManagerInterceptor;
import com.coofee.shadowapp.shadow.TelephonyManagerInterceptor;
import com.coofee.shadowapp.shadow.WifiManagerInterceptor;

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        initShadowManager(base);
    }

    private void initShadowManager(Context base) {
        BootstrapClass.exempt("Landroid");

        ShadowConfig.Builder shadowConfigBuilder = new ShadowConfig.Builder(base, this);

        if (BuildConfig.DEBUG) {
            ShadowLog.debug = true;
            shadowConfigBuilder.interceptAll(true).debug(true);
            ShadowLog.e("debug or test, we will intercept all.");

        } else {
            shadowConfigBuilder.interceptAll(false).debug(false);
        }

        shadowConfigBuilder
                .add(new WifiManagerInterceptor())
                .add(new TelephonyManagerInterceptor())
                .add(new LocationManagerInterceptor())
        ;

        ShadowServiceManager.init(shadowConfigBuilder.build());
    }

}
