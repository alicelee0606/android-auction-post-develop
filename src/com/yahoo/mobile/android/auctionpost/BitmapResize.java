
package com.yahoo.mobile.android.auctionpost;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.util.Log;

public class BitmapResize {
    private static Bitmap mBitmap = null;
    private static String mPath = "";
    private static byte[] mData = null;

    private static void getScaledImage(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        int windowWidth = size.x;
        int windowHeight = size.y;
        Log.i("getScaledImage", windowWidth + " " +  windowHeight);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        float density = activity.getResources().getDisplayMetrics().density;
        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getHeight(), mBitmap.getHeight(), matrix, true);
//        mBitmap = Bitmap.createScaledBitmap(mBitmap, (int) (windowWidth*density), (int) (windowWidth*density), false);
        mBitmap = Bitmap.createScaledBitmap(mBitmap, 960, 960, false);
    }

    public static Bitmap getResizedBitmapFromUri(Activity activity, Uri uri) {
        return getResizedBitmapFromPath(activity, uri.getPath());
    }

    public static Bitmap getResizedBitmapFromPath(Activity activity, String path) {
        if (mPath.equals(path)) {
            Log.i("BitmapResize", "return previous Bitmap");
            return mBitmap;
        } else {
            Log.i("BitmapResize", "path:\n" + mPath + "\n" + path);
        }
        mPath = path;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;

        BitmapFactory.decodeFile(mPath, options);

        int imgHeight = options.outHeight;
        int imgWidth = options.outWidth;
        int limitHeight = activity.getResources().getInteger(R.integer.image_height);
        int limitWidth = activity.getResources().getInteger(R.integer.image_width);

        // get scale size to resize bitmap
        int scale = 1;
        while (imgHeight > limitHeight * scale || imgWidth > limitWidth * scale) {
            scale *= 2;
        }
        Log.i("BitmapResize", "imgHeight = " + imgHeight + " and imgWidth = " + imgWidth);
        Log.i("BitmapResize", "limitHeight = " + limitHeight + " and limitWidth = " + limitWidth);

        options.inSampleSize = scale;

        options.inJustDecodeBounds = false;
        mBitmap = BitmapFactory.decodeFile(mPath, options);

        getScaledImage(activity);
        Log.i("BitmapResize", "new Height = " + options.outHeight + ", new Width = " + options.outWidth);
        return mBitmap;
    }
    
    public static Bitmap getResizedBitmapFromByte(Activity activity, byte[] data) {
        if (mData!=null) {
            if (mData.equals(data)) {
            	Log.i("BitmapResize", "return previous Bitmap");
            	return mBitmap;
            }
        }
        
        mData = data;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;

        int imgHeight = options.outHeight;
        int imgWidth = options.outWidth;
        int limitHeight = activity.getResources().getInteger(R.integer.image_height);
        int limitWidth = activity.getResources().getInteger(R.integer.image_width);

        // get scale size to resize bitmap
        int scale = 1;
        while (imgHeight > limitHeight * scale || imgWidth > limitWidth * scale) {
            scale *= 2;
        }
        Log.i("BitmapResize", "imgHeight = " + imgHeight + " and imgWidth = " + imgWidth);
        Log.i("BitmapResize", "limitHeight = " + limitHeight + " and limitWidth = " + limitWidth);

        options.inSampleSize = scale;

        options.inJustDecodeBounds = false;
        mBitmap = BitmapFactory.decodeByteArray(mData, 0, mData.length, options);

        mData = null;
        
        getScaledImage(activity);
        Log.i("BitmapResize", "new Height = " + options.outHeight + ", new Width = " + options.outWidth);
        return mBitmap;
    }
}
