package com.syx.yuqingmanage.module.appmodule.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.alienlab.utils.Md5Azdg;
import com.syx.yuqingmanage.module.appmodule.service.ISysAppUserService;
import com.syx.yuqingmanage.utils.SqlEasy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        String appUserLoginName = jsonObjectUser.getString("app_user_loginname");
        jsonObjectUser.put("app_user_pwd", Md5Azdg.md5s(jsonObjectUser.getString("app_user_pwd")));
        String insertSql = SqlEasy.insertObject(jsonObjectUser.toJSONString(), "app_user");
        ExecResult execResult = jsonResponse.getExecInsertId(insertSql, null, "", "");
        List list = insertUserProgram(appUserLoginName, appUserProgram);
        list.add("INSERT INTO app_user_area (user_loginname, user_area_id) VALUES ('" + appUserLoginName + "','" + areaId + "') ");
        jsonResponse.getExecResult(list, "", "");


        return execResult;
    }

    @Override
    public ExecResult deleteAppUser(String appUserLoginName) {
        String[] appUserLoginNames = appUserLoginName.split(",");
        List list = new ArrayList();
        for (int i = 0, appUserLoginNamesLen = appUserLoginNames.length; i < appUserLoginNamesLen; i++) {
            String selectSql = "SELECT id FROM  app_user_program WHERE app_user_loginname = '" + appUserLoginNames[i] + "'";
            ExecResult execResult = jsonResponse.getSelectResult(selectSql, null, "");
            JSONArray jsonArray = (JSONArray) execResult.getData();
            for (int j = 0, jsonArrayLen = jsonArray.size(); j < jsonArrayLen; j++) {
                JSONObject jsonObject = jsonArray.getJSONObject(j);
                list.add("DELETE FROM app_user_program_module  WHERE  app_program_id = " + jsonObject.getString("id"));
            }
            list.add("DELETE FROM app_user_area WHERE user_loginname = '" + appUserLoginNames[i] + "'");
            list.add("DELETE FROM app_user_program WHERE app_user_loginname = '" + appUserLoginNames[i] + "'");
            list.add("DELETE FROM app_user WHERE app_user_loginname = '" + appUserLoginNames[i] + "'");
        }
        ExecResult execResult = jsonResponse.getExecResult(list, "", "'");
        return execResult;
    }

    @Override
    public ExecResult updateAppUser(String appUserLoginName, String appUserInfo, String appUserProgram) {
        String deleteSql = "DELETE FROM app_user_program WHERE app_user_loginname  = '" + appUserLoginName + "'";
        String selectSql = "SELECT id FROM  app_user_program WHERE app_user_loginname = '" + appUserLoginName + "'";
        ExecResult execResult = jsonResponse.getSelectResult(selectSql, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        List list = new ArrayList();
        for (int i = 0, jsonArrayLen = jsonArray.size(); i < jsonArrayLen; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            list.add("DELETE FROM app_user_program_module  WHERE  app_program_id = " + jsonObject.getString("id"));
            list.add("DELETE FROM app_user_config WHERE tag_id = " + jsonObject.getString("id"));
        }
        list.add(deleteSql);
        // 删除频道和模块
        jsonResponse.getExecResult(list, "", "");
        // 开始完成插入和修改
        String updateSql = SqlEasy.updateObject(appUserInfo, "app_user", "app_user_loginname = '" + appUserLoginName + "'");
        List listProgram = insertUserProgram(appUserLoginName, appUserProgram);
        listProgram.add(updateSql);
        execResult = jsonResponse.getExecResult(listProgram, "", "");


        return execResult;
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

    @Override
    public ExecResult getSingleAppUser(String userLoginName) {
        String getSingleUser = "SELECT a.*,GROUP_CONCAT(a.app_module_id) AS app_module_ids, " +
                "GROUP_CONCAT(a.app_module_name) AS app_module_names  " +
                "FROM (SELECT a.*,b.app_module_name FROM  " +
                "(SELECT * FROM  app_user_program a  " +
                "LEFT JOIN app_user_program_module b  " +
                "ON a.id = b.app_program_id WHERE  " +
                "a.app_user_loginname = '" + userLoginName + "') a  " +
                "LEFT JOIN app_module b ON a.app_module_id = b.id) a GROUP BY a.id";
        ExecResult execResult = jsonResponse.getSelectResult(getSingleUser, null, "");
        return execResult;
    }


    public List insertUserProgram(String appUserLoginName, String appUserProgram) {
        List list = new ArrayList();
        ExecResult execResult = new ExecResult();
        JSONArray jsonArray = JSON.parseArray(appUserProgram);
        for (int i = 0, jsonArrayLen = jsonArray.size(); i < jsonArrayLen; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String programName = jsonObject.getString("programName");
            String insertUserProgram = "INSERT INTO  app_user_program (app_user_loginname, app_program_name) VALUES ('" + appUserLoginName + "','" + programName + "')";
            execResult = jsonResponse.getExecInsertId(insertUserProgram, null, "", "");
            int programId = Integer.parseInt(execResult.getMessage(), 10);
            list.add("INSERT INTO app_user_config (tag_id, tag_push, tag_user) VALUES(" + programId + ", 1, '" + appUserLoginName + "') ");
            JSONArray jsonArrayModule = jsonObject.getJSONArray("programModule");
            for (int j = 0, jsonArrayModuleLen = jsonArrayModule.size(); j < jsonArrayModuleLen; j++) {
                list.add("INSERT INTO app_user_program_module (app_program_id, app_module_id) VALUES (" + programId + ", " + jsonArrayModule.get(j) + ")");
            }
        }
        return list;
    }
}
