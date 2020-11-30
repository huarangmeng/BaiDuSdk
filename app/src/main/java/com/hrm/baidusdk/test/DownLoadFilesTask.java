package com.hrm.baidusdk.test;

import android.os.AsyncTask;
import android.util.Log;


import java.net.URL;

/**
 * @author: Hrm
 * @description:
 * @data: 2020/11/28
 */
public class DownLoadFilesTask extends AsyncTask<URL, Integer, Long> {
    private static final String TAG = "DownLoadFilesTask";

    @Override
    protected Long doInBackground(URL... urls) {
        int count = urls.length;
        long totalSize = 0;
        for (int i = 0; i < count; i++) {
            totalSize += DownLoader.downLoadFile(urls[i]);
            publishProgress((int) ((i / (float) count) * 100));
            if (isCancelled()) {
                break;
            }
        }
        return totalSize;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Long aLong) {
        Log.d(TAG, "downloaded" + aLong + " bytes");
    }
}
