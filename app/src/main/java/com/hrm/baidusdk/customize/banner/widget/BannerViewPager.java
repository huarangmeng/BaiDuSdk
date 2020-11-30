package com.hrm.baidusdk.customize.banner.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author: Hrm
 * @description: 控制ViewPager 能否滚动，控制是否仿魅族效果的绘制
 * @data: 2020/11/29
 */
public class BannerViewPager extends ViewPager {

    private ArrayList<Integer> childCenterXAbs = new ArrayList<>();
    private SparseArray<Integer> childIndex = new SparseArray<>();

    /**
     * 能否滚动
     */
    private boolean mScrollable = true;

    /**
     * 能否仿魅族效果
     */
    private boolean mEnableMzEffects = false;

    public BannerViewPager(@NonNull Context context) {
        super(context);
    }

    public BannerViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mScrollable)
            return super.onTouchEvent(ev);
        else
            return false;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mScrollable)
            return super.onInterceptTouchEvent(ev);
        else
            return false;
    }

    /**
     * 重写这个方法来控制 子 View 的绘制顺序
     * 让中间的 View 压着两边 View，出现仿魅族效果
     *
     * @param childCount
     * @param n
     * @return 第n个位置的child 的绘制索引
     */
    @Override
    protected int getChildDrawingOrder(int childCount, int n) {
        if (mEnableMzEffects) {
            if (n == 0 || childIndex.size() != childCount) {
                childCenterXAbs.clear();
                childIndex.clear();
                int viewCenterX = getViewCenterX(this);
                for (int i = 0; i < childCount; i++) {
                    int indexAbs = Math.abs(viewCenterX - getViewCenterX(getChildAt(i)));
                    //两个距离相同，后来哪个做自增，从而保持abs不同
                    if (childIndex.get(indexAbs) != null) {
                        indexAbs++;
                    }
                    childCenterXAbs.add(indexAbs);
                    childIndex.append(indexAbs, i);
                }
                //1，0，2  0，1，2
                Collections.sort(childCenterXAbs);
            }
            //哪个 item 距离中心点远一些，就先 draw 它。（最近的就是中间放大的item，最后draw）
            return childIndex.get(childCenterXAbs.get(childCount - 1 - n));
        } else {
            return super.getChildDrawingOrder(childCount, n);
        }
    }

    /**
     * @param view
     * @return View 的中心点横坐标
     */
    private int getViewCenterX(View view) {
        int[] array = new int[2];
        view.getLocationOnScreen(array);
        return array[0] + view.getWidth() / 2;
    }

    /**
     * 设置能否滚动
     *
     * @param mScrollable
     */
    public void setScrollable(boolean mScrollable) {
        this.mScrollable = mScrollable;
    }

    /**
     * 仿魅族 Banner
     * 控制绘制顺序，让中间View覆盖左右两View
     *
     * @param mEnableMzEffects
     */
    public void setEnableMzEffects(boolean mEnableMzEffects) {
        this.mEnableMzEffects = mEnableMzEffects;
    }
}
