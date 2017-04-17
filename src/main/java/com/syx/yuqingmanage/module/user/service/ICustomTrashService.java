package com.syx.yuqingmanage.module.user.service;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Msater Zg on 2016/12/19.
 */
public interface ICustomTrashService {
    /**
     * 获取所有的被删除的客户
     * @return
     */
    public JSONObject getAllTrashUser();
}
