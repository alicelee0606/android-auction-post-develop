package com.yahoo.mobile.android.auctionpost;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;
import com.yahoo.mobile.android.auctionpost.fragments.ProfilePageFragment;

public class ProfilePageActivity extends FragmentActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.profile_page_container, new ProfilePageFragment(),"Profile_Fragment").addToBackStack("Profile_Fragment").commit();
        }
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_page, menu);
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
            Intent intent = new Intent(ProfilePageActivity.this, StreamActivity.class);
            startActivity(intent);
            return true;
        case R.id.profile_page_log_out:
			ParseManager parseManger = ParseManager.getInstance(getApplicationContext());
			parseManger.currentLogOut();
            Intent intent1 = new Intent(ProfilePageActivity.this, StreamActivity.class);
            startActivity(intent1);
            /*
             * CameraColorFragment fragment = (CameraColorFragment) getFragmentManager().findFragmentById(R.layout.fragment_camera_color); Bundle sBundle = new Bundle(); if (showType == 0) sBundle.putString("imageUri",showUri); else sBundle.putString("videoUri", showUri); sBundle.putInt("tagColor", tagColor); Intent intent = new Intent(MediaDecorateActivity.this, PostingPageActivity.class); intent.putExtras(sBundle); startActivity(intent);
             */
        default:
            return super.onOptionsItemSelected(item);
        }
    }

}
