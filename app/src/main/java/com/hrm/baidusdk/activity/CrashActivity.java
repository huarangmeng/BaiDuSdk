package com.hrm.baidusdk.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hrm.baidusdk.R;

/**
 * @author: Hrm
 * @description: Crash模拟测试
 * @data: 2020/11/20
 */
public class CrashActivity extends AppCompatActivity {
    private static final String TAG = "CrashActivity";

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);

        initView();
    }

    private void initView(){
        mButton = (Button) findViewById(R.id.crash_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                throw new RuntimeException("自定义异常：这是自己抛出的异常");
            }
        });
    }
}