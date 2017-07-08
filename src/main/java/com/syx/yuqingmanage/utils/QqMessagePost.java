package com.syx.yuqingmanage.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.xml.internal.xsom.impl.ListSimpleTypeImpl;
import jdk.nashorn.internal.ir.ReturnNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.test.context.ActiveProfiles;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Msater Zg on 2017/3/24.
 */
public class QqMessagePost {
    private AppJsonPost appJsonPost = new AppJsonPost();

    public static final String ADD_URL = "http://39.108.178.160:53234/api/send";

    public static String[] randomCharacter = {" ", "'", "`", ".", ","};
    int randomCharacterLen = randomCharacter.length;

    public boolean postMessage(String getNumber, String postNumber, String type, String title, String context, String link, String source) {
        JSONObject jsonObjectData = new JSONObject();
        System.out.println("执行这个方法了");
        JSONObject jsonObjectMessage = new JSONObject();
        boolean flag;
        if ("qq".equals(type)) {
            jsonObjectMessage.put("Uin", Long.parseLong(getNumber));
        } else {
            jsonObjectMessage.put("GUin", Long.parseLong(getNumber));
        }
        List<String> list = new ArrayList<>();
        for (int i = 0; i < randomCharacterLen; i++) {
            int mathIndex = 1 + (int) (Math.random() * 4);
            list.add(randomCharacter[mathIndex]);
        }
        List<String> listMsg = new ArrayList<>();
        listMsg.add("标题 : " + title);
        listMsg.add("内容 : " + context + "" + StringUtils.join(list, ""));
        listMsg.add("链接 : ");
        listMsg.add(link);
        listMsg.add("来源 : " + source);
        String messAgeWord = StringUtils.join(listMsg, "\n");
        jsonObjectMessage.put("Msg", messAgeWord);
        jsonObjectData.put("sender", Long.parseLong(postNumber));
        if ("qq".equals(type)) {
            jsonObjectData.put("sendMsg", jsonObjectMessage);
        } else {
            jsonObjectData.put("groupMsg", jsonObjectMessage);
        }
        flag = appJsonPost.appadd(ADD_URL, jsonObjectData);
        return flag;
    }
}
