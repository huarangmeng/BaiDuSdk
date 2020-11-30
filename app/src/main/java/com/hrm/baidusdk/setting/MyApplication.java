package com.hrm.baidusdk.setting;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

/**
 * @author: Hrm
 * @description:
 * @data: 2020/11/20
 */
public class MyApplication extends Application {

    private Context mContext;

    private static Handler mHandler;

    private static int mMainThreadId;

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);

        mContext = getApplicationContext();

        mHandler = new Handler();

        mMainThreadId = android.os.Process.myTid();
    }

    public Context getContext() {
        return mContext;
    }

    public static Handler getHandler() {
        return mHandler;
    }

    public static int getMainThreadId() {
        return mMainThreadId;
    }
}
