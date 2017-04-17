package com.syx.yuqingmanage.module.infor.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;

/**
 * Created by Master  Zg on 2016/11/9.
 */
public interface IInForService {
    public ExecResult insertInFor(String inForData, String inForTag);

    public JSONObject getAllInfor(String pageNumber, String pageSize);

    public ExecResult updateInfoData(String infoData, String infoTagId, String infoId);

    public JSONObject getAllInfoChoose(String pageNumber, String pageSize,
                                       String searchTagId, String searchInfoData, String customerName);

    public ExecResult deleteInfoData(String infoId);

    public ExecResult manualPost(String infoId, String customerId);

    public JSONArray exportData(String searchTagId, String searchInfoData, String customerName);

}
