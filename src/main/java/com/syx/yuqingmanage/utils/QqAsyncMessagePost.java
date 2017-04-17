package com.syx.yuqingmanage.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.response.JSONResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Msater Zg on 2017/4/6.
 */
public class QqAsyncMessagePost {
    @Autowired
    private NumberInfoPost numberInfoPost;
    @Autowired
    private QqMessagePost qqMessagePost;
    @Autowired
    private FailData failData;
    @Autowired
    private JSONResponse jsonResponse;

    @Async
    public void postCustomerMessage(JSONArray jsonArray, String infoContext, String infoTitle, String infoLink, String source) {
        long beginTime = System.currentTimeMillis(); // 这段代码放在程序执行前
        int allCustomerLen = jsonArray.size();
        List<String> list = new ArrayList<>();
        list.add("链接：" + infoLink);
        List<String> numberList = new ArrayList<>();
        for (int i = 0; i < allCustomerLen; i++) {
            JSONObject allCustomerSingle = jsonArray.getJSONObject(i);
            //发送的类型
            String postType = allCustomerSingle.getString("get_type");
            //接收号码（qq号，qq群，手机号）
            String getNumber = allCustomerSingle.getString("get_number");
            //发送
            String postNumber = allCustomerSingle.getString("qq_number");
            if ("number".equals(postType)) {
                numberList.add(getNumber);
            } else {
                int timeNumber = 2 + (int) Math.random() * 4;
                try {
                    TimeUnit.SECONDS.sleep(timeNumber);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                boolean flag = qqMessagePost.postMessage(getNumber, postNumber, postType, infoTitle, infoContext, infoLink, source);
                if (!flag) {
                    numberInfoPost.sendMsgByYunPian(postNumber + "消息发送失败了!", "18752002129");
                    failData.qqResend(getNumber, postNumber, postType, infoTitle, infoContext, infoLink, source);
                } else {

                }
            }
        }
        numberInfoPost.sendMsgByYunPian(StringUtils.join(list, ""), StringUtils.join(numberList, ","));
        long endTime = System.currentTimeMillis(); // 这段代码放在程序执行后
        long wasteTime = endTime - beginTime;
        double seconds = wasteTime / 1000;
        System.out.println("耗时：" + seconds + "秒");
    }

    public void postCustomerLate(JSONArray jsonArray, String infoContext, String infoTitle, String infoLink, String infoSource) {
        int allCustomerLen = jsonArray.size();
        List<String> sqlList = new ArrayList<>();
        for (int i = 0; i < allCustomerLen; i++) {
            JSONObject allCustomerSingle = jsonArray.getJSONObject(i);
            //发送的类型
            String postType = allCustomerSingle.getString("get_type");
            //接收号码（qq号，qq群，手机号）
            String postNumber = allCustomerSingle.getString("get_number");
            //发送
            String qqNumber = allCustomerSingle.getString("qq_number");
            //计划
            String qqPlan = allCustomerSingle.getString("scheme_plan_id");
            String sql = " INSERT INTO detention_post_info (info_title,info_content,info_link,info_source,info_post_type,info_number,info_post_qq,info_plan_id) " +
                    " VALUES ('" + infoTitle + "','" + infoContext + "','" + infoLink + "','" + infoSource + "','" + postType + "'," +
                    "'" + postNumber + "','" + qqNumber + "','" + qqPlan + "') ";
            sqlList.add(sql);
        }
        jsonResponse.getExecResult(sqlList, "", "");
    }

    public void postMessAgeTerrace(JSONArray jsonArray, String infoContext, String infoTitle, String infoLink, String infoSource) {
        int allModuleLen = jsonArray.size();
        Date nowTime = new Date();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTimeDate = time.format(nowTime);
        for (int i = 0; i < allModuleLen; i++) {
            // 获取到需要发送信息的平台
            JSONObject allModule = jsonArray.getJSONObject(i);
            System.out.println(allModule.toString());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("infoTime", nowTimeDate);
            jsonObject.put("infoSource", infoSource);
            jsonObject.put("infoLink", infoLink);
            jsonObject.put("infoTitle", infoTitle);
            jsonObject.put("infoContext", infoContext);
            jsonObject.put("tagId", allModule.getString("module_id"));
            jsonObject.put("tagName", allModule.getString("terrace_module_name"));
            Map<String, String> param = new HashMap<>();
            param.put("data", jsonObject.toString());
            try {
                HttpClientUtil.sendPost(allModule.getString("terrace_link"), param).toString();
            } catch (Exception e) {
                /*numberInfoPost.sendMsgByYunPian(allModule.getString("terrace_link") + "信息发送出错!", "18752002129");*/
            }
        }
    }
}
