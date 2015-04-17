
package com.yahoo.mobile.android.auctionpost;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yahoo.mobile.android.auctionpost.camera.CameraPreview;
import com.yahoo.mobile.android.auctionpost.camera.CameraUtilities;

public class CameraActivity extends Activity implements OnClickListener {

	private final String TAG = "Camera Activity";
	private int mCurrentCamera;

	private Camera mCamera;
	private CameraPreview mPreview;
	private FrameLayout mPreviewContainer;
	
	private boolean mIsReleased = true;
	
	private byte[] mData;
	private Uri mUri;
	
	private RelativeLayout mProgressLayout;
	private ProgressBar mImageProcessingProgress;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		
		Log.d("activity", "CameraActivity onCreate");
		
		((ImageButton) findViewById(R.id.btn_take_photo))
				.setOnClickListener(this);
		((ImageButton) findViewById(R.id.btn_switch_to_video))
				.setOnClickListener(this);
				
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setIcon(R.drawable.btn_cancel);
		
        mPreviewContainer = (FrameLayout) findViewById(R.id.camera_preview);
        mProgressLayout = (RelativeLayout) findViewById(R.id.loadingPanel_camera);
        mImageProcessingProgress = (ProgressBar) findViewById(R.id.progress_bar_image_processing);

		if (!checkCameraHardware(this)) {
			return;
		}

		mCurrentCamera = CameraInfo.CAMERA_FACING_BACK;

		// Create an instance of Camera
		mCamera = getCameraInstance(mCurrentCamera);

		if (mCamera == null)
			return;
        
		initPreview();

	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
        	try {
				Camera c = Camera.open();
			} catch (Exception e) {
				final Runnable runnable = new Runnable() {
				    public void run() {
				    	try {
				    		Thread.sleep(500);
				    	} catch(Exception e) {
				    		
				    	}
				     }
				};
			}

        	finish();
            return true;
     
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
	public boolean getIsReleased() {
		return mIsReleased;
	}
	
	public void setIsReleased(boolean isReleased) {
		this.mIsReleased=isReleased;
	}

	private void switchCamera() {
		Log.d("Camera", "switchCamera()");

		if (mCamera != null && mIsReleased==false) {
			mCamera.stopPreview(); // stop preview
			mCamera.release(); // release previous camera
			mIsReleased = true;
		}

		if (mCurrentCamera == CameraInfo.CAMERA_FACING_BACK) {
			mCurrentCamera = CameraInfo.CAMERA_FACING_FRONT;
		} else {
			mCurrentCamera = CameraInfo.CAMERA_FACING_BACK;
		}

		// Create an instance of Camera
		mCamera = getCameraInstance(mCurrentCamera);

		if (mCamera == null)
			return;

		initPreview();
		// mCamera.startPreview();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("Camera", "onResume()");
		
		if (mCamera != null && mIsReleased==false) {
			mCamera.stopPreview(); // stop preview
			mCamera.release(); // release previous camera
			mIsReleased = true;
		}
		
		// Create an instance of Camera
		mCamera = getCameraInstance(mCurrentCamera);

		if (mCamera == null)
			return;

		initPreview();
		
		RelativeLayout panelLayout = (RelativeLayout) findViewById(R.id.camera_panel_container);
		RelativeLayout.LayoutParams panelParams = (RelativeLayout.LayoutParams) panelLayout.getLayoutParams();
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		TypedValue tv = new TypedValue();
		int actionBarHeight = 0;
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
		}
		
		panelParams.height = size.y - size.x - actionBarHeight;
	}

	private void initPreview() {
		Log.d("Camera", "initPreview");
		
		// Create Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this, mCamera);
		mPreviewContainer.removeAllViews();
		mPreviewContainer.addView(mPreview);
		mIsReleased = false;
		
	}
	

	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	/** Check if device has camera */
	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	private Camera getCameraInstance(int type) {
		Camera c = null;
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == type) {
				try {
					c = Camera.open(i); // attempt to get a Camera instance
				} catch (Exception e) {
					// Camera is not available
					showToast("相機裝置無法啟動");
				}
				break;
			}
		}
		Log.d("Camera", "getCameraInstance");
		mIsReleased = false;
		return c;
	}

	private PictureCallback mPicture = new PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			
			File pictureFile = CameraUtilities.getOutputMediaFile();
			if (pictureFile == null) {
				Log.d(TAG,
						"Error creating media file, check storage permissions.");
				showToast("拍攝照片失誤，請檢查存取空間");
				return;
			}

			try {
				
				Bitmap bitmap = BitmapResize.getResizedBitmapFromByte(CameraActivity.this, data);
				
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);

				byte[] dataCropped = stream.toByteArray();
				
				Log.d("size", "camera data = "+dataCropped.length);
				
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(dataCropped);
				
				mData = dataCropped;
				
				fos.close();

				Intent mediaScanIntent = new Intent(
						Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				mUri = Uri.fromFile(pictureFile);
				mediaScanIntent.setData(mUri);
				CameraActivity.this.sendBroadcast(mediaScanIntent);
				
				if (mUri != null) {
	        		Intent intent = new Intent();
	    			intent.setClass(CameraActivity.this, MediaDecorateActivity.class);
	    			
	    			Bundle bundle = new Bundle();
	    			bundle.putString("imagePath", mUri.getPath());
	    			bundle.putString("imageUri", mUri.toString());
	    			Log.d("uri", "Camera: imageUri="+mUri.toString());
	    			Log.d("path", "Camera: imagePath="+mUri.getPath());
	    			intent.putExtras(bundle);
	    			setResult(Activity.RESULT_OK, intent);
	    			Log.d("activity", "CameraActivity finish()");
	    			finish();
	    			mImageProcessingProgress.setVisibility(View.GONE);
	        	} else {
	        		Toast toast = Toast.makeText(CameraActivity.this, "請拍攝一張照片", Toast.LENGTH_SHORT);
	        		toast.setGravity(Gravity.CENTER, 0, 0);
	        		toast.show();
	        	}

			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			}
		}
	};

	ShutterCallback mShutterCallback = new ShutterCallback() {
		public void onShutter() {
			// do stuff like playing shutter sound here
		}
	};
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d("Camera", "onPause()");
		if (mCamera != null && mIsReleased==false) {
			mCamera.stopPreview();
//			mCamera.setPreviewCallback(null);
//			mPreview.getHolder().removeCallback(mPreview);
			mCamera.release(); // release the camera for other applications
			mCamera = null;
			mIsReleased = true;
		}
	}
	

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id==R.id.btn_take_photo) {
			
			if (mCamera != null) {
				View view = new View(this);
				view.setBackgroundColor(Color.BLACK);
				FrameLayout.LayoutParams paramsBlackScreen = new FrameLayout.LayoutParams(mPreview.getWidth(), mPreview.getHeight());
				mPreviewContainer.addView(view, paramsBlackScreen);

				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				params.topMargin = (int) (size.x/2);
				params.leftMargin = (int) (size.x/2);
				mProgressLayout.setLayoutParams(params);
				mImageProcessingProgress.setVisibility(View.VISIBLE);
			}
			
			Log.d(TAG, "camera takePicture");
			mCamera.takePicture(mShutterCallback, null, mPicture);
			
		} else if (id == R.id.btn_switch_to_video) {
			
			try {
				Camera c = Camera.open();
			} catch (Exception e) {
				final Runnable runnable = new Runnable() {
				    public void run() {
				    	try {
				    		Thread.sleep(500);
				    	} catch(Exception e) {
				    		
				    	}
				     }
				};
			}
			
			Log.d("activity", "photo to video");
			Intent intent = new Intent(CameraActivity.this, FFmpegRecorderActivity.class);
			startActivity(intent);
			finish();
		}
	}
	
//	public class ProgersssDialog extends Dialog {
//		private ImageView img;
//	        
//	    public ProgersssDialog(Context context) {
////	        super(context, R.style.progress_dialog);
//	    	super(context);
//	            
//	        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//	        View  view=inflater.inflate(R.layout.progress_dialog, null); 
//	        img=(ImageView) view.findViewById(R.id.progress_dialog_img);
//	        
//	        Animation anim=AnimationUtils.loadAnimation(context, R.anim.loading_dialog_progressbar);
//	        img.setAnimation(anim);
//	        
//	        setContentView(view);
//	        show();  
////	        dismiss();
//	    }
//	}

}
