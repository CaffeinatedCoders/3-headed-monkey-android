package net.three_headed_monkey.commands;


import android.nfc.Tag;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;


abstract public class LooperCommand extends Command {
    public static final String TAG = "LooperCommand";
    protected Handler handler;

    public LooperCommand(ThreeHeadedMonkeyApplication application) {
        super(application);
    }

    @Override
    public void run() {
        Thread looper_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Looper.prepare(); //may throw exception if looper already exists
                } catch (Exception e) {
                    Log.d(TAG, "Looper prepare failed, this should not happen", e);
                }

                handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        handleLooperMessage(msg);
                    }
                };
                LooperCommand.super.run();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onBeforeTimeout();
                        cancelLooper();
                    }
                }, getTimeoutDelaySeconds() * 1000);
                Looper.loop();
            }
        });
        Log.d(TAG, "Starting LooperThread");
        looper_thread.start();
        try {
            looper_thread.join();
            Log.d(TAG, "LooperThread finished");
        } catch (InterruptedException e) {
            Log.e(TAG, "LooperThread has been interrupted", e);
        }

    }

    /**
     * @return delay in seconds the command will timeout, but the doExecute will always be finished first
     */
    abstract public long getTimeoutDelaySeconds();

    /**
     * Just in case the looper is used for more than callbacks
     * @param message
     */
    public void handleLooperMessage(Message message) {

    }

    public void onBeforeTimeout(){}
    public void onBeforeQuit(){}

    /**
     *
     */
    public void cancelLooper() {
        onBeforeQuit();
        Looper.myLooper().quit();
    }
}
