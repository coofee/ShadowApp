package com.coofee.shadowapp;

import android.app.Application;
import android.content.Context;
import android.shadow.*;
import com.coofee.shadowapp.shadow.location.ILocationManagerInterceptor;
import com.coofee.shadowapp.shadow.telephony.IPhoneSubInfoInterceptor;
import com.coofee.shadowapp.shadow.telephony.ITelephonyInterceptor;
import com.coofee.shadowapp.shadow.wifi.IWifiManagerInterceptor;

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
            ShadowLog.d("debug or test, we will intercept all.");

        } else {
            shadowConfigBuilder.interceptAll(false).debug(false);
        }

        shadowConfigBuilder
                .add(new IWifiManagerInterceptor())
                .add(new ILocationManagerInterceptor())
                .add(new ITelephonyInterceptor())
                .add(new IPhoneSubInfoInterceptor())
        ;

        ShadowServiceManager.init(shadowConfigBuilder.build());
    }

}
