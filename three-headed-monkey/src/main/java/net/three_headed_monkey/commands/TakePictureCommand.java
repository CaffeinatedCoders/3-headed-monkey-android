package net.three_headed_monkey.commands;


import android.app.ActionBar;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.squareup.okhttp.MediaType;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.communication.OutgoingBroadcastCommandCommunication;
import net.three_headed_monkey.communication.OutgoingServiceApiCommandCommunication;



public class TakePictureCommand extends LooperCommand implements SurfaceHolder.Callback, Camera.PictureCallback {
    public static final String TAG = "TakePictureCommand";

//    private WindowManager windowManager;
    private Camera camera = null;
    private SurfaceView surfaceView;
    private LinearLayout layout = null;

    public TakePictureCommand(ThreeHeadedMonkeyApplication application) {
        super(application);

    }

    @Override
    public long getTimeoutDelaySeconds() {
        return 20;
    }

    @Override
    protected void doExecute(String command) {
//        Intent intent = TakePictureActivity_.intent(application).get();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        application.startActivity(intent);

        if(!(outgoingCommandCommunication instanceof OutgoingServiceApiCommandCommunication || outgoingCommandCommunication instanceof OutgoingBroadcastCommandCommunication)) {
            sendResponse("takePicture command only works if called from webservice");
            cancelLooper();
            return;
        }

        WindowManager windowManager = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
        layout = new LinearLayout(application);
//        TextView textView = new TextView(application);
//        textView.setText("Hello World!");
//        layout.addView(textView);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        surfaceView = new SurfaceView(application);
        surfaceView.setLayoutParams(new ActionBar.LayoutParams(1,1));
        layout.addView(surfaceView);
        windowManager.addView(layout, params);

        int camera_id = findFrontFacingCamera();
        if(camera_id < 0)
            return;
        camera = Camera.open(camera_id);

        surfaceView.getHolder().addCallback(this);

    }

    @Override
    public void onBeforeQuit() {
        Log.d(TAG, "onBeforeQuit");
        if(camera != null) {
            camera.stopPreview();
            surfaceView.removeCallbacks(this);
            camera.release();
        }
        if(layout != null) {
            Log.d(TAG, "Remove layout from Window");
            layout.setVisibility(View.GONE);
            surfaceView.setVisibility(View.GONE);
            WindowManager windowManager = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
            windowManager.removeView(layout);
        }
        super.onBeforeQuit();
    }

    @Override
    protected boolean respondsToCommand(String command) {
        return command.equalsIgnoreCase("takePicture") || command.equalsIgnoreCase("tp");
    }

    @Override
    protected String getShortUsageText() {
        return "takePicture - Takes a picture";
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.i(TAG, "Fake SUrface Created");
        try {
            camera.setPreviewDisplay(surfaceView.getHolder());
            camera.startPreview();
            Thread.sleep(1000, 0);
            camera.takePicture(null, null, this);
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        Log.d(TAG, "onPictureTaken called, size: " + bytes.length);
        if(!(outgoingCommandCommunication instanceof OutgoingServiceApiCommandCommunication)) {
            sendResponse("Can only upload file if command has been started from web");
        } else {
           OutgoingServiceApiCommandCommunication comm = (OutgoingServiceApiCommandCommunication)outgoingCommandCommunication;
            comm.sendFile("picture_front.jpg", MediaType.parse("image/jpeg"), bytes);
        }
        cancelLooper();
    }

}
