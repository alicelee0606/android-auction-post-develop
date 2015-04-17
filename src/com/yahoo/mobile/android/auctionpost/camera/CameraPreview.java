
package com.yahoo.mobile.android.auctionpost.camera;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.yahoo.mobile.android.auctionpost.CameraActivity;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

	private final String TAG = "Camera Preview";
	
	private SurfaceHolder mHolder;
	private Camera mCamera;
	private Context mCameraContext;
	Size mPreviewSize;
	List<Size> mSupportedPreviewSizes;
	
	private long previousTime=0, currentTime=0;
	
	@SuppressWarnings("deprecation")
	public CameraPreview(Context context, Camera camera) {
		super(context);
		mCamera = camera;
		mCameraContext = context;
		
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		mSupportedPreviewSizes = mCamera.getParameters()
				.getSupportedPreviewSizes();
		
		setIfAutoFocusSupported();
		
	}
	
	public void initPreview() {
	
		if (mCamera == null)
			return;
		
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
		Log.d("CameraPreview", "in CameraPreview: initPreview()");
		
		mCamera.setParameters(parameters);
		mCamera.startPreview();
	}
	
	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it where
		// to draw.
		try {
			if (mCamera != null) {
				mCamera.setPreviewDisplay(holder);
				mCamera.setDisplayOrientation(90);
				Log.d("CameraPreview", "surfaceCreated: previewSize=" + mCamera.getParameters().getPreviewSize().height+"X"+mCamera.getParameters().getPictureSize().width);
			}
		} catch (IOException exception) {
			Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
		}
	}
	
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d("CameraPreview", "surfaceDestroyed");
		if (mCamera!=null && !((CameraActivity)mCameraContext).getIsReleased()) {
//			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			((CameraActivity)mCameraContext).setIsReleased(true);
			mCameraContext = null;
		}
	}
	
	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		Log.d("CameraPrevview", "getOptimalPreviewSize: w="+w+" h="+h);
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) h / w;
		if (sizes == null)
			return null;
		
		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;
		
		int targetHeight = h;
		
		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}
		
		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Log.d("CameraPreview", "surfaceChanged: w="+w+" h="+h);
		
		if (mHolder.getSurface() == null) {
			return;
		}
	
		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}
		
		if (mSupportedPreviewSizes != null) {
			mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, w, h);
			Log.d("CameraPreview", "surfaceChanged: mPreviewSize="+mPreviewSize.height+"X"+mPreviewSize.width);
		}
		
		// start preview with new settings
		try {
		
			mCamera.setPreviewDisplay(mHolder);
		
			Parameters params = mCamera.getParameters();
			params.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
			mCamera.setParameters(params);
			mCamera.startPreview();
		
			Log.d(TAG, "Preview Size: " + mPreviewSize.width + "X"
					+ mPreviewSize.height);
		
		} catch (Exception e) {
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}

		setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
            	// calculate the time difference of two continuous touch
            	// if they are too close, then skip this autofocus execution
            	currentTime = System.currentTimeMillis();
            	long diffTime = currentTime - previousTime;
            	Log.d("time", "currentTime="+currentTime+" diffTime="+diffTime);
            	
            	if (diffTime>500 && event.getAction() == MotionEvent.ACTION_DOWN && mCamera != null) {
            		mCamera.autoFocus(new AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            Log.d(TAG, "2. onAutoFocus = " + success);
                            if (success && mCamera!=null )
            		        {
            		            camera.setOneShotPreviewCallback(null);
            		            Log.i("CameraPreview", "autofocus success");

            		        } else {
            		        	Log.i("CameraPreview", "autofocus failed");
            		        }
                        }
                    });
                }
            	previousTime = currentTime;
                return false;
            }
        });
		
		
	}

	
	private void setIfAutoFocusSupported() {
		// get Camera parameters
		Camera.Parameters params = mCamera.getParameters();
		
		List<String> focusModes = params.getSupportedFocusModes();
		if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			mCamera.setParameters(params);
		}
	}
}
