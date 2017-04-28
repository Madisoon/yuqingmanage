package com.syx.yuqingmanage.module.infor.service;

import com.alienlab.db.ExecResult;

/**
 * Created by Msater Zg on 2017/4/20.
 */
public interface IInForPostService {
    public ExecResult getInforPost(String userLoginName);

    public ExecResult updateInforPost(String id, String loginName);

    public ExecResult deleteInforPost(String id);
}
