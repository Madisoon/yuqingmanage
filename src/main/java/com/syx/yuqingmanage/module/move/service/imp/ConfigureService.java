package com.syx.yuqingmanage.module.move.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.move.service.IConfigureService;
import com.syx.yuqingmanage.utils.HttpClientUtil;
import com.syx.yuqingmanage.utils.SqlEasy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            list.add("DELETE FROM sys_qq WHERE id=" + idS[i] + " ");
            list.add("UPDATE sys_post_customer a SET a.customer_post_qq = '0' WHERE a.id = " + idS[i]);
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

    @Override
    public ExecResult addWx(String wxDate) {
        String addWx = SqlEasy.insertObject(wxDate, "sys_weixin");
        ExecResult execResult = jsonResponse.getExecResult(addWx, null);
        return execResult;
    }

    @Override
    public JSONObject getAllWx() {
        String getAllWxSql = "SELECT * FROM sys_weixin";
        ExecResult execResult = jsonResponse.getSelectResult(getAllWxSql, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        JSONObject jsonObject = new JSONObject();
        if (jsonArray == null) {
            jsonObject.put("total", 0);
        } else {
            jsonObject.put("total", jsonArray.size());
        }
        jsonObject.put("data", jsonArray);
        return jsonObject;
    }

    @Override
    public ExecResult deleteWx(String idData) {
        String[] idDatas = idData.split(",");
        int idDatasLen = idDatas.length;
        List list = new ArrayList();
        for (int i = 0; i < idDatasLen; i++) {
            list.add("DELETE FROM sys_weixin WHERE id = " + idDatas[i]);
        }
        ExecResult execResult = jsonResponse.getExecResult(list, "", "");
        return execResult;
    }

    @Override
    public ExecResult updateWx(String wxData, String id) {
        String updateSql = SqlEasy.updateObject(wxData, "sys_weixin", "id = " + id);
        ExecResult execResult = jsonResponse.getExecResult(updateSql, null);
        return execResult;
    }

    @Override
    public JSONObject postAnnouncement(String title, String content) {
        Map map = new HashMap();
        JSONObject jsonObjectMap = new JSONObject();
        jsonObjectMap.put("title", title);
        jsonObjectMap.put("content", content);
        Map mapParam = new HashMap();
        mapParam.put("data", jsonObjectMap.toString());
        JSONObject jsonObject = HttpClientUtil.sendPost("http://yq.yuwoyg.com:8080/yuqing-app-dict/app/notice/insert", mapParam);
        System.out.println(jsonObject);
        return jsonObject;
    }

    @Override
    public JSONObject getAnnouncement(String filters, String start, String limit) {
        Map map = new HashMap();
        map.put("filters", filters);
        map.put("start", start);
        map.put("limit", limit);
        JSONObject jsonObject = HttpClientUtil.sendPost("http://yq.yuwoyg.com:8080/yuqing-app-dict/app/notice/search", map);
        JSONArray jsonArray = jsonObject.getJSONArray("value");
        JSONObject returnJsonobject = new JSONObject();
        returnJsonobject.put("data", jsonArray);
        return returnJsonobject;
    }

    @Override
    public JSONObject resetAnnouncement() {
        Map map = new HashMap();
        JSONObject jsonObject = HttpClientUtil.sendPost("http://yq.yuwoyg.com:8080/yuqing-app-dict/app/notice/reset", map);
        return jsonObject;
    }

    public static void main(String[] args) {
        ConfigureService configureService = new ConfigureService();
        JSONObject jsonObject = configureService.getAnnouncement("", "0", "20");
        System.out.println(jsonObject);
    }
}
