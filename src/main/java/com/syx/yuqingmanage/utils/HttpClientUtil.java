package com.syx.yuqingmanage.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Msater Zg on 2016/12/15.
 */
@SuppressWarnings("deprecation")
public class HttpClientUtil {
    /**
     * 发送post请求
     *
     * @param url
     * @param params
     * @return
     */
    public static JSONObject postJsonData(String url, Map<String, String> params) {
        CloseableHttpClient httpclient = HttpClientUtil.createDefault();
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();// 设置请求和传输超时时间
        httpPost.setConfig(requestConfig);
        // 拼接参数
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            System.out.println("key=" + key + " value=" + value);
            NameValuePair pair = new BasicNameValuePair(key, value);
            list.add(pair);
        }
        CloseableHttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
            httpPost.setHeader("Content-type", "application/json,charset=utf-8");
            response = httpclient.execute(httpPost);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /** 请求发送成功，并得到响应 **/
        JSONObject jsonObject = null;
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            HttpEntity httpEntity = response.getEntity();
            String result = null;
            try {
                result = EntityUtils.toString(httpEntity);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } // 返回json格式：
            jsonObject = JSONObject.parseObject(result);
        }
        return jsonObject;
    }

    /**
     * Creates {@link CloseableHttpClient} instance with default configuration.
     */
    public static CloseableHttpClient createDefault() {
        return HttpClientBuilder.create().build();
    }

    /**
     * 发送post请求
     *
     * @param url
     * @param params
     * @return
     */
    @SuppressWarnings({"resource"})
    public static JSONObject sendPost(String url, Map<String, String> params) {
        DefaultHttpClient client = new DefaultHttpClient();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();// 设置请求和传输超时时间
        /** NameValuePair是传送给服务器的请求参数 param.get("name") **/
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            NameValuePair pair = new BasicNameValuePair(key, value);
            list.add(pair);
        }
        UrlEncodedFormEntity entity = null;
        try {
            /** 设置编码 **/
            entity = new UrlEncodedFormEntity(list, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        /** 新建一个post请求 **/
        HttpPost post = new HttpPost(url);
        post.setEntity(entity);
        HttpResponse response = null;
        try {
            /** 客服端向服务器发送请求 **/
            response = client.execute(post);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /** 请求发送成功，并得到响应 **/
        JSONObject jsonObject = null;
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            HttpEntity httpEntity = response.getEntity();
            String result = null;
            try {
                result = EntityUtils.toString(httpEntity);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } // 返回json格式：
            jsonObject = JSONObject.parseObject(result);
        }
        return jsonObject;
    }


    public static void main(String[] args) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("ids", "6882,66");

        System.out.println(HttpClientUtil.sendPost(
                "http://118.178.237.219:8080/yuqingmanage/manage/deleteCustomerInfo", param));
    }
}
