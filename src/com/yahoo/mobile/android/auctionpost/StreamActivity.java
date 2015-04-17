
package com.yahoo.mobile.android.auctionpost;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.yahoo.mobile.android.auctionpost.fragments.LogInPageFragment;

public class StreamActivity extends Activity {
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_page);
        
        ImageButton ibPost = (ImageButton)findViewById(R.id.btn_post);
        ibPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            	ParseManager parseManager = ParseManager.getInstance(getApplicationContext());
            	if (parseManager.checkLogIn()) {
                    Intent intent = new Intent();
                    intent.setClass(StreamActivity.this, FFmpegRecorderActivity.class);
                    startActivity(intent);
//                    StreamActivity.this.finish();
            	} else {
            		LogInPageFragment logInPageFragment = LogInPageFragment.newInstance(0);
					FragmentManager fragmentManager = getFragmentManager();
					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
					fragmentTransaction.replace(R.id.stream_page_container, logInPageFragment);
					fragmentTransaction.addToBackStack(null);
					fragmentTransaction.commit();   
            	}
            
            }
        });
    }

    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		Log.d("StreamActivity onResume","");
	}
    
    
     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
    	 MenuInflater inflater = getMenuInflater();
    	 inflater.inflate(R.menu.stream_page, menu);
    	 return true;
     }
    
     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
     
    	 switch (item.getItemId()) {
    	 case android.R.id.home:
    		 break;
    	 case R.id.btn_to_profile:
    		 
    		 ParseManager parseManager = ParseManager.getInstance(getApplicationContext());
    		 if (parseManager.checkLogIn()) {
    			 Intent intent = new Intent(StreamActivity.this, ProfilePageActivity.class);
                 startActivity(intent);
//                 StreamActivity.this.finish();
         	 } else {
         		 LogInPageFragment logInPageFragment = LogInPageFragment.newInstance(1);
				 FragmentManager fragmentManager = getFragmentManager();
				 FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				 fragmentTransaction.replace(R.id.stream_page_container, logInPageFragment);
				 fragmentTransaction.addToBackStack(null);
				 fragmentTransaction.commit();   
         	 }
    	 
    	 }
    	 
    	 return true;
     }

}
