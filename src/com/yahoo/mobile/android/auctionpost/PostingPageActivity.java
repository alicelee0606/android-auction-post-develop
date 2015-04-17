
package com.yahoo.mobile.android.auctionpost;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.yahoo.mobile.android.auctionpost.fragments.PostingPageFragment;

public class PostingPageActivity extends FragmentActivity {

    // boolean[] mPayment;
    // boolean[] mDelivery;
    // String mName;
    // String mDescription;
    // int mPrice;
    // Uri mUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting_page);

        Log.d("activity", "PostingPageActivity onCreate");

        ParseManager parseManager = ParseManager.getInstance(this.getApplicationContext());

        if (savedInstanceState == null) {
            // I have to add tags in both add() and addToBackStack(), or it may crash when next page button is pressed.
            // But this code seems silly...
            PostingPageFragment fragment = new PostingPageFragment();

            getFragmentManager().beginTransaction().add(R.id.posting_page_container, fragment, "PostingPageFragment").addToBackStack("PostingPageFragment").commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.posting_page, menu);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setIcon(R.drawable.btn_cancel);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            if (getFragmentManager().getBackStackEntryCount() <= 1) {
                this.finish();
            } else {
                getFragmentManager().popBackStack();
            }
            return true;
        case R.id.posting_page_action_ok:
            FragmentManager fragmentManager = getFragmentManager();
            String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
            if (fragmentTag == "PostingPageFragment") {
                PostingPageFragment f = (PostingPageFragment)fragmentManager.findFragmentByTag(fragmentTag);
                if (f.onNextPressed()) {
                }
            }
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
