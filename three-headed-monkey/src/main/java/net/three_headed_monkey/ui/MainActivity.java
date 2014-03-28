package net.three_headed_monkey.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import org.androidannotations.annotations.EActivity;
import net.three_headed_monkey.R;

@EActivity(R.layout.main_activity)
public class MainActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
