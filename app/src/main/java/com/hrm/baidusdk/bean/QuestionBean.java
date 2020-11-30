package com.hrm.baidusdk.bean;

/**
 * @author: Hrm
 * @description:
 * @data: 2020/11/26
 */
public class QuestionBean {
    private String question;
    private String describe;

    public QuestionBean(String question, String describe) {
        this.question = question;
        this.describe = describe;
    }

    public String getQuestion() {
        return question;
    }

    public String getDescribe() {
        return describe;
    }
}
