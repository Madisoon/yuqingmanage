package com.syx.yuqingmanage.module.app.service;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Msater Zg on 2017/7/13.
 */
public interface IYuQingService {
    public JSONObject judgerAppUser(String loginName, String password, long timestamp);

    public JSONObject checkToken(String token);

    public JSONObject searchMenus(String loginName);

    public JSONObject searchFocus(int tag_id, int limit, String date, String loginName);

    public JSONObject searchTagInfo(String filters, int limit, String data, String loginName);

    public JSONObject getInfodetail(String id, String loginName);

    public JSONObject searchFavor(int limit, String date, String loginName);

    public JSONObject removeFavor(String id, String loginName);

    public JSONObject addFavor(String id, String loginName);

    public JSONObject checkFavor(String id, String loginName);

    public JSONObject updatePwd(String oldPwd, String newPwd, String loginName);

    public JSONObject updateConfig(String tag_id, String push, String loginName);

    public JSONObject getConfig(String loginName);

    public JSONObject checkVersion(String appVersion, String versionType);

    public JSONObject clickInfoData(String userName, String infoId, String infoType);

    public JSONObject clickFavor(String favorId);

}
