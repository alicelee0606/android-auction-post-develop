
package com.yahoo.mobile.android.auctionpost.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.yahoo.mobile.android.auctionpost.PostingPageActivity;
import com.yahoo.mobile.android.auctionpost.R;

public class MainPageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_page, container, false);
        final Button btn = (Button)rootView.findViewById(R.id.button_to_posting_page);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostingPageActivity.class);
                // intent.putExtra(name, value);
                startActivity(intent);
            }
        });
        return rootView;
    }
}
