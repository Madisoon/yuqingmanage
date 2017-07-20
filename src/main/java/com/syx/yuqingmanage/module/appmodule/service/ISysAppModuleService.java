package com.syx.yuqingmanage.module.appmodule.service;

import com.alibaba.fastjson.JSONArray;
import com.alienlab.db.ExecResult;

/**
 * Created by Msater Zg on 2017/7/13.
 */
public interface ISysAppModuleService {

    public ExecResult insertAppModule(String appModuleInfo, String appModuleTag, String appModuleBaseTag, String areaId);

    public ExecResult deleteAppModule(String appModuleId);

    public ExecResult updateAppModule(String appModuleId, String appModuleInfo, String appModuleTag, String appModuleBaseTag);

    public JSONArray getAllAppModule(String areaId);
}
