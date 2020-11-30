package com.hrm.baidusdk.util;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author: Hrm
 * @description: 字符串工具类
 * @data: 2020/11/22
 */
public class StringUtils {

    public static final String EMPTY_STRING = "";

    /**
     * 判断string 是否为空
     *
     * @param string
     * @return
     */
    public static boolean isEmpty(String string) {
        return (string == null || string.length() == 0);
    }

    /**
     * 返回字符串长度
     *
     * @param string
     * @return
     */
    public static int length(String string) {
        return string == null ? 0 : string.length();
    }

    /**
     * string 转 UTF-8
     * @param string
     * @return
     */
    public static String utf8Encode(String string) {
        if (!isEmpty(string) && string.getBytes().length != length(string)) {
            try {
                return URLEncoder.encode(string, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("UnsupportedEncodingException occurred. ", e);
            }
        }

        return string;
    }
}
