package android.location;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.shadow.ShadowLog;
import android.shadow.ShadowServiceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ShadowLocationManager extends LocationManager {

    public ShadowLocationManager() {
        super();
    }

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public Location getLastKnownLocation(@NonNull String provider) {
        ShadowLog.e("access getLastKnownLocation", new Throwable());
        return ShadowServiceManager.config().locationManagerProvider().getLastKnownLocation(provider);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void requestLocationUpdates(@NonNull String provider, long minTime, float minDistance, @NonNull LocationListener listener) {
        ShadowLog.e("access requestLocationUpdates", new Throwable());
//        super.requestLocationUpdates(provider, minTime, minDistance, listener);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void requestLocationUpdates(@NonNull String provider, long minTime, float minDistance, @NonNull LocationListener listener, @Nullable Looper looper) {
        ShadowLog.e("access requestLocationUpdates", new Throwable());
//        super.requestLocationUpdates(provider, minTime, minDistance, listener, looper);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void requestLocationUpdates(long minTime, float minDistance, @NonNull Criteria criteria, @NonNull LocationListener listener, @Nullable Looper looper) {
        ShadowLog.e("access requestLocationUpdates", new Throwable());
//        super.requestLocationUpdates(minTime, minDistance, criteria, listener, looper);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void requestLocationUpdates(@NonNull String provider, long minTime, float minDistance, @NonNull PendingIntent intent) {
        ShadowLog.e("access requestLocationUpdates", new Throwable());
//        super.requestLocationUpdates(provider, minTime, minDistance, intent);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void requestLocationUpdates(long minTime, float minDistance, @NonNull Criteria criteria, @NonNull PendingIntent intent) {
        ShadowLog.e("access requestLocationUpdates", new Throwable());
//        super.requestLocationUpdates(minTime, minDistance, criteria, intent);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void requestSingleUpdate(@NonNull String provider, @NonNull LocationListener listener, @Nullable Looper looper) {
        ShadowLog.e("access requestLocationUpdates", new Throwable());
//        super.requestSingleUpdate(provider, listener, looper);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void requestSingleUpdate(@NonNull Criteria criteria, @NonNull LocationListener listener, @Nullable Looper looper) {
        ShadowLog.e("access requestLocationUpdates", new Throwable());
//        super.requestSingleUpdate(criteria, listener, looper);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void requestSingleUpdate(@NonNull String provider, @NonNull PendingIntent intent) {
        ShadowLog.e("access requestLocationUpdates", new Throwable());
//        super.requestSingleUpdate(provider, intent);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void requestSingleUpdate(@NonNull Criteria criteria, @NonNull PendingIntent intent) {
        ShadowLog.e("access requestLocationUpdates", new Throwable());
//        super.requestSingleUpdate(criteria, intent);
    }

    @NonNull

    @Override
    public List<String> getAllProviders() {
        return super.getAllProviders();
    }

    @NonNull

    @Override
    public List<String> getProviders(boolean enabledOnly) {
        return super.getProviders(enabledOnly);
    }

    @Nullable

    @Override
    public LocationProvider getProvider(@NonNull String name) {
        return super.getProvider(name);
    }

    @NonNull

    @Override
    public List<String> getProviders(@NonNull Criteria criteria, boolean enabledOnly) {
        return super.getProviders(criteria, enabledOnly);
    }

    @Nullable

    @Override
    public String getBestProvider(@NonNull Criteria criteria, boolean enabledOnly) {
        return super.getBestProvider(criteria, enabledOnly);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void removeUpdates(@NonNull LocationListener listener) {
//        super.removeUpdates(listener);
    }

    @Override
    public void removeUpdates(@NonNull PendingIntent intent) {
        super.removeUpdates(intent);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void addProximityAlert(double latitude, double longitude, float radius, long expiration, @NonNull PendingIntent intent) {
        ShadowLog.e("access addProximityAlert", new Throwable());
//        super.addProximityAlert(latitude, longitude, radius, expiration, intent);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void removeProximityAlert(@NonNull PendingIntent intent) {
//        super.removeProximityAlert(intent);
    }

    @Override
    public boolean isLocationEnabled() {
        return super.isLocationEnabled();
    }

    @Override
    public boolean isProviderEnabled(@NonNull String provider) {
        return super.isProviderEnabled(provider);
    }

    @Override
    public void addTestProvider(@NonNull String name, boolean requiresNetwork, boolean requiresSatellite, boolean requiresCell, boolean hasMonetaryCost, boolean supportsAltitude, boolean supportsSpeed, boolean supportsBearing, int powerRequirement, int accuracy) {
        super.addTestProvider(name, requiresNetwork, requiresSatellite, requiresCell, hasMonetaryCost, supportsAltitude, supportsSpeed, supportsBearing, powerRequirement, accuracy);
    }

    @Override
    public void removeTestProvider(@NonNull String provider) {
        super.removeTestProvider(provider);
    }

    @Override
    public void setTestProviderLocation(@NonNull String provider, @NonNull Location loc) {
        super.setTestProviderLocation(provider, loc);
    }

    @Override
    public void clearTestProviderLocation(@NonNull String provider) {
        super.clearTestProviderLocation(provider);
    }

    @Override
    public void setTestProviderEnabled(@NonNull String provider, boolean enabled) {
        super.setTestProviderEnabled(provider, enabled);
    }

    @Override
    public void clearTestProviderEnabled(@NonNull String provider) {
        super.clearTestProviderEnabled(provider);
    }

    @Override
    public void setTestProviderStatus(@NonNull String provider, int status, @Nullable Bundle extras, long updateTime) {
        super.setTestProviderStatus(provider, status, extras, updateTime);
    }

    @Override
    public void clearTestProviderStatus(@NonNull String provider) {
        super.clearTestProviderStatus(provider);
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean addGpsStatusListener(GpsStatus.Listener listener) {
        ShadowLog.e("access addGpsStatusListener", new Throwable());
//        return super.addGpsStatusListener(listener);
        return false;
    }

    @Override
    public void removeGpsStatusListener(GpsStatus.Listener listener) {
        super.removeGpsStatusListener(listener);
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean registerGnssStatusCallback(@NonNull GnssStatus.Callback callback) {
        ShadowLog.e("access registerGnssStatusCallback", new Throwable());
//        return super.registerGnssStatusCallback(callback);
        return false;
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean registerGnssStatusCallback(@NonNull GnssStatus.Callback callback, @Nullable Handler handler) {
        ShadowLog.e("access registerGnssStatusCallback", new Throwable());
//        return super.registerGnssStatusCallback(callback, handler);
        return false;
    }

    @Override
    public void unregisterGnssStatusCallback(@NonNull GnssStatus.Callback callback) {
        super.unregisterGnssStatusCallback(callback);
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean addNmeaListener(@NonNull OnNmeaMessageListener listener) {
        ShadowLog.e("access addNmeaListener", new Throwable());
//        return super.addNmeaListener(listener);
        return false;
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean addNmeaListener(@NonNull OnNmeaMessageListener listener, @Nullable Handler handler) {
        ShadowLog.e("access addNmeaListener", new Throwable());
//        return super.addNmeaListener(listener, handler);
        return false;
    }

    @Override
    public void removeNmeaListener(@NonNull OnNmeaMessageListener listener) {
        super.removeNmeaListener(listener);
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean registerGnssMeasurementsCallback(@NonNull GnssMeasurementsEvent.Callback callback) {
        ShadowLog.e("access registerGnssMeasurementsCallback", new Throwable());
//        return super.registerGnssMeasurementsCallback(callback);
        return false;
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean registerGnssMeasurementsCallback(@NonNull GnssMeasurementsEvent.Callback callback, @Nullable Handler handler) {
        ShadowLog.e("access registerGnssMeasurementsCallback", new Throwable());
//        return super.registerGnssMeasurementsCallback(callback, handler);
        return false;
    }

    @Override
    public void unregisterGnssMeasurementsCallback(@NonNull GnssMeasurementsEvent.Callback callback) {
        super.unregisterGnssMeasurementsCallback(callback);
    }

    @Override
    public boolean registerGnssNavigationMessageCallback(@NonNull GnssNavigationMessage.Callback callback) {
        return super.registerGnssNavigationMessageCallback(callback);
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean registerGnssNavigationMessageCallback(@NonNull GnssNavigationMessage.Callback callback, @Nullable Handler handler) {
        ShadowLog.e("access registerGnssNavigationMessageCallback", new Throwable());
//        return super.registerGnssNavigationMessageCallback(callback, handler);
        return false;
    }

    @Override
    public void unregisterGnssNavigationMessageCallback(@NonNull GnssNavigationMessage.Callback callback) {
        super.unregisterGnssNavigationMessageCallback(callback);
    }

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public GpsStatus getGpsStatus(@Nullable GpsStatus status) {
        ShadowLog.e("access getGpsStatus", new Throwable());
//        return super.getGpsStatus(status);
        return null;
    }

    @Override
    public int getGnssYearOfHardware() {
        return super.getGnssYearOfHardware();
    }

    @Nullable
    @Override
    public String getGnssHardwareModelName() {
        return super.getGnssHardwareModelName();
    }

    @Override
    public boolean sendExtraCommand(@NonNull String provider, @NonNull String command, @Nullable Bundle extras) {
        return super.sendExtraCommand(provider, command, extras);
    }
}