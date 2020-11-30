package com.hrm.baidusdk.zhihu;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hrm.baidusdk.R;
import com.hrm.baidusdk.bean.QuestionBean;
import com.hrm.baidusdk.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Hrm
 * @description: 常用编号页
 * @data: 2020/11/25
 */
public class CommonQuestionActivity extends AppCompatActivity {
    private static final String TAG = "CommonQuestionActivity";

    private RecyclerView mRecycleView;
    private QuestionAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<QuestionBean> questions = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_question);

        initView();
    }

    private void initView() {
        mRecycleView = (RecyclerView) findViewById(R.id.recycle_view);
        mRecycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new QuestionAdapter();
        copyItemNumber();
        mRecycleView.setAdapter(mAdapter);
    }

    /**
     * 单击 item 复制编号
     */
    private void copyItemNumber() {
        mAdapter.setOnItemClickListener(new QuestionAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                String question = mAdapter.getQuestion(position);
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText(null, question));
                if (cm.hasPrimaryClip()) {
                    cm.getPrimaryClip().getItemAt(0).getText();
                }
                ToastUtils.makeTextShort(getApplicationContext(), "复制成功: " + question);
            }
        });
    }
}
