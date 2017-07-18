package com.syx.yuqingmanage.module.appmodule.service;

import com.alienlab.db.ExecResult;

/**
 * Created by Msater Zg on 2017/7/13.
 */
public interface ISysAppModuleService {

    public ExecResult insertAppModule(String appModuleInfo, String appModuleTag, String areaId);

    public ExecResult deleteAppModule(String appModuleId);

    public ExecResult updateAppModule(String appModule, String appModuleId);

    public ExecResult getAllAppModule(String areaId, String pageSize, String pageNumber);
}
