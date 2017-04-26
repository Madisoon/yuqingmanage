package com.syx.yuqingmanage.module.setting.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.sun.xml.internal.xsom.impl.ListSimpleTypeImpl;
import com.syx.yuqingmanage.module.setting.service.IAreaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Master  Zg on 2016/11/29.
 */
@Service
public class AreaService implements IAreaService {
    @Autowired
    private JSONResponse jsonResponse;

    @Override
    public ExecResult getTypeArea(String areaId) {
        ExecResult execResult = new ExecResult();
        String sqlArea = "";
        if ("".equals(areaId)) {
            sqlArea = "SELECT * FROM sys_area WHERE area_parent = '0' ";
        } else {
            sqlArea = "SELECT * FROM sys_area WHERE area_parent = '" + areaId + "' ";
        }
        execResult = jsonResponse.getSelectResult(sqlArea, null, "");
        return execResult;
    }

    @Override
    public ExecResult postAreaData(String areaId, String areaValue, String areaGrade) {
        String[] areaValues = areaValue.split("@");
        int areaValueLen = areaValues.length;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < areaValueLen; i++) {
            String sql = "INSERT INTO sys_area (area_name,area_parent,area_grade) " +
                    "VALUES('" + areaValues[i] + "','" + areaId + "','" + areaGrade + "')";
            list.add(sql);
        }
        ExecResult execResult = jsonResponse.getExecResult(list, "", "");
        return execResult;
    }

    @Override
    public ExecResult getAllArea() {
        String sql = "SELECT id,area_name AS NAME, area_parent FROM sys_area";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        return execResult;
    }

    @Override
    public ExecResult updateArea(String areaId, String areaName) {
        String updateArea = "UPDATE sys_area SET area_name = '" + areaName + "' WHERE id = '" + areaId + "'";
        ExecResult execResult = jsonResponse.getExecResult(updateArea, null);
        return execResult;
    }

    @Override
    public ExecResult deleteArea(String areaId) {
        String[] areaIds = areaId.split(",");
        int areaIdLen = areaIds.length;
        List list = new ArrayList();
        list.add("DELETE FROM sys_area WHERE id = '" + areaIds[0] + "'");
        for (int i = 1; i < areaIdLen; i++) {
            list.add(" OR id = '" + areaIds[i] + "' ");
        }
        String deleteSql = StringUtils.join(list, "");
        ExecResult execResult = jsonResponse.getExecResult(deleteSql, null);
        return execResult;
    }

    @Override
    public ExecResult getAreaMaxId() {
        String maxId = "SELECT * FROM  sys_area ORDER BY id DESC LIMIT 0,1 ";
        ExecResult execResult = jsonResponse.getSelectResult(maxId, null, "");
        return execResult;
    }

    @Override
    public ExecResult insertArea(String areaData) {
        JSONObject jsonObject = JSON.parseObject(areaData);
        String name = jsonObject.getString("name");
        String id = jsonObject.getString("id");
        String area_parent = jsonObject.getString("area_parent");
        String insertArea = "INSERT INTO sys_area (id,area_name,area_parent) VALUES('" + id + "','" + name + "','" + area_parent + "') ";
        ExecResult execResult = jsonResponse.getExecResult(insertArea, null);
        return execResult;
    }
}
