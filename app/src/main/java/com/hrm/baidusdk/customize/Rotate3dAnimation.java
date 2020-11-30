package com.hrm.baidusdk.customize;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;


/**
 * @author: Hrm
 * @description: View 动画。围绕 y 轴旋转同时沿着 Z 轴平移
 * @data: 2020/11/23
 */
public class Rotate3dAnimation extends Animation {
    //开始角度
    private final float mFromDegrees;
    //结束角度
    private final float mToDegrees;
    //动画围绕在 2D 空间的中心点执行，可以用 X 轴和 Y 轴坐标来定义这个中心点
    private final float mCenterX;
    private final float mCenterY;
    /**
     * 控制镜头景深，不需要的话赋值给 0 即可
     * mReverse 为 true，表示反方向，false 表示正方向
     */
    private final float mDepthZ;
    private final boolean mReverse;
    //用于辅助实现 3D 效果
    private Camera mCamera;

    public Rotate3dAnimation(float mFromDegrees, float mToDegrees, float mCenterX, float mCenterY, float mDepthZ, boolean mReverse) {
        this.mFromDegrees = mFromDegrees;
        this.mToDegrees = mToDegrees;
        this.mCenterX = mCenterX;
        this.mCenterY = mCenterY;
        this.mDepthZ = mDepthZ;
        this.mReverse = mReverse;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;
        float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);
        final float centerX = mCenterX;
        final float centerY = mCenterY;
        final Camera camera = mCamera;
        final Matrix matrix = t.getMatrix();
        //保存一次 camera 初始状态，用于 restore()
        camera.save();
        if (mReverse) {
            camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
        } else {
            camera.translate(0.0f, 0.0f, mDepthZ * (0.0f - interpolatedTime));
        }
        //围绕 Y 轴旋转 degrees 度
        camera.rotateY(degrees);
        //从camera 中去除矩阵，赋值给 matrix
        camera.getMatrix(matrix);
        //camera 恢复到初始状态，继续用于下次计算
        camera.restore();
        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
    }
}
