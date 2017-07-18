package com.syx.yuqingmanage.module.appmodule.service.imp;

import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.appmodule.service.ISysAppModuleService;
import com.syx.yuqingmanage.utils.SqlEasy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Msater Zg on 2017/7/13.
 */
@Service
public class SysAppModuleService implements ISysAppModuleService {
    @Autowired
    JSONResponse jsonResponse;

    @Override
    public ExecResult insertAppModule(String appModuleInfo, String appModuleTag, String areaId) {
        String sqlInsert = SqlEasy.insertObject(appModuleInfo, "app_module");
        ExecResult execResult = jsonResponse.getExecInsertId(sqlInsert, null, "", "");
        int insertId = Integer.parseInt(execResult.getMessage(), 10);
        String sqlInsertArea = "INSERT INTO app_module_area (app_area_id, app_module_id) VALUES (" + areaId + ", " + insertId + ")";
        List list = new ArrayList();
        String[] appModuleTags = appModuleTag.split(",");
        for (int i = 0, appModuleTagsLen = appModuleTags.length; i < appModuleTagsLen; i++) {
            list.add("INSERT INTO app_module_tag (app_module_id, app_module_tag_id) VALUES (" + insertId + "," + appModuleTags[i] + ")");
        }
        jsonResponse.getExecResult(sqlInsertArea, null);
        return execResult;
    }

    @Override
    public ExecResult deleteAppModule(String appModuleId) {
        String[] appModuleIds = appModuleId.split(",");
        List list = new ArrayList();
        for (int i = 0, appModuleIdsLen = appModuleIds.length; i < appModuleIdsLen; i++) {
            list.add("DELETE FROM app_module  WHERE  id = " + appModuleIds[i]);
            list.add("DELETE FROM app_module_area  WHERE  app_module_id = " + appModuleIds[i]);
            list.add("DELETE FROM app_module_tag WHERE app_module_id =  " + appModuleIds[i]);
        }
        ExecResult execResult = jsonResponse.getExecResult(list, "", "");
        return execResult;
    }

    @Override
    public ExecResult updateAppModule(String appModule, String appModuleId) {
        String sqlUpdate = SqlEasy.updateObject(appModule, "app_module", "appModuleId = " + appModuleId);
        ExecResult execResult = jsonResponse.getExecResult(sqlUpdate, null);
        return execResult;
    }

    @Override
    public ExecResult getAllAppModule(String areaId, String pageSize, String pageNumber) {
        return null;
    }
}
