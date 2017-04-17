package com.syx.yuqingmanage.module.user.service;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;

/**
 * Created by Msater Zg on 2016/12/22.
 */
public interface ICustomerService {
    /**
     * 获取到所有的客户（名称，状态，时间）
     * @return
     */
    public JSONObject getAllCustomer();

    /**
     * 根据id组删除用户
     * @param ids
     * @return
     */
    public ExecResult deleteCustomer(String ids);

    /**
     * 根据信息新增客户
     * @param customerInfo
     * @return
     */
    public ExecResult insertCustomer(String customerInfo);

    /**
     * 根据信息，修改客户信息
     * @param customerInfo
     * @return
     */
    public ExecResult changeCustomer(String customerInfo,String customerId);
}
