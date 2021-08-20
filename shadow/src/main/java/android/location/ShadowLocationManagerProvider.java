package android.location;

import android.content.Context;

public interface ShadowLocationManagerProvider {

    Location getLastKnownLocation(Context context, String provider);

    public static class Adapter implements ShadowLocationManagerProvider {

        @Override
        public Location getLastKnownLocation(Context context, String provider) {
            return null;
        }
    }

    public static class Wrapper {

        private final Context baseContext;

        private final ShadowLocationManagerProvider provider;

        public Wrapper(Context baseContext, ShadowLocationManagerProvider provider) {
            this.baseContext = baseContext;
            this.provider = provider;
        }

        public Location getLastKnownLocation(String provider) {
            return this.provider.getLastKnownLocation(baseContext, provider);
        }

    }
}
