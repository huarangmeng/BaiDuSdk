package com.hrm.baidusdk.zhihu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.hrm.baidusdk.R;
import com.hrm.baidusdk.customize.floatbutton.DragFloatActionButton;
import com.hrm.baidusdk.customize.floatbutton.DragFloatButtonClickListener;
import com.hrm.baidusdk.util.MainHandler;
import com.hrm.baidusdk.util.StringUtils;
import com.hrm.baidusdk.util.ThreadTask;
import com.hrm.baidusdk.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author: Hrm
 * @description:
 * @data: 2020/11/26
 */
public class ViewZhiHuAnswerPicture extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ViewZhiHuAnswerPicture";

    private ImageView imageView;

    private DragFloatActionButton queryQuestionButton;
    private Button previousButton, nextButton;
    private Button cleanButton;
    private Button commonQuestionButton;

    private EditText editText;

    private String question;

    private int curIndex;
    private static int maxPictureIndex;

    private int offset = 0;
    private int limit = 0;

    private List<String> pictureUrl = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_zhihu_answer_picture);

        initData();

        initView();
    }

    private void initData() {
        question = "31123603";
        ThreadTask.getInstance().executorNetThread(new Runnable() {
            @Override
            public void run() {
                HttpClientUtils httpClientUtils = new HttpClientUtils(question);
                pictureUrl = httpClientUtils.getImageUrl();
                Log.d(TAG, String.valueOf(pictureUrl.size()));
                MainHandler.get(new Runnable() {
                    @Override
                    public void run() {
                        previousButton.setClickable(true);
                        nextButton.setClickable(true);
                        Glide.with(getApplicationContext()).load(pictureUrl.get(curIndex)).error(R.drawable.image).into(imageView);
                    }
                });
                maxPictureIndex = pictureUrl.size() == 0 ? 0 : pictureUrl.size() - 1;
            }
        });
        curIndex = 0;
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.image);
        queryQuestionButton = (DragFloatActionButton) findViewById(R.id.query_question_button);
        queryQuestionButton.setOnClickListener(dragFloatButtonClickListener);

        previousButton = (Button) findViewById(R.id.previous_picture_button);
        nextButton = (Button) findViewById(R.id.next_picture_button);
        previousButton.setClickable(false);
        nextButton.setClickable(false);
        previousButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        cleanButton = (Button) findViewById(R.id.clearButton);
        cleanButton.setOnClickListener(this);

        commonQuestionButton = (Button) findViewById(R.id.common_question_button);
        commonQuestionButton.setOnClickListener(this);

        editText = (EditText) findViewById(R.id.editText);
    }

    private void reloadQuestion() {
        String tempQuestion = editText.getText().toString().trim();
        if (!StringUtils.isEmpty(tempQuestion)) {
            question = tempQuestion;
        }
        String pattern = "^[1-9]\\d*$";
        if (Pattern.matches(pattern, question)) {
            limit = offset = 0;
            reloadData(question);
        } else {
            Log.d(TAG, question);
            ToastUtils.makeTextShort(getApplicationContext(), "输入的问题编号不规范");
        }
    }

    private void reloadData(final String answer) {
        pictureUrl.clear();
        previousButton.setClickable(false);
        nextButton.setClickable(false);
        offset = limit;
        limit += 5;
        ThreadTask.getInstance().executorNetThread(new Runnable() {
            @Override
            public void run() {
                HttpClientUtils httpClientUtils = new HttpClientUtils(answer, offset, limit);
                pictureUrl = httpClientUtils.getImageUrl();
                final int pictureSize = pictureUrl.size();
                if (pictureSize == 0) {
                    Log.i(TAG, "获取到的图片数据为 0");
                    ToastUtils.makeTextShort(getApplicationContext(), "当前回答图片为 0");
                }
                MainHandler.get(new Runnable() {
                    @Override
                    public void run() {
                        previousButton.setClickable(true);
                        nextButton.setClickable(true);
                        if (pictureSize == 0) {
                            Glide.with(getApplicationContext()).load(R.drawable.image).into(imageView);
                        } else {
                            Glide.with(getApplicationContext()).load(pictureUrl.get(curIndex)).into(imageView);
                        }
                    }
                });
                maxPictureIndex = pictureSize == 0 ? 0 : pictureSize - 1;
            }
        });
        curIndex = 0;
    }

    private void previousPicture() {
        if (pictureUrl.size() == 0) {
            ToastUtils.makeTextShort(getApplicationContext(), "当前无图片");
        } else if (curIndex == 0) {
            curIndex = maxPictureIndex;
        } else {
            curIndex--;
        }
        Glide.with(getApplicationContext()).load(pictureUrl.get(curIndex)).into(imageView);
    }

    private void nextPicture() {
        if (pictureUrl.size() == 0) {
            ToastUtils.makeTextShort(getApplicationContext(), "当前无图片");
        } else if (curIndex == maxPictureIndex) {
            reloadData(question);
        } else {
            curIndex++;
            Glide.with(getApplicationContext()).load(pictureUrl.get(curIndex)).into(imageView);
        }
    }

    private void cleanGlideCache() {
        ThreadTask.getInstance().executorDBThread(new Runnable() {
            @Override
            public void run() {
                Glide.get(getApplicationContext()).clearDiskCache();
            }
        });
        Glide.get(getApplicationContext()).clearMemory();
        ToastUtils.makeTextShort(getApplicationContext(), "成功清除缓存");
    }

    private void jumpToCommonQuestion() {
        Intent intent = new Intent(ViewZhiHuAnswerPicture.this, CommonQuestionActivity.class);
        startActivity(intent);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previous_picture_button:
                previousPicture();
                break;
            case R.id.next_picture_button:
                nextPicture();
                break;
            case R.id.clearButton:
                cleanGlideCache();
                break;
            case R.id.common_question_button:
                jumpToCommonQuestion();
                break;
            default:
                break;
        }
    }

    DragFloatButtonClickListener dragFloatButtonClickListener = new DragFloatButtonClickListener() {
        @Override
        public void onClick() {
            reloadQuestion();
        }

        @Override
        public void onLongClick() {

        }
    };
}
