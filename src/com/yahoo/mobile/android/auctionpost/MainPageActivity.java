
package com.yahoo.mobile.android.auctionpost;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.yahoo.mobile.android.auctionpost.ParseManager.IResultListener;
import com.yahoo.mobile.android.auctionpost.fragments.LogInPageFragment;

public class MainPageActivity extends FragmentActivity {

	private String userId,password;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        
        if ((loginPreferences.getString("userId", "") != null) && (loginPreferences.getString("password", "") != null)) {
        	userId = loginPreferences.getString("userId", "");
        	password = loginPreferences.getString("password", "");
        	
        	ParseManager parseManager = ParseManager.getInstance(getApplicationContext());
    	    parseManager.logIn(userId, password, new IResultListener() {
    		    @Override
    		    public void onSuccess() {
    		    	Toast.makeText(getApplicationContext(), "Automatically log in!", Toast.LENGTH_SHORT).show();
    		    	Intent newAct = new Intent();
    	            newAct.setClass(MainPageActivity.this, StreamActivity.class);
    	            startActivity(newAct);
    	            MainPageActivity.this.finish();

    		    }

    		    @Override
    		    public void onFail() {
    		    	Intent newAct = new Intent();
    	            newAct.setClass(MainPageActivity.this, StreamActivity.class);
    	            startActivity(newAct);
    	            MainPageActivity.this.finish();		
    		    }
    	    });
        } else {
        	Intent newAct = new Intent();
            newAct.setClass(MainPageActivity.this, StreamActivity.class);
            startActivity(newAct);
            MainPageActivity.this.finish();		
        }
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        // if (id == R.id.action_settings) {
        // return true;
        // }
        return super.onOptionsItemSelected(item);
    }
}
