
package com.yahoo.mobile.android.auctionpost.fragments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yahoo.mobile.android.auctionpost.DetailSellPost;
import com.yahoo.mobile.android.auctionpost.HideSoftKeyboard;
import com.yahoo.mobile.android.auctionpost.ItemPageActivity;
import com.yahoo.mobile.android.auctionpost.MediaDecorateActivity;
import com.yahoo.mobile.android.auctionpost.ParseManager;
import com.yahoo.mobile.android.auctionpost.ParseManager.IPostListener;
import com.yahoo.mobile.android.auctionpost.R;

public class PostingPageFragment extends Fragment {

    boolean[] mPaymentCheckedItems; // doesn't work while declaring in callback function, must be global
    boolean[] mDeliveryCheckedItems;
    Bitmap mBitmap = null;
    String mName;
    String mDescription;
    int mPrice;
    Uri mImageUri;
    Uri mVideoUri = null;
    String mPath = "";
    int mTagColor = 0;
    boolean postingFlag;

    // String mObjectId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // mPath = getActivity().getIntent().getExtras().getString("path");

        Log.i("life", "onCreateView");
        postingFlag = false;

        mImageUri = Uri.parse(getActivity().getIntent().getExtras().getString("imageUri"));
        if (getActivity().getIntent().getExtras().getString("videoUri") != null) {
            mVideoUri = Uri.parse(getActivity().getIntent().getExtras().getString("videoUri"));
        }
        mTagColor = getActivity().getIntent().getExtras().getInt("tagColor");
        Log.i("mTagColor", Integer.toString(mTagColor));

        View rootView = inflater.inflate(R.layout.fragment_posting_page, container, false);
        setImageViewFromUri(rootView);
        setPaymentGridView(rootView);
        setDeliveryGridView(rootView);
        // initCatagorySpinner(rootView);
        // setImageOnClickListener(rootView);
        // setPaymentOnClickListener(rootView);
        // setDeliveryOnClickListener(rootView);

        HideSoftKeyboard.setHideSoftKeyboard(rootView, getActivity());
        return rootView;
    }

    public void setImageViewFromUri(View view) {
        ImageView imgv = (ImageView)view.findViewById(R.id.posting_page_image);
//        mBitmap = BitmapResize.getResizedBitmapFromUri(getActivity(), mImageUri);
        try {
        	mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mImageUri);
        } catch (Exception e) {
        	
        }
//        Log.d("PostingPageFragment", "mBitmap width="+mBitmap.getWidth()+" height="+mBitmap.getHeight());
        imgv.setImageBitmap(mBitmap);
        Log.i("size", "bitmap width="+mBitmap.getWidth()+" height="+mBitmap.getHeight());
        
        try {
        	Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    	    Point size = new Point();
    		display.getSize(size);
    		Log.i("size", "window width="+size.x+" height="+size.y);
			final double viewWidthToBitmapWidthRatio = (double)size.x / (double)mBitmap.getWidth();
			imgv.getLayoutParams().height = (int) (mBitmap.getHeight() * viewWidthToBitmapWidthRatio);
			Log.i("size", "img width="+imgv.getLayoutParams().width+" height="+imgv.getLayoutParams().height);
			
        } catch(Exception e) {
        	Log.e("color layout", "");
        }
    }

    private boolean checkBooleanArrayHasTrue(boolean[] array) {
        if (array == null)
            return false;
        for (boolean b : array)
            if (b == true)
                return true;
        return false;
    }

    public boolean onNextPressed() {
        if (postingFlag == true)
            return true;
        postingFlag = true;

        if (mImageUri == null) {
            postingFlag = false;
            Toast.makeText(getActivity(), "請選擇一張相片", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (((EditText)getActivity().findViewById(R.id.posting_page_name)).getText().toString().length() == 0) {
            postingFlag = false;
            Toast.makeText(getActivity(), "請輸入商品名稱", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (((EditText)getActivity().findViewById(R.id.posting_page_price)).getText().toString().length() == 0) {
            postingFlag = false;
            Toast.makeText(getActivity(), "請輸入商品價錢", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!checkBooleanArrayHasTrue(mPaymentCheckedItems)) {
            postingFlag = false;
            Toast.makeText(getActivity(), "請選擇付款方式", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!checkBooleanArrayHasTrue(mDeliveryCheckedItems)) {
            postingFlag = false;
            Toast.makeText(getActivity(), "請選擇交貨地點", Toast.LENGTH_SHORT).show();
            return false;
        }

        Activity activity = getActivity();
        Toast.makeText(activity, "上傳刊登中，請稍後", Toast.LENGTH_SHORT).show();
        
        mName = ((EditText)getActivity().findViewById(R.id.posting_page_name)).getText().toString();
        mDescription = ((EditText)getActivity().findViewById(R.id.posting_page_description)).getText().toString();
        mPrice = Integer.parseInt(((EditText)getActivity().findViewById(R.id.posting_page_price)).getText().toString());

        // pass data to activity and may be useful while calling next fragment (pass data to it or something)
        // mListener.onPostingPageUpdate(mName, mPrice, mDescription, mPaymentCheckedItems, mDeliveryCheckedItems, mUri);

        // but I found that I can call the next fragment directly... and the callback above become useless?
        // Bundle args = new Bundle();
        // args.putString("name", mName);
        // args.putInt("price", mPrice);
        // args.putString("description", mDescription);
        // args.putBooleanArray("payment", mPaymentCheckedItems);
        // args.putBooleanArray("delivery", mDeliveryCheckedItems);
        // args.putString("uri", mUri.toString());
        //
        // PreviewPageFragment fragment = new PreviewPageFragment();
        // fragment.setArguments(args);
        // // TODO, it failed to load preview fragment when using add()...but why?
        // getActivity().getFragmentManager().beginTransaction().replace(R.id.posting_page_container, fragment,
        // "PreviewPageFragment").addToBackStack("PreviewPageFragment").commit();
        // Log.i("backstack", getActivity().getFragmentManager().getBackStackEntryCount() + "");
        //
        // return true;

        DetailSellPost sp = new DetailSellPost();
        ParseManager parseManager = ParseManager.getInstance(activity.getApplicationContext());
        sp.setName(mName);
        sp.setPrice(mPrice);
        sp.setDescription(mDescription);
        List<Integer> tmpList;

        tmpList = new ArrayList<Integer>();
        for (int i = 0; i < mPaymentCheckedItems.length; i++)
            if (mPaymentCheckedItems[i])
                tmpList.add(i);
        sp.setPayment(tmpList);

        tmpList = new ArrayList<Integer>();
        for (int i = 0; i < mDeliveryCheckedItems.length; i++)
            if (mDeliveryCheckedItems[i])
                tmpList.add(i + 1);
        sp.setDelivery(tmpList);
        sp.setTagColor(mTagColor);

        sp.setOnShelf(true);
        File imageFile = new File(mImageUri.getPath());
        byte[] imageBytes;
        imageBytes = myBitmapToByte(mBitmap);
        Log.d("size", "posting data size = "+imageBytes.length);


        if (mVideoUri != null) {
            sp.setIsVd(true);

            File videoFile = new File(mVideoUri.getPath());
            byte[] videoBytes = myFileToByte(videoFile);
            sp.setShowName(videoFile.getName());
            sp.setShowData(videoBytes);

            sp.setCoverName(imageFile.getName());
            sp.setCoverData(imageBytes);
        } else {
            sp.setIsVd(false);

            sp.setShowName(imageFile.getName());
            sp.setShowData(imageBytes);
        }

        if (parseManager.checkLogIn()) {
            parseManager.createSellPost(sp, new IPostListener() {

                @Override
                public void onSuccess() {
                    // nothing needed here
                }

                @Override
                public void onFail() {
                    postingFlag = false;
                    Toast.makeText(getActivity(), "刊登失敗，請檢查網路連線並重試一次", Toast.LENGTH_SHORT).show();
                    Log.i("upload in preview page", "fail");
                }

                @Override
                public void onComplete(String objectId) {
                    postingFlag = false;
                    Toast.makeText(getActivity(), "刊登成功！", Toast.LENGTH_SHORT).show();
                    Log.i("upload in preview page", "success");
                    MediaDecorateActivity.self.finish();
                    // getActivity().finish();
                    Bundle bundle = new Bundle();

                    bundle.putString("ObjectId", objectId);
                    Intent intent = new Intent(getActivity(), ItemPageActivity.class);
                    intent.putExtras(bundle);
                    Log.d("activity", "from posting page to item page");
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            });
        } else {
            postingFlag = false;
            Toast.makeText(getActivity(), "未登入資料庫，請重啟程式以確保登入流程正確執行", Toast.LENGTH_SHORT).show();
            Log.i("parseManager", "not yet logged in.");
        }
        return true;
    }


    public byte[] myFileToByte(File file) {
        FileInputStream fis = null;
        try {
        	
        	Log.d("file size", Long.toString(file.length()));
        	
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        byte[] byteArray = new byte[(int)file.length()];
        try {
            fis.read(byteArray);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Log.d("byte array size", Integer.toString(byteArray.length));
        
        return byteArray;
    }

    public byte[] myBitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
        byte[] byteArray = stream.toByteArray();
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    public void setPaymentGridView(View view) {
        GridView gv = (GridView)view.findViewById(R.id.posting_page_payment_grid);
        final String[] methods = getActivity().getResources().getStringArray(R.array.payment_methods);
        gv.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.simple_textview, methods));
        gv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                Log.i("set OnItemClick", pos + " " + id + " ");
                if (mPaymentCheckedItems == null)
                    mPaymentCheckedItems = new boolean[methods.length];

                if (mPaymentCheckedItems[pos] == false) {
                    mPaymentCheckedItems[pos] = true;
                    v.setBackgroundResource(R.drawable.btn_posting_delivery_active);
                    ((TextView)v).setTextColor(Color.WHITE);
                } else {
                    mPaymentCheckedItems[pos] = false;
                    v.setBackgroundResource(R.drawable.btn_posting_devliery_default);
                    ((TextView)v).setTextColor(Color.BLACK);
                }
            }
        });
    }

    public void setDeliveryGridView(View view) {
        GridView gv = (GridView)view.findViewById(R.id.posting_page_delivery_grid);
        final String[] methods = getActivity().getResources().getStringArray(R.array.delivery_methods);
        gv.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.simple_textview, methods));
        gv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                Log.i("set OnItemClick", pos + " " + id + " ");
                if (mDeliveryCheckedItems == null)
                    mDeliveryCheckedItems = new boolean[methods.length];

                if (mDeliveryCheckedItems[pos] == false) {
                    mDeliveryCheckedItems[pos] = true;
                    v.setBackgroundResource(R.drawable.btn_posting_delivery_active);
                    ((TextView)v).setTextColor(Color.WHITE);
                } else {
                    mDeliveryCheckedItems[pos] = false;
                    v.setBackgroundResource(R.drawable.btn_posting_devliery_default);
                    ((TextView)v).setTextColor(Color. BLACK);
                }
            }
        });
    }

    // private PostingPageListener mListener;
    //
    // public interface PostingPageListener {
    // void onPostingPageUpdate(String name, int price, String description, boolean[] payment, boolean[] delivery, Uri uri);
    // }
    //
    // public void setListener(PostingPageListener listener) {
    // mListener = listener;
    // }

    // private final int REQUEST_PIC = 1188;
    // void setImageOnClickListener(View view) {
    // ImageView imgview = (ImageView)view.findViewById(R.id.posting_page_image);
    // imgview.setOnClickListener(new OnClickListener() {
    // @Override
    // public void onClick(View v) {
    // Intent intent = new Intent();
    // intent.setType("image/*");
    // intent.setAction(Intent.ACTION_PICK);
    // startActivityForResult(intent, REQUEST_PIC);
    // }
    // });
    // }

    // @Override
    // public void onActivityResult(int requestCode, int resultCode, Intent data) {
    // if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PIC) {
    // Uri uri = data.getData();
    // if (uri != null) {
    // ImageView imgview = (ImageView)getActivity().findViewById(R.id.posting_page_image);
    // mUri = uri;
    //
    // /*
    // * it seems that putting getResizedBitmapFromUri() in setImageBitmap() causes following 'motionrecognitionmanager' warning.
    // * .unregisterlistener / listener count = 0- 0. and image view will not display any image in this case. update: No, some other unknown
    // * problem causes the above warning.
    // */
    // Bitmap bitmap = BitmapResize.getResizedBitmapFromUri(getActivity(), mUri);
    // imgview.setImageBitmap(bitmap);
    // }
    // }
    // super.onActivityResult(requestCode, resultCode, data);
    // }

    // void initCatagorySpinner(View view) {
    // Spinner spinner = (Spinner)view.findViewById(R.id.posting_page_catagory);
    // ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.catagory_array,
    // android.R.layout.simple_spinner_item);
    // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // spinner.setAdapter(adapter);
    // }
    //
    // void setDeliveryOnClickListener(View view) {
    // Button button = (Button)view.findViewById(R.id.posting_page_delivery_button);
    // button.setOnClickListener(new OnClickListener() {
    // @Override
    // public void onClick(View v) {
    // final String[] deliveryMethods = getResources().getStringArray(R.array.delivery_methods);
    // if (mDeliveryCheckedItems == null) {
    // int len = deliveryMethods.length;
    // mDeliveryCheckedItems = new boolean[len];
    // }
    // DeliveryDialogFragment dialogFragment = DeliveryDialogFragment.newInstance(mDeliveryCheckedItems);
    // dialogFragment.setDeliveryListener(new DeliveryListener() {
    // @Override
    // public void onDeliveryUpdate(boolean[] checkedItems) {
    // String str = "";
    // mDeliveryCheckedItems = checkedItems;
    // for (int i = 0; i < checkedItems.length; i++) {
    // if (checkedItems[i]) {
    // if (str.length() != 0)
    // str += ", ";
    // str += deliveryMethods[i];
    // }
    // }
    // ((Button)getActivity().findViewById(R.id.posting_page_delivery_button)).setText(str);
    // }
    // });
    // dialogFragment.show(getActivity().getFragmentManager().beginTransaction(), "DeliveryDialog");
    // }
    // });
    // }
    //
    // void setPaymentOnClickListener(View view) {
    // Button button = (Button)view.findViewById(R.id.posting_page_payment_button);
    // button.setOnClickListener(new OnClickListener() {
    // @Override
    // public void onClick(View v) {
    // // Toast.makeText(getActivity(), "payment clicked!", Toast.LENGTH_SHORT).show();
    // final String[] paymentMethods = getResources().getStringArray(R.array.payment_methods);
    // if (mPaymentCheckedItems == null) {
    // int len = paymentMethods.length;
    // mPaymentCheckedItems = new boolean[len];
    // }
    // PaymentDialogFragment dialogFragment = PaymentDialogFragment.newInstance(mPaymentCheckedItems);
    // dialogFragment.setPaymentListener(new PaymentListener() {
    // @Override
    // public void onPaymentUpdate(boolean[] checkedItems) {
    // String str = new String("");
    // mPaymentCheckedItems = checkedItems;
    // for (int i = 0; i < checkedItems.length; i++)
    // if (checkedItems[i]) {
    // if (str.length() != 0)
    // str += ", ";
    // str += paymentMethods[i];
    // }
    // ((Button)getActivity().findViewById(R.id.posting_page_payment_button)).setText(str);
    // }
    // });
    // dialogFragment.show(getActivity().getFragmentManager().beginTransaction(), "PaymentDialog");
    // }
    // });
    // }
}
