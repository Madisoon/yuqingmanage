package com.syx.yuqingmanage.module.infor.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;

/**
 * Created by Master  Zg on 2016/11/9.
 */
public interface IInForService {
    public ExecResult insertInFor(String inForData, String inForTag);

    /**
     * 确认信息（筛选的过程）
     * @param infoId
     * @return
     */
    public ExecResult infoSure(String infoId, String infoData);

    public JSONObject getAllInfor(String pageNumber, String pageSize, String postType);

    public ExecResult updateInfoData(String infoData, String infoTagId, String infoId);

    public JSONObject getAllInfoChoose(String pageNumber, String pageSize,
                                       String searchTagId, String searchInfoData, String customerName);

    public ExecResult deleteInfoData(String infoId);

    public String exportData(String searchTagId, String searchInfoData, String customerName, String exportType);

}
