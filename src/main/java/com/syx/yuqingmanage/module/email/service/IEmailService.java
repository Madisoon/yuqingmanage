package com.syx.yuqingmanage.module.email.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;

/**
 * Created by Master  Zg on 2016/12/12.
 */
public interface IEmailService {
    public ExecResult insertEmailData(String id, String url, String tagIdS);

    public ExecResult deleteEmailData(String id);

    public JSONObject getAllPostEmail(String pageNumber, String pageSize);

    public JSONObject getAllPostEmailMonitor(String pageNumber, String pageSize, String isStatus);

    public ExecResult updateEmailMonitor(String id);

    public ExecResult deleteEmailMonitor(String id);

    public ExecResult insertTemplateData(String data);

    public ExecResult updateTemplateData(String data, String id);

    public ExecResult deleteTemplateData(String id);

    public JSONArray getAllTemplate();
}
