
package com.yahoo.mobile.android.auctionpost;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.yahoo.mobile.android.auctionpost.fragments.CameraColorFragment;

public class MediaDecorateActivity extends FragmentActivity {

    public static MediaDecorateActivity self;
    private int tagColor = 0;

    // private String path;
    // private String imagePath;
    // private int showType = 0;

    public void setTag(int color) {
        tagColor = color;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        self = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_decorate);
        
        Log.d("time", "MediaDecorate onCreate");
        
        Log.d("activity", "MediaDecorateActivity onCreate");
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setIcon(R.drawable.btn_cancel);
        
        Bundle bundle = this.getIntent().getExtras();
        /*
         * //String path = bundle.getString("path"); if (bundle.getString("path") != null) { showUri = bundle.getString("videoUri"); showType = 0; } else { showUri = bundle.getString("videoUri"); showType = 1; } Log.i("showType",Integer.toString(showType));
         */

        if (savedInstanceState == null) {
            CameraColorFragment fragment = new CameraColorFragment();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.media_decorate_container, fragment).commit();
        }
    }

    @Override
	public View onCreateView(String name, @NonNull Context context,
			@NonNull AttributeSet attrs) {
		// TODO Auto-generated method stub
    	Log.d("time", "MediaDecorate onCreateView");
		return super.onCreateView(name, context, attrs);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("time", "MediaDecorate onResume");
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.choose_color_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            // Toast.makeText(this, "pressed home button, back to MainPageActivity " + getFragmentManager().getBackStackEntryCount(), Toast.LENGTH_SHORT).show();
            // if (getFragmentManager().getBackStackEntryCount() <= 1) {
            // this.finish();
            // } else {
            // getFragmentManager().popBackStack();
            // }
            Intent intent = new Intent(MediaDecorateActivity.this, FFmpegRecorderActivity.class);
            startActivity(intent);
            return true;
        case R.id.choose_color_page_action_ok:
            Bundle sBundle = this.getIntent().getExtras();
            sBundle.putInt("tagColor", tagColor);
            Log.i("tagColor", Integer.toString(tagColor));
            intent = new Intent(MediaDecorateActivity.this, PostingPageActivity.class);
            intent.putExtras(sBundle);
            startActivity(intent);
            /*
             * CameraColorFragment fragment = (CameraColorFragment) getFragmentManager().findFragmentById(R.layout.fragment_camera_color); Bundle sBundle = new Bundle(); if (showType == 0) sBundle.putString("imageUri",showUri); else sBundle.putString("videoUri", showUri); sBundle.putInt("tagColor", tagColor); Intent intent = new Intent(MediaDecorateActivity.this, PostingPageActivity.class); intent.putExtras(sBundle); startActivity(intent);
             */
        default:
            return super.onOptionsItemSelected(item);
        }
    }

}
