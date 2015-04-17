
package com.yahoo.mobile.android.auctionpost;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PhotoTakingActivity extends Activity {

    public static PhotoTakingActivity self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        self = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Log.d("activity", "PhotoTakingActivity onCreate => take photo");
        Intent intent = new Intent(PhotoTakingActivity.this, CameraActivity.class);
        startActivityForResult(intent, 0);
	}
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);

    	Log.d("activity", "PhotoTakingActivity onActivityResult");
    	switch (requestCode) {
    		case (0): {
    			if (resultCode == Activity.RESULT_OK) {
    				// TODO Switch tabs using the index

    				Bundle bundle = data.getExtras();
    				Log.d("camera", "imageUri = " + bundle.getString("imageUri") + " path = " + bundle.getString("path"));

    				Intent intent = new Intent(PhotoTakingActivity.this, MediaDecorateActivity.class);
    				intent.putExtras(bundle);
    				startActivity(intent);
    				finish();
    			}
    		}
    		default:
    		    finish();
    	}
    }
}
