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
    private JSONResponse jsonResponse;

    @Async
    public void postCustomerMessage(JSONArray jsonArray, String infoLink, String informationId) {
        int allCustomerLen = jsonArray.size();
        List<String> list = new ArrayList<>();
        List<String> numberList = new ArrayList<>();
        for (int i = 0; i < allCustomerLen; i++) {
            JSONObject allCustomerSingle = jsonArray.getJSONObject(i);
            //接收的客户名称
            String consumerName = allCustomerSingle.getString("customer_name");
            //发送的类型
            String postType = allCustomerSingle.getString("get_type");
            //接收号码（qq号，qq群，手机号）
            String getNumber = allCustomerSingle.getString("get_number");
            //发送
            String postNumber = allCustomerSingle.getString("qq_number");
            String weixinPostNumber = allCustomerSingle.getString("customer_post_weixin");
            String customerPriority = allCustomerSingle.getString("customer_priority");
            if ("number".equals(postType)) {
                numberList.add(getNumber);
                list.add("链接:" + infoLink);
            } else {
                String insertSql = "";
                if ("qq".equals(postType) || "qqGroup".equals(postType)) {
                    insertSql = "INSERT INTO sys_manual_post (infor_id,infor_post_type,infor_post_people," +
                            "infor_get_people,infor_priority,infor_consumer) " +
                            "VALUES('" + informationId + "','" + postType + "','" + postNumber + "'," +
                            "'" + getNumber + "'," + customerPriority + ",'" + consumerName + "') ";
                } else {
                    insertSql = "INSERT INTO sys_manual_post (infor_id,infor_post_type,infor_post_people," +
                            "infor_get_people,infor_priority,infor_consumer) " +
                            "VALUES('" + informationId + "','" + postType + "','" + weixinPostNumber + "'," +
                            "'" + getNumber + "'," + customerPriority + ",'" + consumerName + "') ";
                }
                jsonResponse.getExecResult(insertSql, null);
            }
        }
        if (numberList.size() > 0) {
            numberInfoPost.sendMsgByYunPian(StringUtils.join(list, ""), StringUtils.join(numberList, ","));
        }
    }

    /**
     * 延迟推送
     *
     * @param jsonArray
     * @param infoLink
     * @param infoId
     */
    public void postCustomerLate(JSONArray jsonArray, String infoLink, String infoId) {
        int allCustomerLen = jsonArray.size();
        List<String> sqlList = new ArrayList<>();
        for (int i = 0; i < allCustomerLen; i++) {
            JSONObject allCustomerSingle = jsonArray.getJSONObject(i);
            //接收的客户名称
            String consumerName = allCustomerSingle.getString("customer_name");
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
            String sql = " INSERT INTO detention_post_info (info_id,info_link,info_priority,info_post_type,info_number,info_post_qq,info_plan_id,info_consumer) " +
                    " VALUES ('" + infoId + "','" + infoLink + "'," + customerPriority + ",'" + postType + "'," +
                    "'" + postNumber + "','" + qqNumber + "','" + qqPlan + "','" + consumerName + "') ";
            sqlList.add(sql);
        }
        jsonResponse.getExecResult(sqlList, "", "");
    }

    /**
     * 推送到平台的逻辑
     *
     * @param jsonArray
     * @param infoContext
     * @param infoTitle
     * @param infoLink
     * @param infoSource
     * @param infoType
     * @param infoSite
     */
    public void postMessAgeTerrace(JSONArray jsonArray, String infoContext, String infoTitle, String infoLink, String infoSource, String infoType, String infoSite) {
        int allModuleLen = jsonArray.size();
        Date nowTime = new Date();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTimeDate = time.format(nowTime);
        for (int i = 0; i < allModuleLen; i++) {
            // 获取到需要发送信息的平台
            JSONObject allModule = jsonArray.getJSONObject(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("infoTime", nowTimeDate);
            jsonObject.put("infoSource", infoSource);
            jsonObject.put("infoLink", infoLink);
            jsonObject.put("pubTime", nowTimeDate);
            jsonObject.put("levelId", ("1".equals(infoType)) ? infoType : "-1");
            jsonObject.put("infoTitle", infoTitle);
            jsonObject.put("infoSite", infoSite);
            jsonObject.put("infoContext", infoContext);
            jsonObject.put("tagId", allModule.getString("module_id"));
            jsonObject.put("tagName", allModule.getString("terrace_module_name"));
            Map<String, String> param = new HashMap<>(16);
            param.put("data", jsonObject.toString());
            try {
                HttpClientUtil.sendPost(allModule.getString("terrace_link"), param).toString();
            } catch (Exception e) {

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
