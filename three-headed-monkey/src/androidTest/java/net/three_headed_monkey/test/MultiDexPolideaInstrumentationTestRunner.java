package net.three_headed_monkey.test;

import android.os.Bundle;
import android.support.multidex.MultiDex;

import pl.polidea.instrumentation.PolideaInstrumentationTestRunner;

public class MultiDexPolideaInstrumentationTestRunner extends PolideaInstrumentationTestRunner{
    @Override
    public void onCreate(Bundle arguments) {
        MultiDex.install(getTargetContext());
        super.onCreate(arguments);
    }
}
