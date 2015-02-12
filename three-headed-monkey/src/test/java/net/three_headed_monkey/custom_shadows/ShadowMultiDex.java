package net.three_headed_monkey.custom_shadows;

import android.content.Context;
import android.support.multidex.MultiDex;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(MultiDex.class)
public class ShadowMultiDex {
    @Implementation
    public static void install(Context context) {
        // do nothing
    }
}
