
package com.yahoo.mobile.android.auctionpost.fragments;

import android.app.Fragment;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yahoo.mobile.android.auctionpost.R;

public class ImageCaptureFragment extends Fragment {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Camera mCamera;

    // private CameraPreview mPreview;

    private boolean safeCameraOpen(int id) {
        boolean qOpened = false;

        try {
            releaseCameraAndPreview();
            mCamera = Camera.open(id);
            qOpened = (mCamera != null);
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }

        return qOpened;
    }

    private void releaseCameraAndPreview() {
        // mPreview.setCamera(null);
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    // /** Create a file Uri for saving an image or video */
    // private static Uri getOutputMediaFileUri(int type){
    // return Uri.fromFile(getOutputMediaFile(type));
    // }
    //
    //
    // /** Create a File for saving an image or video */
    // private static File getOutputMediaFile(int type){
    // // To be safe, you should check that the SDCard is mounted
    // // using Environment.getExternalStorageState() before doing this.
    // boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    // if (sdCardExist) {
    // Log.d("sdCard", "sdCard exists!");
    //
    // File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
    // Environment.DIRECTORY_PICTURES), "MaiMai");
    // // This location works best if you want the created images to be shared
    // // between applications and persist after your app has been uninstalled.
    //
    // // Create the storage directory if it does not exist
    // if (! mediaStorageDir.exists()){
    // if (! mediaStorageDir.mkdirs()){
    // Log.d("MyCameraApp", "failed to create directory");
    // return null;
    // }
    // }
    //
    // // Create a media file name
    // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    // File mediaFile;
    // if (type == MEDIA_TYPE_IMAGE){
    // mediaFile = new File(mediaStorageDir.getPath() + File.separator +
    // "IMG_"+ timeStamp + ".jpg");
    // } else if(type == MEDIA_TYPE_VIDEO) {
    // mediaFile = new File(mediaStorageDir.getPath() + File.separator +
    // "VID_"+ timeStamp + ".mp4");
    // } else {
    // return null;
    // }
    // return mediaFile;
    // }
    // else {
    // Log.d("sdCard", "sdCard not exists.");
    // return null;
    // }
    // }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("fragment", "ImageCaptureFragment onCreateView");

        View v;
        v = inflater.inflate(R.layout.fragment_image_capture, container, false);

        // create Intent to take a picture and return control to the calling application
        // Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Intent intent = new Intent();
        // intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
        // startActivity(intent);

        // fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        // intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        //
        // // start the image capture Intent
        // startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

        return v;
    }

}
