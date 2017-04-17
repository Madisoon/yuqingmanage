package com.syx.yuqingmanage.module.setting.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.setting.service.IModuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Master  Zg on 2016/11/15.
 */
@Service
public class ModuleService implements IModuleService {
    @Autowired
    private JSONResponse jsonResponse;

    @Override
    public ExecResult insertModule(String module_value, String module_url, String id) {
        JSONArray jsonArray = JSON.parseArray(module_value);
        JSONArray jsonArrayUrl = JSON.parseArray(module_url);
        int jsonLength = jsonArray.size();
        List<String> sqlList = new ArrayList<>();
        if ("".equals(id)) {
            for (int i = 0; i < jsonLength; i++) {
                String sql = "INSERT INTO sys_menu (menu_name,menu_type,menu_pid) VALUE('" + jsonArray.get(i) + "','模块','0')";
                sqlList.add(sql);
            }
        } else {
            for (int i = 0; i < jsonLength; i++) {
                String sql = "INSERT INTO sys_menu (menu_name,menu_type,menu_pid,menu_content) VALUE('" + jsonArray.get(i) + "','子功能','" + id + "','" + jsonArrayUrl.get(i) + "')";
                sqlList.add(sql);
            }
        }
        ExecResult execResult = jsonResponse.getExecResult(sqlList, "成功", "失败");
        return execResult;
    }

    @Override
    public ExecResult getAllModule() {
        String sql = "SELECT * FROM sys_menu WHERE menu_type = '模块'";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        return execResult;
    }

    @Override
    public ExecResult deleteModule(String module_id) {
        List<String> list = new ArrayList<>();
        list.add("DELETE FROM sys_menu WHERE menu_id = " + module_id + " OR menu_pid = " + module_id + "  ");
        list.add("DELETE FROM sys_role_menu WHERE menu_id = " + module_id);
        String sql = StringUtils.join(list, "");
        ExecResult execResult = jsonResponse.getExecResult(list, "", "");
        return execResult;
    }

    @Override
    public ExecResult getAllSecondModule(String module_id) {
        List<String> list = new ArrayList<>();
        list.add("SELECT * FROM sys_menu WHERE menu_pid = '");
        list.add(module_id);
        list.add("'");
        String sql = StringUtils.join(list, "");
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        return execResult;
    }

    @Override
    public ExecResult updateModuleInfo(String menuId, String menuName, String menuContent) {
        String sql = "";
        if ("".equals(menuContent)) {
            sql = "UPDATE sys_menu SET menu_name = '" + menuName + "' WHERE menu_id = '" + menuId + "'";
        } else {
            sql = "UPDATE sys_menu SET menu_name = '" + menuName + "',menu_content = '" + menuContent + "' WHERE menu_id = '" + menuId + "'";
        }
        ExecResult execResult = jsonResponse.getExecResult(sql, null);
        return execResult;
    }
}
