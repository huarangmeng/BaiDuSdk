package com.hrm.baidusdk.customize.banner.widget;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.hrm.baidusdk.customize.banner.constant.BannerConfig;

/**
 * @author: Hrm
 * @description: 控制 ViewPager 翻页的时间
 * @data: 2020/11/29
 */
public class BannerScroller extends Scroller {
    //ViewPager 翻页的时间
    private int mDuration = BannerConfig.SCROLL_TIME;

    public BannerScroller(Context context) {
        super(context);
    }

    public BannerScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public BannerScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    /**
     * 设置翻页时间
     *
     * @param mDuration
     */
    public void setDuration(int mDuration) {
        this.mDuration = mDuration;
    }
}
