package com.yahoo.mobile.android.auctionpost.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yahoo.mobile.android.auctionpost.FFmpegRecorderActivity;
import com.yahoo.mobile.android.auctionpost.ParseManager;
import com.yahoo.mobile.android.auctionpost.ParseManager.IResultListener;
import com.yahoo.mobile.android.auctionpost.ProfilePageActivity;
import com.yahoo.mobile.android.auctionpost.R;

public class LogInPageFragment extends Fragment {
	
	private int fromAction;
	private String userId,password;
    private Button logInOk;
    private EditText editTextUserId,editTextPassword;
    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogIn;
    private TextView signUp;
    
    public static LogInPageFragment newInstance(int index) {
        LogInPageFragment f = new LogInPageFragment();
        Bundle args = new Bundle();
        args.putInt("fromAction", index);
        f.setArguments(args);
        return f;
    }
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_page, container, false);
        return rootView;
    }
	
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	
    	fromAction = getArguments().getInt("fromAction", 0);
    	
        logInOk = (Button) view.findViewById(R.id.log_in_ok);
        editTextUserId = (EditText) view.findViewById(R.id.edit_user_id);
        editTextPassword = (EditText) view.findViewById(R.id.edit_password);
        saveLoginCheckBox = (CheckBox) view.findViewById(R.id.save_log_in);
        signUp = (TextView) view.findViewById(R.id.to_sign_up);
        
        logInOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				 if (v == logInOk) {
			            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			            imm.hideSoftInputFromWindow(editTextUserId.getWindowToken(), 0);
			            imm.hideSoftInputFromWindow(editTextPassword.getWindowToken(), 0);

			            userId = editTextUserId.getText().toString();
			            password = editTextPassword.getText().toString();

			            if (saveLoginCheckBox.isChecked()) {
			                loginPrefsEditor.putBoolean("saveLogIn", true);
			                loginPrefsEditor.putString("userId", userId);
			                loginPrefsEditor.putString("password", password);
			                loginPrefsEditor.commit();
			            } else {
			                loginPrefsEditor.clear();
			                loginPrefsEditor.commit();
			            }

			            doLogIn(fromAction, userId,password);
			        }
			    
			}
		});
        
       
        loginPreferences = getActivity().getSharedPreferences("loginPrefs", getActivity().MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogIn = loginPreferences.getBoolean("saveLogIn", false);
        if (saveLogIn == true) {
            editTextUserId.setText(loginPreferences.getString("userId", ""));
            editTextPassword.setText(loginPreferences.getString("password", ""));
            saveLoginCheckBox.setChecked(true);
        }
        
        signUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				 if (v == signUp) {
					 SignUpPageFragment signUpFragment = SignUpPageFragment.newInstance(fromAction);
					 FragmentManager fragmentManager = getFragmentManager();
					 FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
					 fragmentTransaction.replace(R.id.stream_page_container, signUpFragment);
					 fragmentTransaction.addToBackStack(null);
					 fragmentTransaction.commit();
			     }
			}
		});
    }
	
	public void doLogIn(final int fromAction, String userId, String password) {
	    ParseManager parseManager = ParseManager.getInstance(getActivity().getApplicationContext());
	    parseManager.logIn(userId, password, new IResultListener() {
		    @Override
		    public void onSuccess() {
		    	Toast.makeText(getActivity(), "Log in successfully!", Toast.LENGTH_SHORT).show();
			
			    if (fromAction == 0) {
			       Intent newAct = new Intent();
			       newAct.setClass(getActivity(), FFmpegRecorderActivity.class);
			       startActivity(newAct);
//				   getActivity().finish();
			    } else if (fromAction == 1) {
			       Intent newAct = new Intent();
			       newAct.setClass(getActivity(), ProfilePageActivity.class);
			       startActivity(newAct);
//				   getActivity().finish();
			    } else {
			    }
		    }

		    @Override
		    public void onFail() {
		    	Toast.makeText(getActivity(), "Log in faied.Please try again!", Toast.LENGTH_SHORT).show();
		    }
	    });
	}

}
