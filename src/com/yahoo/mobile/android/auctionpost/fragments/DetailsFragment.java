
package com.yahoo.mobile.android.auctionpost.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailsFragment extends Fragment {
    public static DetailsFragment newInstance(int index) {
        DetailsFragment f = new DetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView text = new TextView(getActivity());
        text.setText("item selected? CHOICE_MODE_SINGLE");
        return text;
    }
}
