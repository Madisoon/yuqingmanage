package com.syx.yuqingmanage.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Msater Zg on 2017/3/1.
 */
@Component
public class MessageScheduledJob extends QuartzJobBean {
    private JSONResponse jsonResponse = new JSONResponse();
    private NumberInfoPost numberInfoPost = new NumberInfoPost();

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        postMessage();
    }

    public void postMessage() {
        String sql = " SELECT * FROM detention_post_info ";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        if (jsonArray != null) {
            // 没有需要延迟推送的信息
            int jsonArrayLen = jsonArray.size();
            for (int i = 0; i < jsonArrayLen; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                // 计划id
                String infoPlanId = jsonObject.getString("info_plan_id");
                // 发送账号
                String infoPostQq = jsonObject.getString("info_post_qq");
                // 发送类型
                String infoPostType = jsonObject.getString("info_post_type");
                // 接收账号
                String infoNumber = jsonObject.getString("info_number");
                // 信息等级
                String infoPriority = jsonObject.getString("info_priority");
                // 信息id
                String infoId = jsonObject.getString("info_id");
                // 信息链接
                String infoLink = jsonObject.getString("info_link");
                // 信息接受的客户名称
                String inforConsumer = jsonObject.getString("info_consumer");
                String id = jsonObject.getString("id");
                //当前时间累加半小时
                long timeMillis = System.currentTimeMillis();
                timeMillis += 30 * 60 * 1000;
                Date timeAdvance = new Date(timeMillis);
                // 提前半个小时发送
                int flag = DifTimeGet.judgeTimeInterval(getScheduledTime(infoPlanId, new Date()), timeAdvance);
                if (flag == 1) {
                    // 开始发送，发送完，删除这条信息
                    String sqlDelete = "DELETE FROM detention_post_info WHERE id= " + id;
                    if ("number".equals(infoPostType)) {
                        String content = "链接:" + infoLink;
                        numberInfoPost.sendMsgByYunPian(content, infoNumber);
                        jsonResponse.getExecResult(sqlDelete, null);
                    } else {
                        String insertSql = "INSERT INTO sys_manual_post (infor_id,infor_post_type,infor_post_people," +
                                "infor_get_people,infor_priority,infor_consumer) " +
                                "VALUES('" + infoId + "','" + infoPostType + "','" + infoPostQq + "'," +
                                "'" + infoNumber + "'," + infoPriority + ", '" + inforConsumer + "') ";
                        List list = new ArrayList();
                        list.add(insertSql);
                        list.add(sqlDelete);
                        jsonResponse.getExecResult(list, "", "");
                    }
                }
            }
        }
    }

    public String getScheduledTime(String planId, Date nowTime) {
        String weekDate = DifTimeGet.getWeekTime(nowTime);
        String sql = " SELECT * FROM sys_plan WHERE id = " + planId;
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        return jsonObject.getString(weekDate);
    }
}
