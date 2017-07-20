package com.syx.yuqingmanage.module.appmodule.service;

import com.alibaba.fastjson.JSONArray;
import com.alienlab.db.ExecResult;

/**
 * Created by Master  Zg on 2016/11/9.
 */
public interface ISysAppUserService {

    public ExecResult insertAppUser(String appUserInfo, String appUserProgram, String areaId);

    public ExecResult deleteAppUser(String appUserLoginName);

    public ExecResult updateAppUser(String appUserLoginName, String appUserInfo, String appUserProgram);

    public JSONArray getAllAppUserModule(String areaId);
}
