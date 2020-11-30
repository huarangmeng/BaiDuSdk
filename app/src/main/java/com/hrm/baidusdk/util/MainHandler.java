package com.hrm.baidusdk.util;

import android.os.Handler;

import com.hrm.baidusdk.setting.MyApplication;

/**
 * @author: Hrm
 * @description:UI主线程
 * @data: 2020/11/22
 */
public class MainHandler {

    public static void get(Runnable runnable) {
        if (isRunUIThread()) {
            runnable.run();
        } else {
            executeTask(runnable);
        }
    }

    public static void get(Runnable runnable, long delayMillis) {
        executeTaskDelay(runnable, delayMillis < 0 ? 0 : delayMillis);
    }

    private static void executeTask(Runnable runnable) {
        getHandler().post(runnable);
    }

    private static void executeTaskDelay(Runnable runnable, long delayMillis) {
        getHandler().postDelayed(runnable, delayMillis);
    }

    private static Handler getHandler() {
        return MyApplication.getHandler();
    }

    /**
     * 判断程序是否在主线程中运行
     *
     * @return
     */
    public static boolean isRunUIThread() {
        //myPid 当前的进程 myUid 当前的用户 myTid 当前的一个线程
        return android.os.Process.myTid() == getMainThreadId();
    }

    private static int getMainThreadId() {
        return MyApplication.getMainThreadId();
    }
}
