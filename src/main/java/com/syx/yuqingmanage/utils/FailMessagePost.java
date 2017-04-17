package com.syx.yuqingmanage.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * Created by Msater Zg on 2017/3/30.
 */
@Component
public class FailMessagePost extends QuartzJobBean {
    QqMessagePost qqMessagePost = new QqMessagePost();

    JSONResponse jsonResponse = new JSONResponse();

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        postFailMessage();
    }

    public void postFailMessage() {
        String qqMessage = " SELECT * FROM qq_post_fail ";
        ExecResult execResult = jsonResponse.getSelectResult(qqMessage, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        if (jsonArray != null) {
            int jsonArrayLen = jsonArray.size();
            for (int i = 0; i < jsonArrayLen; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String postNumber = jsonObject.getString("post_number");
                String getNumber = jsonObject.getString("get_number");
                String title = jsonObject.getString("info_title");
                String content = jsonObject.getString("info_content");
                String link = jsonObject.getString("info_link");
                String type = jsonObject.getString("post_type");
                String source = jsonObject.getString("info_source");
                boolean flag = qqMessagePost.postMessage(getNumber, postNumber, type, title, content, link, source);
                if (flag) {
                    // 重新发送成功，删除这条信息
                    String deleteSql = "DELETE FROM qq_post_fail WHERE id = " + id;
                    jsonResponse.getExecResult(deleteSql, null);
                }
            }
        }
    }
}
