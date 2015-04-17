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

import com.yahoo.mobile.android.auctionpost.ParseManager;
import com.yahoo.mobile.android.auctionpost.ParseManager.IResultListener;
import com.yahoo.mobile.android.auctionpost.FFmpegRecorderActivity;
import com.yahoo.mobile.android.auctionpost.ProfilePageActivity;
import com.yahoo.mobile.android.auctionpost.R;
import com.yahoo.mobile.android.auctionpost.StreamActivity;

public class SignUpPageFragment extends Fragment {
	
	private int fromAction;
	private String userId,password,email;
    private Button signUpOk;
    private EditText editTextUserId,editTextPassword,editTextEmail;
    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogIn;
    private TextView logIn;
    
    public static SignUpPageFragment newInstance(int index) {
        SignUpPageFragment f = new SignUpPageFragment();
        Bundle args = new Bundle();
        args.putInt("fromAction", index);
        f.setArguments(args);
        return f;
    }
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_signup_page, container, false);
        return rootView;
    }
	
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	fromAction = getArguments().getInt("fromAction", 0);
        
        signUpOk = (Button) view.findViewById(R.id.sign_up_ok);
        editTextUserId = (EditText) view.findViewById(R.id.new_user_id);
        editTextPassword = (EditText) view.findViewById(R.id.new_password);
        editTextEmail = (EditText) view.findViewById(R.id.new_email);
        saveLoginCheckBox = (CheckBox) view.findViewById(R.id.save_sign_up);
        logIn = (TextView) view.findViewById(R.id.to_log_in);
        
        signUpOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				 if (v == signUpOk) {
					   
			            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			            imm.hideSoftInputFromWindow(editTextUserId.getWindowToken(), 0);
			            imm.hideSoftInputFromWindow(editTextPassword.getWindowToken(), 0);
			            imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);

			            userId = editTextUserId.getText().toString();
			            password = editTextPassword.getText().toString();
			            email = editTextEmail.getText().toString();
			            

			            if (saveLoginCheckBox.isChecked()) {
			                loginPrefsEditor.putBoolean("saveLogIn", true);
			                loginPrefsEditor.putString("userId", userId);
			                loginPrefsEditor.putString("password", password);
			                loginPrefsEditor.commit();
			            }

			            ParseManager mParseManager = ParseManager.getInstance(getActivity().getApplicationContext());
			            mParseManager.signUp(userId, password, email, new IResultListener () {

							@Override
							public void onSuccess() {
								Toast.makeText(getActivity(), "Sign up successfully! Automatically logging in...", Toast.LENGTH_SHORT).show();
								doLogIn(fromAction,userId,password);
							}

							@Override
							public void onFail() {
								Toast.makeText(getActivity(), "Sign up failed.Please try again!", Toast.LENGTH_SHORT).show();								
							}
			            	
			            });
			        }
			    
			}
		});
        
        logIn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				 if (v == logIn) {
					 LogInPageFragment logInPageFragment = LogInPageFragment.newInstance(fromAction);
					 FragmentManager fragmentManager = getFragmentManager();
					 FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
					 fragmentTransaction.replace(R.id.stream_page_container, logInPageFragment);
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
				   getActivity().finish();
			    } else if (fromAction == 1) {
			       Intent newAct = new Intent();
			       newAct.setClass(getActivity(), ProfilePageActivity.class);
			       startActivity(newAct);
				   getActivity().finish();
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
