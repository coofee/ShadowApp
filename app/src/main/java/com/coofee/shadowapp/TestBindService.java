package com.coofee.shadowapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import com.coofee.shadowapp.test.BinderProvider;
import com.coofee.shadowapp.test.IServiceFetcher;

public class TestBindService extends Service {

    private final ServiceFetcher serviceFetcher = new ServiceFetcher();

    public TestBindService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceFetcher;
    }

    private class ServiceFetcher extends IServiceFetcher.Stub {

        @Override
        public IBinder getService(String name) throws RemoteException {
            return BinderProvider.getService(TestBindService.this, name);
        }
    }
}