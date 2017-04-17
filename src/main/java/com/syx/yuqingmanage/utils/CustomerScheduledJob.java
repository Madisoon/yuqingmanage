package com.syx.yuqingmanage.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Msater Zg on 2017/2/17.
 */
public class CustomerScheduledJob extends QuartzJobBean {
    private JSONResponse jsonResponse = new JSONResponse();

    @Override
    protected void executeInternal(JobExecutionContext arg0)
            throws JobExecutionException {
        /*System.out.println("I am CustomerScheduledJob");*/
        judgeCustomer();
    }

    public String judgeCustomer() {
        String sqlCustomer = "SELECT * FROM  sys_post_customer ";
        ExecResult execResult = jsonResponse.getSelectResult(sqlCustomer, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        if (jsonArray == null) {

        } else {
            int jsonArrayLen = jsonArray.size();
            for (int i = 0; i < jsonArrayLen; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String customerEndTime = jsonObject.getString("customer_end_time");

                try {
                    Date a1 = new SimpleDateFormat("yyyy-MM-dd").parse(customerEndTime);
                    Date b1 = new Date();
                    long dayNumber = (a1.getTime() - b1.getTime()) / (24 * 60 * 60 * 1000);
                    if (dayNumber <= 0) {
                        //到期了a
                        String sql = "UPDATE sys_post_customer SET customer_status = '0' WHERE id = " + id;
                        jsonResponse.getExecResult(sql, null);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }
}
