package com.wilki.tica.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.wilki.tica.dragAndTouchListeners.DialogClickListener;
import com.wilki.tica.logicLayer.InterfaceType;
import com.wilki.tica.R;
import com.wilki.tica.logicLayer.Scanner;
import com.wilki.tica.logicLayer.TopCode;
import com.wilki.tica.exceptions.TopCodeNotRecognisedException;
import com.wilki.tica.fragments.BusyDialogFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.wilki.tica.instructions.Instruction;

/**
 * Created by John Wilkie on 4/1/2017.
 * Activity for tangible interface. Used while attempting a task with the tangible interface.
 */

public class TangibleTaskActivity extends TaskActivity implements DialogClickListener {

    private Scanner codeScanner;
    private Camera cam;
    private PhotoTaken pictureCallback;
    private Bitmap cameraImage;
    private boolean sent;
    private BusyDialogFragment takingPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, getIntent(), InterfaceType.TANGIBLE, getApplicationContext());
        setContentView(R.layout.activity_tangible_task);

        // create fragment but don't show it yet.
        takingPicture = new BusyDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("message", "capturing codes");
        takingPicture.setArguments(bundle);

        codeScanner = new Scanner();

        // get camera instance
        cam = getCameraInstance();
        if(cam == null){
            System.out.println("Can't open camera");
        } else {
            pictureCallback = new PhotoTaken();
        }
        sent = false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == 25){
            if((action == KeyEvent.ACTION_UP || action == 1 || action == 0) && !sent){
                // tangible play button pressed.

                // play sound
                final MediaPlayer mp = MediaPlayer.create(this, R.raw.click);
                mp.start();

                // show capturing tangibles fragment
                takingPicture.show(getFragmentManager(), "capturing tangible codes");

                // take photo
                new CaptureImage().execute();
                sent = true;
            }else{
                sent = false;
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public void exitTask(View view){
        displayExitPopup();
    }

    @Override
    public void onPause(){
        super.onPause();
        if(cam != null){
            // release camera so other apps can use it.
            cam.stopPreview();
            cam.setPreviewCallback(null);
            cam.release();
            cam = null;
        }
    }

    /*
     * Gets an instance of the Camera class.
     * from - https://developer.android.com/guide/topics/media/camera.html
     */
    private Camera getCameraInstance(){
        Camera cam = null;
        try {
            cam = Camera.open(1); // attempt to get a Camera instance
        } catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return cam; // returns null if camera is unavailable
    }

    /**
     * Uses TopCode scanner to read tangible instructions used and converts them to a list of
     * instructions.
     * @return list of instructions read from the captured image.
     */
    protected List<Instruction> readInstructions() {
        if(cameraImage != null) {
            List<Instruction> foundInstructions = new ArrayList<>();
            cameraImage = rotate(cameraImage, -90);
            cameraImage = flip(cameraImage, true, false);
            List<TopCode> foundTags = codeScanner.scan(cameraImage);
            for(int i = 0; i < foundTags.size(); i++) {
                TopCode currentCode = foundTags.get(i);
                try {
                    foundInstructions.add(Instruction.matchTopCodeToInstruction(currentCode));
                } catch (TopCodeNotRecognisedException e){
                    // should worn user that a code was not recognised
                }
            }
            return foundInstructions;
        }
        return null;
    }

    /*
     * Takes a bitmap and rotates it the set number of degrees.
     * from - http://stackoverflow.com/questions/31925712/android-getting-an-image-from-gallery-comes-rotated/31927359#31927359
     */
    private Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /*
     * Takes a bitmap and flips it horizontally or vertically. Used to reverse the effect of taking
     * a photo in a mirror.
     * from - http://stackoverflow.com/questions/31925712/android-getting-an-image-from-gallery-comes-rotated/31927359#31927359
     */
    private Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    /*
     * Method called once the camera has taken a photo.
     */
    private class PhotoTaken implements Camera.PictureCallback{
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            cameraImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            List<Instruction> capturedInstructions = readInstructions();
            if(takingPicture != null){
                // Dismiss taking photo fragment
                takingPicture.dismiss();
            }
            processInstructions(capturedInstructions);
        }
    }

    /*
     * AsyncTask used to capture the image without freezing the UI thread.
     */
    private class CaptureImage extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            takePhoto();
            return null;
        }

        /*
         * Takes a photo using the Camera class.
         * http://stackoverflow.com/questions/20684553/how-to-take-pictures-from-the-camera-without-preview-when-my-app-starts
         */
        private void takePhoto(){
            if(cam != null){
                try {
                    cam.setPreviewTexture(new SurfaceTexture(10));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Camera.Parameters params = cam.getParameters();
                params.setPreviewSize(640, 480);
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                params.setPictureFormat(ImageFormat.JPEG);
                cam.setParameters(params);
                cam.startPreview();
                cam.takePicture(null, null, null, pictureCallback);
            }
        }
    }

}
