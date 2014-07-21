package net.three_headed_monkey.test_utils;

import android.content.Context;
import android.location.LocationManager;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;

import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLocationManager;

public abstract class TestBase {
    public ThreeHeadedMonkeyApplication application;
    public ShadowApplication shadowApplication;
    public LocationManager locationManager;
    public ShadowLocationManager shadowLocationManager;

    public void setUp() throws Exception {
        application = (ThreeHeadedMonkeyApplication) Robolectric.application;
        shadowApplication = Robolectric.shadowOf(application);

        locationManager = (LocationManager) Robolectric.application.getSystemService(Context.LOCATION_SERVICE);
        shadowLocationManager = Robolectric.shadowOf(locationManager);
        shadowLocationManager.setProviderEnabled(LocationManager.PASSIVE_PROVIDER, true);

    }
}
