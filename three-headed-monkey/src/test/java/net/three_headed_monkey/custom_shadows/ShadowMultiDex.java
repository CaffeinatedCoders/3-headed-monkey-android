//copied from https://raw.githubusercontent.com/robolectric/robolectric/master/robolectric-shadows/shadows-multidex/src/main/java/org/robolectric/shadows/ShadowMultiDex.java
//@TODO: can be removed once this class makes it into robolectric release


package net.three_headed_monkey.custom_shadows;

import android.content.Context;
import android.support.multidex.MultiDex;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(MultiDex.class)
public class ShadowMultiDex {
  @Implementation
  public static void install(Context context) {
    // Do nothing since with Robolectric nothing is dexed.
  }
}
