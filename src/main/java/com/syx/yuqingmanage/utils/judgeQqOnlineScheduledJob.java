package com.syx.yuqingmanage.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.concurrent.TimeUnit;

public class judgeQqOnlineScheduledJob extends QuartzJobBean {
    private final String url = "http://180.96.63.186:53234/api/login";
    private JSONResponse jsonResponse = new JSONResponse();

    private AppJsonPost appJsonPost = new AppJsonPost();

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        qqOnline();
    }

    public void qqOnline() {
        String qqSql = " SELECT qq_number,qq_password FROM  sys_qq ";
        ExecResult execResult = jsonResponse.getSelectResult(qqSql, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        if (jsonArray == null) {

        } else {
            int jsonArrayLen = jsonArray.size();
            for (int i = 0; i < jsonArrayLen; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String qqNumber = jsonObject.getString("qq_number");
                String qqPwd = jsonObject.getString("qq_password");
                JSONObject jsonObjectPost = new JSONObject();
                jsonObjectPost.put("name", qqNumber);
                jsonObjectPost.put("password", qqPwd);
                try {
                    TimeUnit.SECONDS.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("尝试登陆qq");
                appJsonPost.appadd(url, jsonObjectPost);
            }
        }
    }
}
