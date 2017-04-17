package com.syx.yuqingmanage.module.setting.service.imp;

import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.setting.service.IDepService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Master  Zg on 2016/11/8.
 */
@Service
public class DepService implements IDepService {
    @Autowired
    private JSONResponse jsonResponse;

    @Override
    public ExecResult getAllDep() {
        String sql = "SELECT * FROM sys_deparment ORDER BY dep_createtime";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        return execResult;
    }

    @Override
    public ExecResult postDepData(String dep_name, String dep_no) {
        String sql = "INSERT INTO sys_deparment (dep_name,dep_no) VALUES('" + dep_name + "','" + dep_no + "')";
        ExecResult execResult = jsonResponse.getExecResult(sql, null);
        return execResult;
    }

    @Override
    public ExecResult deleteById(String dep_id) {
        String sql = "DELETE FROM sys_deparment WHERE id = '" + dep_id + "' ";
        ExecResult execResult = jsonResponse.getExecResult(sql, null);
        return execResult;
    }

    @Override
    public ExecResult getUserByDepNo(String dep_no) {
        String sql = "SELECT * FROM sys_user a WHERE a.`user_dep` = '" + dep_no + "'";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        return execResult;
    }

    @Override
    public ExecResult updateDep(String depName, String depNo, String depId) {
        List<String> list = new ArrayList<>();
        list.add("UPDATE sys_deparment  SET");
        list.add(" dep_name = '" + depName + "',dep_no = '" + depNo + "' WHERE id = '" + depId + "'");
        //如果改编号，最好用户的编号全部改掉
        ExecResult execResult = jsonResponse.getExecResult(StringUtils.join(list, ""), null);
        return execResult;
    }
}
