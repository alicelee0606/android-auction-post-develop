
package com.yahoo.mobile.android.auctionpost.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.yahoo.mobile.android.auctionpost.R;

public class PaymentDialogFragment extends DialogFragment {

    static PaymentDialogFragment newInstance(boolean[] checkedItems) {
        PaymentDialogFragment fragment = new PaymentDialogFragment();
        Bundle args = new Bundle();
        // TODO, set args
        args.putBooleanArray("checkedItem", checkedItems);
        fragment.setArguments(args);
        return fragment;
    }

    public interface PaymentListener {
        public void onPaymentUpdate(boolean[] checkedItems);
    }

    private PaymentListener mPaymentListener;

    public void setPaymentListener(PaymentListener listener) {
        this.mPaymentListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final boolean[] checkedItems = getArguments().getBooleanArray("checkedItem");
        final CharSequence[] paymentMethods = getActivity().getResources().getStringArray(R.array.payment_methods);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Payment");
        builder.setMultiChoiceItems(paymentMethods, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index, boolean isChecked) {
                // Toast.makeText(getActivity(), "index: " + index + " " + isChecked, Toast.LENGTH_SHORT).show();
                checkedItems[index] = isChecked;
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPaymentListener.onPaymentUpdate(checkedItems);
            }
        });
        return builder.create();
    }
}
