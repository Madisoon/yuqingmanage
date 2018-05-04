package com.syx.yuqingmanage.module.infor.service;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;

/**
 * Created by Msater Zg on 2017/4/20.
 */
public interface IInForHistoryService {
    public JSONObject getAllHistory(String pageNumber, String pageSize);

    public JSONObject getChoiceHistory(String pageNumber, String pageSize, String tableChoiceData, String timeOrderType);

    public JSONObject exportHistoryInfor(String searchData, String exportType);
}
