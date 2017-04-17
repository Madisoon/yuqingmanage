package com.syx.yuqingmanage.module.app.service;

import com.alienlab.db.ExecResult;

/**
 * Created by Master  Zg on 2016/12/12.
 */
public interface IAppService {

    public ExecResult addUser(String userInfo);

    public ExecResult deleteUser(String id);

    public ExecResult updateUser(String userInfo);

    public ExecResult getAllCustomer();

    public ExecResult getCustomerById(String id);

    public ExecResult deleteCustomerInfo(String id);

    public ExecResult insertCutomerId(String id);

    public void refreshData();
}
