package com.syx.yuqingmanage.module.appmodule.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.appmodule.service.ISysAppUserService;
import com.syx.yuqingmanage.utils.SqlEasy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.ls.LSInput;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Master  Zg on 2016/11/9.
 */
@Service
public class SysAppUserService implements ISysAppUserService {
    @Autowired
    private JSONResponse jsonResponse;

    @Override
    public ExecResult insertAppUser(String appUserInfo, String appUserProgram, String areaId) {
        JSONObject jsonObjectUser = JSONObject.parseObject(appUserInfo);
        String appUserLoginname = jsonObjectUser.getString("app_user_loginname");
        String insertSql = SqlEasy.insertObject(appUserInfo, "app_user");
        ExecResult execResult = jsonResponse.getExecInsertId(insertSql, null, "", "");
        JSONArray jsonArray = JSON.parseArray(appUserProgram);
        List list = new ArrayList();
        for (int i = 0, jsonArrayLen = jsonArray.size(); i < jsonArrayLen; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String programName = jsonObject.getString("programName");
            String insertUserProgram = "INSERT INTO  app_user_program (app_user_loginname, app_program_name) VALUES ('" + appUserLoginname + "','" + programName + "')";
            execResult = jsonResponse.getExecInsertId(insertUserProgram, null, "", "");
            int programId = Integer.parseInt(execResult.getMessage(), 10);
            JSONArray jsonArrayModule = jsonObject.getJSONArray("programModule");
            for (int j = 0, jsonArrayModuleLen = jsonArrayModule.size(); j < jsonArrayModuleLen; j++) {
                list.add("INSERT INTO app_user_program_module (app_program_id, app_module_id) VALUES (" + programId + ", " + jsonArrayModule.get(j) + ")");
            }
        }
        list.add("INSERT INTO app_user_area (user_loginname, user_area_id) VALUES ('" + appUserLoginname + "','" + areaId + "') ");
        jsonResponse.getExecResult(list, "", "");
        return execResult;
    }

    @Override
    public ExecResult deleteAppUser(String appUserLoginName) {
        String[] appUserLoginNames = appUserLoginName.split(",");
        List list = new ArrayList();
        for (int i = 0, appUserLoginNamesLen = appUserLoginNames.length; i < appUserLoginNamesLen; i++) {
            list.add("DELETE FROM app_user_area WHERE user_loginname = '" + appUserLoginNames[i] + "'");
            list.add("DELETE FROM app_user_program WHERE app_user_loginname = '" + appUserLoginNames[i] + "'");
            list.add("DELETE FROM app_user WHERE user_loginname = '" + appUserLoginNames[i] + "'");
        }
        ExecResult execResult = jsonResponse.getExecResult(list, "", "'");
        return execResult;
    }

    @Override
    public ExecResult updateAppUser(String appUserLoginName, String appUserInfo, String appUserProgram) {
        return null;
    }

    @Override
    public JSONArray getAllAppUserModule(String areaId) {
        String[] areaIds = areaId.split(",");
        List list = new ArrayList();
        list.add("b.user_area_id = '" + areaIds[0] + "'");
        for (int i = 1, areaIdsLen = areaIds.length; i < areaIdsLen; i++) {
            list.add(" OR b.user_area_id = '" + areaIds[i] + "'");
        }
        String getSql = "SELECT a.*,b.user_area_id,c.id AS programId,c.app_program_name FROM app_user a  " +
                "LEFT JOIN app_user_area b  " +
                "ON a.app_user_loginname = b.user_loginname " +
                "LEFT JOIN  app_user_program c  " +
                "ON a.app_user_loginname = c.app_user_loginname WHERE " + StringUtils.join(list, "") + " " +
                " GROUP BY a.id ORDER BY a.app_user_time DESC";

        ExecResult execResult = jsonResponse.getSelectResult(getSql, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        return jsonArray;
    }
}
