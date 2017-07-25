package com.syx.yuqingmanage.module.app.service;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Msater Zg on 2017/7/13.
 */
public interface IYuQingService {
    public JSONObject judgerAppUser(String loginName, String password, long timestamp);

    public JSONObject checkToken();

    // 带cookie，根据cookie获取用户名
    public JSONObject searchMenus();

    // 需要先判断
    public JSONObject searchFocus(int tag_id, int limit, String date);

    public JSONObject searchTagInfo(String filters, int limit, String data);

    public JSONObject getInfodetail(String id);

    public JSONObject searchFavor(int limit, String date);

    // 根据id判断是否移除所有的数据
    public JSONObject removeFavor(String id);

    public JSONObject addFavor(int id);

    public JSONObject checkFavor(int id);

    public JSONObject updatePwd(String oldPwd, String newPwd);

    public JSONObject updateConfig(String type, String tag_id, String name, String push);

    public JSONObject getConfig();

}
