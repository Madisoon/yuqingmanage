package com.syx.yuqingmanage.module.appmodule.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.appmodule.service.ISysAppModuleService;
import com.syx.yuqingmanage.utils.SqlEasy;
import org.apache.commons.lang3.StringUtils;
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
    public ExecResult insertAppModule(String appModuleInfo, String appModuleTag, String appModuleBaseTag, String areaId) {
        String sqlInsert = SqlEasy.insertObject(appModuleInfo, "app_module");
        ExecResult execResult = jsonResponse.getExecInsertId(sqlInsert, null, "", "");
        int insertId = Integer.parseInt(execResult.getMessage(), 10);
        String sqlInsertArea = "INSERT INTO app_module_area (app_area_id, app_module_id) VALUES (" + areaId + ", " + insertId + ")";
        List list = new ArrayList();
        String[] appModuleTags = appModuleTag.split(",");
        for (int i = 0, appModuleTagsLen = appModuleTags.length; i < appModuleTagsLen; i++) {
            list.add("INSERT INTO app_module_tag (app_module_id, app_module_tag_id) VALUES (" + insertId + "," + appModuleTags[i] + ")");
        }

        String[] appModuleBaseTags = appModuleBaseTag.split(",");
        for (int i = 0, appModuleBaseTagsLen = appModuleBaseTags.length; i < appModuleBaseTagsLen; i++) {
            list.add("INSERT INTO app_module_tag_dep (app_module_id, tag_id) VALUES (" + insertId + "," + appModuleBaseTags[i] + ")");
        }
        jsonResponse.getExecResult(list, "", "");
        jsonResponse.getExecResult(sqlInsertArea, null);
        return execResult;
    }

    @Override
    public ExecResult updateAppModule(String appModuleId, String appModuleInfo, String appModuleTag, String appModuleBaseTag) {
        List list = new ArrayList();
        String sqlUpdate = SqlEasy.updateObject(appModuleInfo, "app_module", "id = " + appModuleId);
        list.add(sqlUpdate);
        list.add("DELETE FROM app_module_tag WHERE app_module_id = '" + appModuleId + "'");
        list.add("DELETE FROM app_module_tag_dep WHERE app_module_id = '" + appModuleId + "'");
        String[] appModuleTags = appModuleTag.split(",");
        for (int i = 0, appModuleTagsLen = appModuleTags.length; i < appModuleTagsLen; i++) {
            list.add("INSERT INTO app_module_tag (app_module_id, app_module_tag_id) VALUES (" + appModuleId + "," + appModuleTags[i] + ")");
        }

        String[] appModuleBaseTags = appModuleBaseTag.split(",");
        for (int i = 0, appModuleBaseTagsLen = appModuleBaseTags.length; i < appModuleBaseTagsLen; i++) {
            list.add("INSERT INTO app_module_tag_dep (app_module_id, tag_id) VALUES (" + appModuleId + "," + appModuleBaseTags[i] + ")");
        }
        ExecResult execResult = jsonResponse.getExecResult(list, null, "");
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
            list.add("DELETE FROM app_module_tag_dep WHERE app_module_id =  " + appModuleIds[i]);
        }
        ExecResult execResult = jsonResponse.getExecResult(list, "", "");
        return execResult;
    }

    @Override
    public JSONArray getAllAppModule(String areaId) {
        String[] areaIds = areaId.split(",");
        int areaIdsLen = areaIds.length;
        List areaList = new ArrayList();
        areaList.add("b.app_area_id = '" + areaIds[0] + "'");
        for (int i = 1; i < areaIdsLen; i++) {
            areaList.add("OR b.app_area_id = '" + areaIds[i] + "' ");
        }
        String areaListWhere = StringUtils.join(areaList, "");
        List list = new ArrayList();
        list.add("SELECT a.*,GROUP_CONCAT(a.app_module_tag_id) AS app_module_tag_ids,GROUP_CONCAT(a.name) AS tag_names ");
        list.add("FROM (SELECT a.*,b.name FROM (SELECT a.*,c.user_name,d.app_module_tag_id FROM app_module a ");
        list.add("LEFT JOIN app_module_area b ON a.id = b.app_module_id ");
        list.add("LEFT JOIN sys_user c ON a.app_module_create = c.user_loginname ");
        list.add("LEFT JOIN app_module_tag d ON a.id = d.app_module_id ");
        list.add("WHERE a.app_module_type = '0' AND (" + areaListWhere + ")) a ");
        list.add("LEFT JOIN sys_tag b ON a.app_module_tag_id = b.id ");
        list.add("UNION ALL ");
        list.add("SELECT a.*,b.topic_name AS NAME FROM (SELECT a.*,c.user_name,d.app_module_tag_id FROM app_module a ");
        list.add("LEFT JOIN app_module_area b ON a.id = b.app_module_id ");
        list.add("LEFT JOIN sys_user c ON a.app_module_create = c.user_loginname ");
        list.add("LEFT JOIN app_module_tag d ON a.id = d.app_module_id ");
        list.add("WHERE a.app_module_type = '1' AND (" + areaListWhere + ")) a ");
        list.add("LEFT JOIN sys_topic b ON a.app_module_tag_id = b.id) a GROUP BY a.id ");
        String getSql = StringUtils.join(list, "");
        ExecResult execResult = jsonResponse.getSelectResult(getSql, null, "");
        JSONArray jsonArray = new JSONArray();
        jsonArray = (JSONArray) execResult.getData();
        return jsonArray;
    }
}
