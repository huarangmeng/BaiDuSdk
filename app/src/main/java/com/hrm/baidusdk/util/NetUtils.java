package com.hrm.baidusdk.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * @author: Hrm
 * @description: 网络工具类
 * @data: 2020/11/20
 */
public class NetUtils {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT < 23) {
                //编译版本29以上弃用 NetworkInfo，版本在 23 的原因是 getActiveNetwork() 在 23以上生效
                NetworkInfo mWiFiNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (mWiFiNetworkInfo != null) {
                    if (mWiFiNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {//wifi
                        return true;
                    } else if (mWiFiNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {//移动数据
                        return true;
                    }
                }
            } else {
                Network network = connectivityManager.getActiveNetwork();
                if (network != null) {
                    NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(network);
                    if (nc != null) {
                        if (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {//wifi
                            return true;
                        } else if (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {//移动数据
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
