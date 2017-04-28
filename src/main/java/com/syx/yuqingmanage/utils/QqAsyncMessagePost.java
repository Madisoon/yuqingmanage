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
    /* @Autowired*/
    private JSONResponse jsonResponse = new JSONResponse();

    @Async
    public void postCustomerMessage(JSONArray jsonArray, String infoContext, String infoTitle, String infoLink, String source, String inforCreater) {
        long beginTime = System.currentTimeMillis(); // 这段代码放在程序执行前
        int allCustomerLen = jsonArray.size();
        List<String> list = new ArrayList<>();
        list.add("链接：" + infoLink);
        List<String> numberList = new ArrayList<>();
        for (int i = 0; i < allCustomerLen; i++) {
            JSONObject allCustomerSingle = jsonArray.getJSONObject(i);
            System.out.println("allCustomerSingle");
            //发送的类型
            String postType = allCustomerSingle.getString("get_type");
            //接收号码（qq号，qq群，手机号）
            String getNumber = allCustomerSingle.getString("get_number");
            //发送
            String postNumber = allCustomerSingle.getString("qq_number");
            String weixinPostNumber = allCustomerSingle.getString("customer_post_weixin");
            String customerPriority = allCustomerSingle.getString("customer_priority");
            List<String> listMsg = new ArrayList<>();
            listMsg.add("标题 : " + infoTitle);
            listMsg.add("内容 : " + infoContext);
            listMsg.add("链接 : ");
            listMsg.add(infoLink);
            listMsg.add("来源 : " + source);
            String messAgeWord = StringUtils.join(listMsg, "\n");
            if ("number".equals(postType)) {
                numberList.add(getNumber);
            } else {
                /* 发送信息给qq
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

                }*/
                String insertSql = "";
                if ("qq".equals(postType) || "qqGroup".equals(postType)) {
                    insertSql = "INSERT INTO sys_manual_post (infor_context,infor_post_type,infor_post_people," +
                            "infor_get_people,infor_priority,infor_create_people) VALUES('" + messAgeWord + "','" + postType + "','" + postNumber + "','" + getNumber + "'," + customerPriority + ",'" + inforCreater + "') ";
                } else {
                    insertSql = "INSERT INTO sys_manual_post (infor_context,infor_post_type,infor_post_people," +
                            "infor_get_people,infor_priority,infor_create_people) VALUES('" + messAgeWord + "','" + postType + "','" + weixinPostNumber + "','" + getNumber + "'," + customerPriority + ",'" + inforCreater + "') ";
                }
                jsonResponse.getExecResult(insertSql, null);
            }
        }
        numberInfoPost.sendMsgByYunPian(StringUtils.join(list, ""), StringUtils.join(numberList, ","));
        long endTime = System.currentTimeMillis(); // 这段代码放在程序执行后
        long wasteTime = endTime - beginTime;
        double seconds = wasteTime / 1000;
        System.out.println("耗时：" + seconds + "秒");
    }

    public void postCustomerLate(JSONArray jsonArray, String infoContext, String infoTitle, String infoLink, String infoSource, String inforCreater) {
        int allCustomerLen = jsonArray.size();
        List<String> sqlList = new ArrayList<>();
        for (int i = 0; i < allCustomerLen; i++) {
            JSONObject allCustomerSingle = jsonArray.getJSONObject(i);
            //发送的类型
            String postType = allCustomerSingle.getString("get_type");
            //接收号码（qq号，qq群，手机号）
            String postNumber = allCustomerSingle.getString("get_number");
            //发送
            String qqNumber = "";
            if ("qq".equals(postType) || "qqGroup".equals(postType)) {
                qqNumber = allCustomerSingle.getString("qq_number");
            } else {
                qqNumber = allCustomerSingle.getString("customer_post_weixin");
            }
            String customerPriority = allCustomerSingle.getString("customer_priority");
            String qqPlan = allCustomerSingle.getString("scheme_plan_id");
            String sql = " INSERT INTO detention_post_info (info_title,info_content,info_link,info_source,info_priority,info_post_type,info_number,info_post_qq,info_plan_id,info_creater_people) " +
                    " VALUES ('" + infoTitle + "','" + infoContext + "','" + infoLink + "','" + infoSource + "'," + customerPriority + ",'" + postType + "'," +
                    "'" + postNumber + "','" + qqNumber + "','" + qqPlan + "','" + inforCreater + "') ";
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

    @Async
    public void insertData(JSONObject jsonObject, JSONArray jsonArray) {
        String id = jsonObject.getString("id");
        int jsonArray1Len = jsonArray.size();
        int j;
        for (j = 0; j < jsonArray1Len; j++) {
            JSONObject jsonObject2 = jsonArray.getJSONObject(j);
            String ids = jsonObject2.getString("base_id");
            if (id.equals(ids)) {
                break;
            }
        }
        if (j == jsonArray1Len) {
            List list = new ArrayList();
            list.add("INSERT INTO base_yuqing_user  (base_id,base_user_name,base_start_time,base_end_time) ");
            list.add("VALUES ('" + jsonObject.getString("id") + "','" + jsonObject.getString("name") + "' ");
            list.add(",'" + jsonObject.getString("add_time") + "','" + jsonObject.getString("end_date") + "') ");
            jsonResponse.getExecResult(StringUtils.join(list, ""), null);
        }
    }
}
