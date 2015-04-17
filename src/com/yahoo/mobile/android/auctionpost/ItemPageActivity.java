
package com.yahoo.mobile.android.auctionpost;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.yahoo.mobile.android.auctionpost.fragments.ItemPageFragment;

public class ItemPageActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_page);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.item_page_container, new ItemPageFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item_page, menu);
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == android.R.id.home) {
            onBackPressed();
        }
        // if (id == R.id.action_settings) {
        // return true;
        // }
        return super.onOptionsItemSelected(item);
    }
}