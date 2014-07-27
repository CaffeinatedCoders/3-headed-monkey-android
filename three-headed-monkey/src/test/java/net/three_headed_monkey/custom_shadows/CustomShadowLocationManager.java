package net.three_headed_monkey.custom_shadows;

import android.location.LocationManager;

import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowLocationManager;

@Implements(LocationManager.class)
public class CustomShadowLocationManager extends ShadowLocationManager {
    public CustomShadowLocationManager() {
        super();
        setProviderEnabled(LocationManager.PASSIVE_PROVIDER, true);
    }
}
