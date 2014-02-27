package net.three_headed_monkey.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class LauncherActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainActivity_.class);
        startActivity(intent);
        finish();
    }
}