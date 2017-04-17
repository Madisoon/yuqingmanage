package com.syx.yuqingmanage.module.move.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.move.service.IConfigureService;
import com.syx.yuqingmanage.utils.SqlEasy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Master  Zg on 2016/11/9.
 */
@Service
public class ConfigureService implements IConfigureService {
    @Autowired
    private JSONResponse jsonResponse;

    @Override
    public ExecResult addQq(String qqDate) {
        String sql = SqlEasy.insertObject(qqDate, "sys_qq");
        ExecResult execResult = jsonResponse.getExecResult(sql, null);
        return execResult;
    }

    @Override
    public JSONObject getAllQq() {
        String sql = "SELECT * FROM sys_qq";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = (JSONArray) execResult.getData();
        jsonObject.put("data", jsonArray);
        jsonObject.put("total", jsonArray.size());
        return jsonObject;
    }

    @Override
    public ExecResult deleteQq(String idData) {
        String[] idS = idData.split(",");
        int idSLen = idS.length;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < idSLen; i++) {
            list.add("DELETE FROM sys_qq WHERE id=" + idS[i] + "");
        }
        ExecResult execResult = jsonResponse.getExecResult(list, "", "");
        return execResult;
    }

    @Override
    public ExecResult updateQq(String qqData, String id) {
        String sql = SqlEasy.updateObject(qqData, "sys_qq", "id = " + id + "");
        ExecResult execResult = jsonResponse.getExecResult(sql, null);
        return execResult;
    }

    @Override
    public ExecResult addPlan(String planData) {
        String sql = SqlEasy.insertObject(planData, "sys_plan");
        ExecResult execResult = jsonResponse.getExecResult(sql, null);
        return execResult;
    }

    @Override
    public JSONObject getAllPlan() {
        String sql = "SELECT * FROM sys_plan";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = (JSONArray) execResult.getData();
        jsonObject.put("data", jsonArray);
        jsonObject.put("total", jsonArray.size());
        return jsonObject;
    }

    @Override
    public ExecResult deletePlan(String idData) {
        String[] idS = idData.split(",");
        int idSLen = idS.length;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < idSLen; i++) {
            list.add("DELETE FROM sys_plan WHERE id=" + idS[i] + "");
        }
        ExecResult execResult = jsonResponse.getExecResult(list, "", "");
        return execResult;
    }

    @Override
    public ExecResult updatePlan(String planData, String id) {
        String sql = SqlEasy.updateObject(planData, "sys_plan", "id = " + id + "");
        ExecResult execResult = jsonResponse.getExecResult(sql, null);
        return execResult;
    }
}
