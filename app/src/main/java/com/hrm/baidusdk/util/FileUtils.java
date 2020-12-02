package com.hrm.baidusdk.util;

import android.os.Environment;

import com.hrm.baidusdk.setting.MyApplication;

/**
 * @author: Hrm
 * @description:
 * @data: 2020/12/2
 */
public class FileUtils {
    public static String getAppRootFilePath() {
        return Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + MyApplication.getAppName() + "/";
    }
}
