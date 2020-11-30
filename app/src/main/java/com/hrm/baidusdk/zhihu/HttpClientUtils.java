package com.hrm.baidusdk.zhihu;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

/**
 * @author: Hrm
 * @description:
 * @data: 2020/11/25
 */
public class HttpClientUtils {
    private static final String TAG = "HttpClientUtils";
    private Set<String> pictureUrls = new HashSet<>();
    private List<String> pictureUrl = new ArrayList<>();

    private List<String> topicUrls = new ArrayList<>();

    private String demoUrl = "https://www.zhihu.com/api/v4/questions/31123603/answers?include=data[*].is_normal,admin_closed_comment,reward_info,is_collapsed,annotation_action,annotation_detail,collapse_reason,is_sticky,collapsed_by,suggest_edit,comment_count,can_comment,content,editable_content,voteup_count,reshipment_settings,comment_permission,created_time,updated_time,review_info,relevant_info,question,excerpt,relationship.is_authorized,is_author,voting,is_thanked,is_nothelp,is_labeled;data[*].mark_infos[*].url;data[*].author.follower_count,badge[*].topics&offset=5&limit=5&sort_by=created";

    public HttpClientUtils() {
        topicUrls.add(getUrl());
        requestZhiHu(topicUrls.get(0));
    }

    public HttpClientUtils(String question) {
        topicUrls.add(getUrl(question));
        requestZhiHu(topicUrls.get(0));
    }

    public HttpClientUtils(String question, int offset, int limit) {
        topicUrls.add(getUrl(question, offset, limit));
        requestZhiHu(topicUrls.get(0));
    }

    private String getUrl(String question, int offset, int limit) {
        String url = "https://www.zhihu.com/api/v4/questions/" + question +
                "/answers?include=data[*].is_normal,admin_closed_comment,reward_info,is_collapsed,annotation_action,annotation_detail,collapse_reason,is_sticky,collapsed_by,suggest_edit,comment_count,can_comment,content,editable_content,voteup_count,reshipment_settings,comment_permission,created_time,updated_time,review_info,relevant_info,question,excerpt,relationship.is_authorized,is_author,voting,is_thanked,is_nothelp,is_labeled;data[*].mark_infos[*].url;data[*].author.follower_count,badge[*].topics&offset=" + offset + "&limit=" + limit + "&sort_by=default";
        return url;
    }

    private String getUrl(String question) {
        return getUrl(question, 0, 5);
    }

    private String getUrl() {
        return getUrl("31123603");
    }

    private void requestZhiHu(String topicUrl) {
        try {
            URL url = new URL(topicUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:63.0) Gecko/20100101 Firefox/63.0");
            connection.connect();
            // 获取输入流
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {// 循环读取流
                sb.append(line);
            }
            br.close();// 关闭流
            connection.disconnect();// 断开连接
            getImageUrl(sb.toString());
            pictureUrl.addAll(pictureUrls);
            printImageUrl();
        } catch (IOException e) {
            Log.e(TAG, "数据请求失败");
            e.printStackTrace();
        }
    }

    public List<String> getImageUrl() {
        return pictureUrl;
    }

    private void printImageUrl() {
        for (String str : pictureUrls) {
            Log.d(TAG, str);
        }
    }

    private void getImageUrl(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray array = jsonObject.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                String content = array.getJSONObject(i).getString("content");
                Document document = (Document) Jsoup.parse(content);
                Elements imgElements = document.select("[src]");
                for (Element e : imgElements) {
                    String tempUrl = e.attr("src");
                    if (tempUrl.startsWith("https")) {
                        pictureUrls.add(tempUrl);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
