package net.three_headed_monkey.ui.command_activities;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;

import net.three_headed_monkey.R;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

@SuppressLint("Registered")
@EActivity(R.layout.activity_alarm_command_activity)
public class AlarmCommandActivity extends Activity {
    public static final String TAG = "AlarmCommandActivity";

    //public for testing
    public MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        startAlarm();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Background
    public void startAlarm() {
        Log.d(TAG, "Starting alarm");
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                    0);
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound);
        mediaPlayer.setWakeMode(AlarmCommandActivity.this, PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public void stopAlarm() {
        Log.d(TAG, "Stopping alarm");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        stopAlarm();
        super.onDestroy();
    }

    @Click(R.id.layout_alarm_activity_container)
    public void closeActivity() {
        stopAlarm();
        finish();
    }


}
