package com.hrm.baidusdk.customize.banner.loader;

import android.content.Context;
import android.view.View;


/**
 * @author: Hrm
 * @description:
 * @data: 2020/11/29
 */
public interface BannerLoader<T, V extends View> {

    /**
     * 加载 View
     *
     * @param context
     * @param entry
     * @param position  显示的位置
     * @param imageView
     */
    void loadView(Context context, BannerEntry entry, int position, V imageView);

    /**
     * 创建 View
     *
     * @param context
     * @param position
     * @return
     */
    V createView(Context context, int position);
}
