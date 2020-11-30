package com.hrm.baidusdk.customize.banner.loader;

import android.content.Context;
import android.widget.ImageView;

/**
 * @author: Hrm
 * @description: ImageView加载器
 * @data: 2020/11/29
 */
public abstract class ImageLoader<T> implements BannerLoader<T, ImageView> {

    @Override
    public ImageView createView(Context context, int position) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }
}
