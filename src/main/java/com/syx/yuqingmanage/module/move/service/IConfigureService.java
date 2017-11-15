package com.syx.yuqingmanage.module.move.service;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;

/**
 * Created by Master  Zg on 2016/11/9.
 */
public interface IConfigureService {
    public ExecResult addQq(String qqDate);

    public JSONObject getAllQq();

    public ExecResult deleteQq(String idData);

    public ExecResult updateQq(String qqData, String id);

    public ExecResult addPlan(String planData);

    public JSONObject getAllPlan();

    public ExecResult deletePlan(String idData);

    public ExecResult updatePlan(String planData, String id);

    public ExecResult addWx(String wxDate);

    public JSONObject getAllWx();

    public ExecResult deleteWx(String idData);

    public ExecResult updateWx(String wxData, String id);

    public JSONObject postAnnouncement(String title, String content);

    public JSONObject getAnnouncement(String filters, String start, String limit);

    public JSONObject resetAnnouncement();
}
