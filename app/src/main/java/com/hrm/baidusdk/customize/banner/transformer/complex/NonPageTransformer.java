package com.hrm.baidusdk.customize.banner.transformer.complex;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

/**
 * @author: Hrm
 * @description:
 * @data: 2020/11/29
 */
public class NonPageTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(@NonNull View page, float position) {
        page.setScaleX(0.999f);
    }

    public static final ViewPager.PageTransformer INSTANCE = new NonPageTransformer();
}
