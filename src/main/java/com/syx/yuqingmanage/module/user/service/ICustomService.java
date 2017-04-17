package com.syx.yuqingmanage.module.user.service;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;

/**
 * Created by Master  Zg on 2016/11/21.
 */
public interface ICustomService {
    /**
     * 获取所有的用户
     *
     * @return
     */
    public JSONObject getAllCustom(String pageNumber, String pageSize, String choiceSelect);

    /**
     * 获取到所有的分组的客户
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public JSONObject getAllCustomGroup(String pageNumber, String pageSize);

    /**
     * 删除用户（数组）
     *
     * @param idArray
     * @return
     */
    public ExecResult deleteCustom(String idArray);

    /**
     * 新增一条客户
     *
     * @param customData
     * @return
     */
    public ExecResult postCustomData(String customData, String customId, String customInfo);

    /**
     * 获取所有的部门和人的去权限关系
     *
     * @return
     */
    public ExecResult getDeparmentUser();

    /**
     * 根据id获取到属于同一个客户的人
     * @param customerId
     * @return
     */
    public JSONObject getAllCustomerById(String customerId);
}
