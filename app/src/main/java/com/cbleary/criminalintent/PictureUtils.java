package com.cbleary.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Created by cbleary on 4/10/16.
 */
public class PictureUtils {
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight){
        //Read the dimensions of image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        //Calculate how much to scale image down
        int inSampleSize = 1;
        if(srcHeight > destHeight || srcWidth > destWidth){
            if(srcHeight < srcWidth){
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        //Read and creat final bitmap
        return BitmapFactory.decodeFile(path, options);
    }

    //Used for generating a rough estimate.  We don't know the dimn of the photoview until after
    //a layout pass happens (after onResume).  Just conservatively estimate.
    public static Bitmap getScaledBitmap(String path, Activity activity){
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return getScaledBitmap(path, size.x, size.y);
    }
}
