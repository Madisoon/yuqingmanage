package com.syx.yuqingmanage.module.setting.service.imp;

import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.setting.service.IAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        String sql = "SELECT * FROM sys_area";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        return execResult;
    }
}
