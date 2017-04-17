package com.syx.yuqingmanage.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Msater Zg on 2017/3/28.
 */
public class AppJsonPost {
    @Autowired
    FailData failData;

    public boolean appadd(String postUrl, JSONObject jsonObject) {
        boolean flag = true;
        StringBuffer sb = new StringBuffer("");
        try {
            //创建连接
            URL url = new URL(postUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type",
                    "application/json");
            connection.setRequestProperty("Charset", "UTF-8");

            connection.connect();

            //POST请求
            DataOutputStream out = new DataOutputStream(
                    connection.getOutputStream());
            out.write(jsonObject.toString().getBytes("UTF-8"));
            out.flush();
            out.close();
            //读取响应
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String lines;
                while ((lines = reader.readLine()) != null) {
                    lines = new String(lines.getBytes(), "utf-8");
                    sb.append(lines);
                }
                reader.close();
                // 响应值
                JSONObject reponse = JSON.parseObject(sb.toString());
                System.out.println(reponse);
                System.out.println(jsonObject);
                String qqStatus = reponse.getString("status");
                System.out.println(qqStatus);
                if (!"0".equals(qqStatus)) {
                    flag = false;
                }
            } catch (Exception e) {
                flag = false;
            }
            // 断开连接
            connection.disconnect();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return flag;
    }
}
