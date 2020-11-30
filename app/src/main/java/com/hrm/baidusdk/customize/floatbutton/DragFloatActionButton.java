package com.hrm.baidusdk.customize.floatbutton;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;

import com.hrm.baidusdk.customize.floatbutton.DragFloatButtonClickListener;
import com.hrm.baidusdk.util.MainHandler;

import java.util.Timer;
import java.util.TimerTask;


/**
 * @author: Hrm
 * @description: 悬浮吸附按钮
 * @data: 2020/11/22
 */
public class DragFloatActionButton extends View {

    private static final String TAG = "DragFloatActionButton";
    private static final int LONG_PRESS_TIME = 300;

    private int parentHeight;
    private int parentWidth;

    private int width;
    private int height;

    private int lastX;
    private int lastY;

    private float normalAlpha = 0.9f;
    private float fadeAlpha = 0.3f;

    private int marginLeftAndRight = 10;
    private int marginTopAndBottom = 10;

    private boolean isDrag;
    private boolean isAlreadyDrag;
    private boolean isLongPress;
    private int slop;

    private DragFloatButtonClickListener mClickListener;

    private Timer timer;
    private TimerTask timerTask;

    public DragFloatActionButton(Context context) {
        super(context);
        slop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public DragFloatActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        slop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public DragFloatActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        slop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setOnClickListener(DragFloatButtonClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public void setNormalAlpha(float normalAlpha) {
        if (normalAlpha > 1.0f) {
            this.normalAlpha = 1.0f;
        } else this.normalAlpha = Math.max(normalAlpha, 0.0f);
    }

    public void setFadeAlpha(float fadeAlpha) {
        if (fadeAlpha > 1.0f) {
            this.fadeAlpha = 1.0f;
        } else this.fadeAlpha = Math.max(fadeAlpha, 0.0f);
    }

    /**
     * 解决 wrap_content 无效问题，重新测量宽高，规格未确定时，自动赋予确定大小
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = measureWidth(widthMeasureSpec);
        height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                this.setAlpha(normalAlpha);
                setPressed(true);
                isDrag = false;
                isAlreadyDrag = false;
                isLongPress = true;
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        isLongPress = true;
                        if (mClickListener != null) {
                            mClickListener.onLongClick();
                        }
                    }
                };
                timer.schedule(timerTask, LONG_PRESS_TIME);
                isLongPress = false;
                //父布局不要阻拦子布局的监听
                getParent().requestDisallowInterceptTouchEvent(true);
                lastX = rawX;
                lastY = rawY;
                if (getParent() != null) {
                    ViewGroup parent = (ViewGroup) getParent();
                    parentHeight = parent.getHeight();
                    parentWidth = parent.getWidth();
                }
                Log.d(TAG, "down" + "     isDrag:    " + isDrag);
                break;
            case MotionEvent.ACTION_MOVE:
                //如果不存在父类的宽高则无法拖动
                if (parentHeight <= 0.2 || parentWidth <= 0.2) {
                    isDrag = false;
                    break;
                }
                this.setAlpha(normalAlpha);
                int dx = rawX - lastX;
                int dy = rawY - lastY;
                //这里修复一些华为手机无法触发点击事件
                int distance = (int) Math.sqrt(dx * dx + dy * dy);
                slop = Math.min(slop, 3);
                if (distance == 0 || distance <= slop) {
                    if (!isAlreadyDrag()) {
                        isDrag = false;
                    }
                    Log.d(TAG, "move" + "   slop: " + slop + "    isDrag:   " + isDrag);
                    break;
                }
                isDrag = true;
                isAlreadyDrag = true;
                isLongPress = false;
                timer.cancel();
                timerTask.cancel();
                float x = getX() + dx;
                float y = getY() + dy;
                //检测是否到达边缘 左上右下
                x = x < marginLeftAndRight ? marginLeftAndRight : x > parentWidth - getWidth() - marginLeftAndRight ? parentWidth - getWidth() - marginLeftAndRight : x;
                y = getY() < marginTopAndBottom ? marginTopAndBottom : getY() + getHeight() > parentHeight ? parentHeight - getHeight() - marginTopAndBottom : y;
                setX(x);
                setY(y);
                lastX = rawX;
                lastY = rawY;
                Log.d(TAG, "move" + "    isDrag:   " + isDrag);
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "up" + "    isDrag:    " + isDrag);
                if (isDrag()) {
                    //恢复按压效果
                    setPressed(false);
                } else {
                    if (!isLongPress() && mClickListener != null) {
                        mClickListener.onClick();
                    }
                    autoFade();
                }
                timer.cancel();
                timerTask.cancel();
                moveHide(rawX);
                break;
        }
        //如果是拖拽则消耗事件，否则正常传递即可
        return isDrag() || super.onTouchEvent(event);
    }

    private boolean isLongPress() {
        return isLongPress;
    }

    private boolean isAlreadyDrag() {
        return isAlreadyDrag;
    }

    private boolean isDrag() {
        return isDrag;
    }

    /**
     * 吸附效果
     *
     * @param rawX
     */
    private void moveHide(int rawX) {
        if (rawX >= parentWidth / 2) {
            animate().setInterpolator(new DecelerateInterpolator())
                    .setDuration(500)
                    .xBy(parentWidth - getWidth() - getX() - marginLeftAndRight)
                    .start();
        } else {
            ObjectAnimator oa = ObjectAnimator.ofFloat(this, "x", getX(), marginLeftAndRight);
            oa.setInterpolator(new DecelerateInterpolator());
            oa.setDuration(500);
            oa.start();
        }
        autoFade();
    }

    /**
     * 当按钮没有被操作后，自动变透明，防止干扰视图
     */
    private void autoFade() {
        MainHandler.get(new Runnable() {
            @Override
            public void run() {
                AlphaAnimation alphaAnimation = new AlphaAnimation(normalAlpha, fadeAlpha);
                alphaAnimation.setDuration(1000);
                startAnimation(alphaAnimation);

                setAlpha(fadeAlpha);
            }
        }, 2000);
    }

    /**
     * 重新测量宽度，AT_MOST 时 宽度为 resultWidth = 200
     *
     * @param measureSpec
     * @return
     */
    private int measureWidth(int measureSpec) {
        int resultWidth = 200;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.EXACTLY:
                resultWidth = specSize;
                break;
            case MeasureSpec.AT_MOST:
                resultWidth = Math.min(resultWidth, specSize);
                break;
        }
        return resultWidth;
    }

    /**
     * 重新测量高度，AT_MOST 时 高度为 resultHeight = 200
     *
     * @param measureSpec
     * @return
     */
    private int measureHeight(int measureSpec) {
        int resultHeight = 200;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.EXACTLY:
                resultHeight = specSize;
                break;
            case MeasureSpec.AT_MOST:
                resultHeight = Math.min(resultHeight, specSize);
                break;
        }
        return resultHeight;
    }

}
