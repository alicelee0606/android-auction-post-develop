
package com.yahoo.mobile.android.auctionpost.fragments;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yahoo.mobile.android.auctionpost.BitmapResize;
import com.yahoo.mobile.android.auctionpost.DetailSellPost;
import com.yahoo.mobile.android.auctionpost.ParseManager;
import com.yahoo.mobile.android.auctionpost.R;

public class PreviewPageFragment extends Fragment {
    boolean[] mPayment;
    boolean[] mDelivery;
    String mName;
    String mDescription;
    int mPrice;
    Uri mUri;
    Bitmap mBitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        (getActivity().findViewById(R.id.posting_page_action_ok)).setVisibility(View.INVISIBLE);
        (getActivity().findViewById(R.id.posting_page_action_ok)).setClickable(false);

        mName = getArguments().getString("name");
        mPrice = getArguments().getInt("price");
        mDescription = getArguments().getString("description");
        mPayment = getArguments().getBooleanArray("payment");
        mDelivery = getArguments().getBooleanArray("delivery");
        mUri = Uri.parse(getArguments().getString("uri"));
        Log.i("test", mUri.getPath());

        View rootView = inflater.inflate(R.layout.fragment_preview_page, container, false);
        setSubmitButtonOnClickListener(rootView);
        setPreviewLayout(rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        (getActivity().findViewById(R.id.posting_page_action_ok)).setVisibility(View.VISIBLE);
        (getActivity().findViewById(R.id.posting_page_action_ok)).setClickable(true);
        super.onDestroyView();
    }

    public void setPreviewLayout(View view) {
        ((TextView)view.findViewById(R.id.item_page_name)).setText(mName);
        ((TextView)view.findViewById(R.id.item_page_price)).setText("" + mPrice);
        ((TextView)view.findViewById(R.id.item_page_description)).setText(mDescription);
        /*
         * it seems that putting getResizedBitmapFromUri() in setImageBitmap() causes following 'motionrecognitionmanager' warning. .unregisterlistener / listener count = 0- 0. and image view will not display any image in this case.
         */
        mBitmap = BitmapResize.getResizedBitmapFromUri(getActivity(), mUri);
        ((ImageView)view.findViewById(R.id.item_page_image)).setImageBitmap(mBitmap);

        CharSequence[] paymentMethods = getActivity().getResources().getStringArray(R.array.payment_methods);
        CharSequence[] deliveryMethods = getActivity().getResources().getStringArray(R.array.delivery_methods);

        String paymentStr = "";
        for (int i = 0; i < mPayment.length; i++) {
            if (mPayment[i]) {
                if (paymentStr.length() != 0)
                    paymentStr += "、";
                paymentStr += paymentMethods[i];
            }
        }
//        ((TextView)view.findViewById(R.id.item_page_payment)).setText(paymentStr);

        String deliveryStr = "";
        for (int i = 0; i < mDelivery.length; i++) {
            if (mDelivery[i]) {
                if (deliveryStr.length() != 0)
                    deliveryStr += "、";
                deliveryStr += deliveryMethods[i];
            }
        }
        ((TextView)view.findViewById(R.id.item_page_delivery)).setText(deliveryStr);

        return;
    }

    public void setSubmitButtonOnClickListener(View view) {
        final Button btn = (Button)view.findViewById(R.id.preview_page_submit_button);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("URI path", mUri.getPath());
                btn.setClickable(false);
                Activity activity = getActivity();

                Toast.makeText(activity, "Uploading, please wait.", Toast.LENGTH_SHORT).show();

                DetailSellPost sp = new DetailSellPost();
                ParseManager parseManager = ParseManager.getInstance(activity.getApplicationContext());
                sp.setName(mName);
                sp.setPrice(mPrice);
                sp.setDescription(mDescription);
                sp.setIsVd(false);
                List<Integer> tmpList;

                tmpList = new ArrayList<Integer>();
                for (int i = 0; i < mPayment.length; i++)
                    if (mPayment[i])
                        tmpList.add(i);
                sp.setPayment(tmpList);

                tmpList = new ArrayList<Integer>();
                for (int i = 0; i < mDelivery.length; i++)
                    if (mDelivery[i])
                        tmpList.add(i);
                sp.setDelivery(tmpList);

                // get img type
                ContentResolver cR = activity.getContentResolver();
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                String type = mime.getExtensionFromMimeType(cR.getType(mUri));
                sp.setShowName("temp." + type);

                byte[] data = null;
                data = myBitmapToByte(mBitmap);
                sp.setShowData(data);

                Log.i("data size", "" + data.length);
                /*
                 * if (parseManager.checkLogIn()) { parseManager.createSellPost(sp, new IResultListener() {
                 * @Override public void onSuccess() { Toast.makeText(getActivity(), "Post successed", Toast.LENGTH_SHORT).show(); Log.i("upload in preview page", "success"); getActivity().finish(); }
                 * @Override public void onFail() { btn.setClickable(true); Toast.makeText(getActivity(), "Post failed, please try again.", Toast.LENGTH_SHORT).show(); Log.i("upload in preview page", "fail"); } }); } else { Toast.makeText(getActivity(), "parseManager not yet logged in.", Toast.LENGTH_SHORT).show(); Log.i("parseManager", "not yet logged in."); }
                 */

            }
        });
    }

    public byte[] myBitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    // not used anymore
    public byte[] myUriToByte(Uri uri) throws IOException {

        InputStream input = getActivity().getContentResolver().openInputStream(mUri);

        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = input.read(buffer)) != -1)
            byteBuffer.write(buffer, 0, len);

        return byteBuffer.toByteArray();
    }
}
