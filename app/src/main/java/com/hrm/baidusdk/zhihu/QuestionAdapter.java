package com.hrm.baidusdk.zhihu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hrm.baidusdk.R;
import com.hrm.baidusdk.bean.QuestionBean;

import java.util.ArrayList;
import java.util.List;


/**
 * @author: Hrm
 * @description: 知乎常用编号列表 RecycleView 适配器
 * @data: 2020/11/26
 */
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {
    public interface OnItemClickListener {
        void onClick(int position);
    }

    private OnItemClickListener mClickListener;

    private List<QuestionBean> questionBeanList = new ArrayList<>();

    public QuestionAdapter() {
        initData();
    }

    public QuestionAdapter(List<QuestionBean> questionBeanList) {
        this.questionBeanList = questionBeanList;
    }

    public void setOnItemClickListener(OnItemClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public String getQuestion(int position) {
        return questionBeanList.get(position).getQuestion();
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent, false);
        QuestionViewHolder viewHolder = new QuestionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, final int position) {
        holder.question.setText(questionBeanList.get(position).getQuestion());
        holder.describe.setText(questionBeanList.get(position).getDescribe());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return questionBeanList.size();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        public TextView question;
        public TextView describe;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question);
            describe = itemView.findViewById(R.id.describe);
        }
    }

    private void initData() {
        questionBeanList.add(new QuestionBean("313825759", "有个身材火辣的女朋友是种怎样的体验？"));
        questionBeanList.add(new QuestionBean("28560777", "女朋友很漂亮是怎样的体验？"));
        questionBeanList.add(new QuestionBean("34243513", "你见过最漂亮的女生长什么样？"));
        questionBeanList.add(new QuestionBean("28997505", "有个漂亮女朋友是种怎样的体验？"));
        questionBeanList.add(new QuestionBean("427537253", "女朋友/女生特别特别喜欢丝袜是种什么体验？"));
        questionBeanList.add(new QuestionBean("388408274", "女生们喜欢穿什么颜色的丝袜？"));
        questionBeanList.add(new QuestionBean("388925973", "女生的丝袜怎么穿好看?"));
        questionBeanList.add(new QuestionBean("296688172", "如何让女友爱上情趣内衣？"));
        questionBeanList.add(new QuestionBean("29024583", "女朋友长得好看是怎样的体验？"));
        questionBeanList.add(new QuestionBean("340990864", "有性感的身材是什么感受?"));
    }
}
