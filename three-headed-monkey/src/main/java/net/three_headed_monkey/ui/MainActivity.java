package net.three_headed_monkey.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import net.three_headed_monkey.R;

import org.androidannotations.annotations.EActivity;

@SuppressLint("Registered")
@EActivity(R.layout.main_activity)
public class MainActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
