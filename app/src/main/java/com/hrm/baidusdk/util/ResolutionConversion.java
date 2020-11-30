package com.hrm.baidusdk.util;

import android.content.Context;

/**
 * @author: Hrm
 * @description: 手机分辨率转换
 * @data: 2020/11/29
 */
public class ResolutionConversion {
    /**
     * 根据手机的分辨率从 dp 的单位 转换成 px(像素)
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素)的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
