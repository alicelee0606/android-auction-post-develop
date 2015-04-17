
package com.yahoo.mobile.android.auctionpost.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.yahoo.mobile.android.auctionpost.R;

public class DeliveryDialogFragment extends DialogFragment {

    static DeliveryDialogFragment newInstance(boolean[] checkedItems) {
        DeliveryDialogFragment fragment = new DeliveryDialogFragment();
        Bundle args = new Bundle();
        // TODO, set args
        args.putBooleanArray("checkedItem", checkedItems);
        fragment.setArguments(args);
        return fragment;
    }

    public interface DeliveryListener {
        public void onDeliveryUpdate(boolean[] checkedItems);
    }

    private DeliveryListener mDeliveryListener;

    public void setDeliveryListener(DeliveryListener listener) {
        this.mDeliveryListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final boolean[] checkedItems = getArguments().getBooleanArray("checkedItem");
        final CharSequence[] deliveryMethods = getActivity().getResources().getStringArray(R.array.delivery_methods);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Delivery");
        builder.setMultiChoiceItems(deliveryMethods, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index, boolean isChecked) {
                // Toast.makeText(getActivity(), "index: " + index + " " + isChecked, Toast.LENGTH_SHORT).show();
                checkedItems[index] = isChecked;
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDeliveryListener.onDeliveryUpdate(checkedItems);
            }
        });
        return builder.create();
    }
}
