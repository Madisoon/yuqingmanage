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

    private QqMessagePost qqMessagePost = new QqMessagePost();

    FailData failData = new FailData();

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        postMessage();
        System.out.println("执行");
    }

    public String postMessage() {
        String sql = " SELECT * FROM detention_post_info ";
        System.out.println("执行");
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        if (jsonArray == null) {
            return "";
        } else {
            int jsonAraayLen = jsonArray.size();
            for (int i = 0; i < jsonAraayLen; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String infoPlanId = jsonObject.getString("info_plan_id");
                String infoTitle = jsonObject.getString("info_title");
                String infoContent = jsonObject.getString("info_content");
                String infoLink = jsonObject.getString("info_link");
                String infoSource = jsonObject.getString("info_source");
                String infoPostQq = jsonObject.getString("info_post_qq");
                String infoPostType = jsonObject.getString("info_post_type");
                String infoNumber = jsonObject.getString("info_number");
                String id = jsonObject.getString("id");
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
                    } else if ("qq".equals(infoPostType) || "qqGroup".equals(infoPostType)) {
                        System.out.println("开始发送");
                        // 定时发qq消息
                        int timeNumber = 2 + (int) Math.random() * 4;
                        try {
                            TimeUnit.SECONDS.sleep(timeNumber);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        boolean flagQq = qqMessagePost.postMessage(infoNumber, infoPostQq, infoPostType, infoTitle, infoContent, infoLink, infoSource);
                        if (!flagQq) {
                            // 如果发送失败，就直接放入到数据库中等待下一次发送
                            failData.qqResend(infoNumber, infoPostQq, infoPostType, infoTitle, infoContent, infoLink, infoSource);
                        }
                        /*qq调用一遍直接把信息删除*/
                        jsonResponse.getExecResult(sqlDelete, null);
                    } else {
                        System.out.println("调用发送微信的接口");
                    }
                } else {
                    System.out.println("这条信息现在不发送！");
                }
            }
            return "";
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
