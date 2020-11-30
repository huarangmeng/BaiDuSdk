package com.hrm.baidusdk.util;

import android.content.Context;
import android.widget.Toast;

/**
 * @author: Hrm
 * @description: Toast工具类
 * @data: 2020/11/25
 */
public class ToastUtils {
    public static void makeTextShort(final Context context, final String str) {
        MainHandler.get(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void makeTextLong(final Context context, final String str) {
        MainHandler.get(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, str, Toast.LENGTH_LONG).show();
            }
        });
    }
}
