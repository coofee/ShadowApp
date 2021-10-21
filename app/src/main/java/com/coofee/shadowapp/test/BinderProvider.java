package com.coofee.shadowapp.test;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.*;
import android.shadow.ShadowLog;
import android.util.ArrayMap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BinderProvider extends ContentProvider {

    public static String AUTHORITY = BinderProvider.class.getName();

    private static boolean sSameProcess = false;

    private static final ArrayMap<String, IBinder> sServiceMap = new ArrayMap<String, IBinder>();

    private static final IServiceFetcher sServiceFetcher = new IServiceFetcher.Stub() {

        @Override
        public IBinder getService(String name) throws RemoteException {
            return sServiceMap.get(name);
        }
    };

    private static class TestService extends Binder {
        public TestService() {

        }

        @Override
        protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
            return super.onTransact(code, data, reply, flags);
        }
    }

    @Override
    public void attachInfo(Context context, ProviderInfo info) {
        super.attachInfo(context, info);
        BinderProvider.AUTHORITY = info.authority;
    }

    @Override
    public boolean onCreate() {

        sSameProcess = true;
        sServiceMap.put("test_service", new TestService());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        IBinder iBinder = sServiceMap.get(arg);
        Bundle result = new Bundle();
        result.putBinder(":binder", iBinder);
        return result;
    }

    public static IBinder getService(Context context, String name) {
        try {
            IBinder binder = null;
            if (sSameProcess) {
                binder = sServiceFetcher.getService(name);
            } else {

                Uri contentUri = Uri.parse("content://" + AUTHORITY);
                Bundle data = context.getContentResolver().call(contentUri, "@", "test_service", null);
                // binder is android.os.BinderProxy
                binder = data.getBinder(":binder");
            }
            ShadowLog.e("BinderProvider.getService; binder=" + binder + " by name=" + name);
            return binder;
        } catch (RemoteException e) {
            ShadowLog.e("fail get service by name=" + name, e);
        }
        return null;
    }
}
