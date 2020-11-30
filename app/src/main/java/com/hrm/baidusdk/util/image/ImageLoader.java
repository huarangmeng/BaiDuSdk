package com.hrm.baidusdk.util.image;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.bumptech.glide.disklrucache.DiskLruCache;

/**
 * @author: Hrm
 * @description:
 * @data: 2020/11/28
 */
public class ImageLoader {
    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCache mDiskLruCache;

    private Context mContext;

    public ImageLoader(Context context) {
        mContext = context.getApplicationContext();
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }
}
