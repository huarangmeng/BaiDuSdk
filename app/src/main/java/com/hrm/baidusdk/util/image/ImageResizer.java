package com.hrm.baidusdk.util.image;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileDescriptor;

/**
 * @author: Hrm
 * @description: 图片压缩
 * @data: 2020/11/28
 */
public class ImageResizer {
    private static final String TAG = "ImageResizer";

    public ImageResizer() {
    }

    /**
     * 通过 inSampleSize 来缩放图片，达到更高效的加载
     *
     * @param resource
     * @param resId     资源ID
     * @param reqWidth  期望的宽度
     * @param reqHeight 期望的高度
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources resource, int resId,
                                                         int reqWidth, int reqHeight) {
        //First decode with inJusDecodeBounds = true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resource, resId, options);
        //Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        //Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resource, resId, options);
    }

    public Bitmap decodeSampledBitmapFromFileDescriptor(FileDescriptor fd,
                                                        int reqWidth, int reqHeight) {
        //First decode with inJusDecodeBounds = true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);
        //Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        //Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }

    /**
     * 计算 inSampleSize
     *
     * @param options
     * @param reqWidth  期望的宽度
     * @param reqHeight 期望的高度
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        //Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d(TAG, "origin,w= " + width + "h= " + height);
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            //Calculate the largest inSampleSize value that is a power of 2 andkeeps both
            //height and width larger than the requested height and width
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.d(TAG,"sampleSize: " + inSampleSize);
        return inSampleSize;
    }
}
