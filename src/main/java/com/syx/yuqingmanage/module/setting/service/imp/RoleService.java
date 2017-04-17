package com.syx.yuqingmanage.module.setting.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.setting.service.IRoleService;
import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Master  Zg on 2016/11/16.
 */
@Service
public class RoleService implements IRoleService {
    @Autowired
    private JSONResponse jsonResponse;

    @Override
    public ExecResult getAllRole() {
        String sql = "SELECT * FROM sys_role";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        return execResult;
    }

    @Override
    public ExecResult deleteRole(String id) {
        List<String> list = new ArrayList<>();
        list.add(" DELETE FROM sys_role WHERE id= " + id);
        list.add(" DELETE FROM sys_role_menu WHERE role_id = " + id);
        ExecResult execResult = jsonResponse.getExecResult(list, "", "");
        return execResult;
    }

    @Override
    public ExecResult getSingleRole(String id) {
        String sql = "SELECT * FROM sys_menu WHERE menu_pid = '0' ";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONArray jsonArrayFirstModule = (JSONArray) execResult.getData();
        List<String> list = new ArrayList<>();
        list.add("SELECT * FROM ");
        list.add("(SELECT a.*,b.menu_name AS menu_first_name FROM sys_menu a,sys_menu b  ");
        list.add("WHERE a.menu_pid<>0 AND a.menu_pid = b.menu_id) a  ");
        list.add("LEFT JOIN ");
        list.add("(SELECT b.menu_id AS judge_menu_id FROM sys_role a,sys_role_menu b  ");
        list.add("WHERE a.id = b.role_id AND a.id = '" + id + "') b  ");
        list.add("ON a.menu_id = b.judge_menu_id ");
        String sql_ = StringUtils.join(list, "");
        execResult = jsonResponse.getSelectResult(sql_, null, "");
        JSONArray jsonArrayAllModule = (JSONArray) execResult.getData();
        JSONArray data = new JSONArray();
        for (int i = 0, firstLen = jsonArrayFirstModule.size(); i < firstLen; i++) {
            JSONObject jsonObject = jsonArrayFirstModule.getJSONObject(i);
            JSONArray jsonArray = new JSONArray();
            String menu_id = jsonObject.getString("menu_id");
            for (int j = 0, allLen = jsonArrayAllModule.size(); j < allLen; j++) {
                jsonObject = jsonArrayAllModule.getJSONObject(j);
                if (menu_id.equals(jsonObject.getString("menu_pid"))) {
                    jsonArray.add(jsonObject);
                }
            }
            data.add(jsonArray);

        }
        execResult.setData(data);

        return execResult;
    }

    @Override
    public ExecResult changeRole(String role_id, String menu_id, String menu_pid, String menu_purview) {
        ExecResult returnResult = new ExecResult();
        if ("0".equals(menu_purview)) {
            //添加的逻辑
            String sql = "SELECT * FROM sys_role_menu WHERE role_id = '" + role_id + "'AND menu_id = '" + menu_pid + "' ";
            ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
            List<String> list = new ArrayList<>();
            String sql_ = "INSERT INTO sys_role_menu (role_id,menu_id) VALUES('" + role_id + "','" + menu_id + "')";
            list.add(sql_);
            if (execResult.getResult() == 0) {
                sql_ = "INSERT INTO sys_role_menu (role_id,menu_id) VALUES('" + role_id + "','" + menu_pid + "')";
                list.add(sql_);
            }
            returnResult = jsonResponse.getExecResult(list, "", "");
        } else {
            //取消的逻辑的逻辑
            String deleteSql = "DELETE FROM sys_role_menu WHERE role_id = '" + role_id + "'AND menu_id = '" + menu_id + "' ";
            jsonResponse.getExecResult(deleteSql, null);
            List<String> list = new ArrayList<>();
            list.add("SELECT * FROM sys_menu a ,sys_role_menu b WHERE a.menu_pid ='");
            list.add(menu_pid);
            list.add("' AND a.menu_id = b.menu_id AND b.role_id='");
            list.add(role_id);
            list.add("'");
            String selectSql = StringUtils.join(list, "");
            returnResult = jsonResponse.getSelectResult(selectSql, null, "");
            if (returnResult.getResult() == 0) {
                deleteSql = "DELETE FROM sys_role_menu WHERE role_id = '" + role_id + "' AND menu_id ='" + menu_pid + "'";
                jsonResponse.getExecResult(deleteSql, null);
            }
        }
        return returnResult;
    }

    @Override
    public ExecResult insertRole(String role_name) {
        JSONArray jsonArray = JSON.parseArray(role_name);
        List<String> sqlList = new ArrayList<>();
        for (int i = 0, jsonlength = jsonArray.size(); i < jsonlength; i++) {
            String sql = "insert into sys_role (role_name) values ('" + jsonArray.getString(i) + "')";
            sqlList.add(sql);
        }
        ExecResult execResult = jsonResponse.getExecResult(sqlList, "", "");
        return execResult;
    }

    @Override
    public ExecResult updateRoleName(String roleId, String roleName) {
        String sql = "UPDATE sys_role  SET  role_name = '" + roleName + "'  WHERE id = '" + roleId + "' ";
        ExecResult execResult = jsonResponse.getExecResult(sql, null);
        return execResult;
    }

    @Override
    public ExecResult getUserRole(String roleId) {
        String sql = "SELECT b.* FROM sys_role_user a,sys_user b WHERE a.role_id = '" + roleId + "' AND a.`user_id` = b.`user_loginname`";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        return execResult;
    }
}
