package com.hrm.baidusdk.setting;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * @author: Hrm
 * @description: 权限申请
 * @data: 2020/11/20
 */
public class Permission {
    private static final String[] permissionString = {Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.SYSTEM_ALERT_WINDOW};

    private Context context;

    public Permission(Context context) {
        this.context = context;
    }

    public void checkPermission() {
        int targetSdkVersion = 0;
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                boolean isAllGranted = checkPermissionAllGranted();
                if (isAllGranted) {
                    return;
                }
                ActivityCompat.requestPermissions((Activity) context,
                        permissionString, 1);
            }
        }
    }

    private boolean checkPermissionAllGranted() {
        for (String permission : Permission.permissionString) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}
