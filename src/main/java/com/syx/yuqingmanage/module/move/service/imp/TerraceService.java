package com.syx.yuqingmanage.module.move.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.move.service.ITerraceService;
import com.syx.yuqingmanage.utils.SqlEasy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Msater Zg on 2017/3/6.
 */
@Service
public class TerraceService implements ITerraceService {
    @Autowired
    private JSONResponse jsonResponse;

    @Override
    public ExecResult insertTerrace(String terraceData, String moduleData, String areaId) {
        String sqlInsert = SqlEasy.insertObject(terraceData, "sys_terrace");
        ExecResult execResult = jsonResponse.getExecInsertId(sqlInsert, null, "", "");
        int terraceId = Integer.parseInt(execResult.getMessage());
        List<String> list = new ArrayList<>();
        String sqlArea = " INSERT INTO sys_terrace_area (area_id,terrace_id) VALUES(" + areaId + "," + terraceId + ") ";
        list.add(sqlArea);
        String[] moduleDatas = moduleData.split(",");
        int moduleDatasLen = moduleDatas.length;
        for (int i = 0; i < moduleDatasLen; i++) {
            String sqlModule = "INSERT INTO terrace_module (terrace_id,module_id) VALUES(" + terraceId + "," + moduleDatas[i] + ")";
            list.add(sqlModule);
        }
        execResult = jsonResponse.getExecResult(list, "", "");
        return execResult;
    }

    @Override
    public JSONObject getAllTerrace(String areaId) {
        String sql = "";
        String[] areaIds = areaId.split(",");
        int areaIdsLen = areaIds.length;
        List<String> sqlArea = new ArrayList<>();
        for (int i = 0; i < areaIdsLen; i++) {
            if (i == 0) {
                sqlArea.add(" a.area_id =  " + areaIds[i]);
            } else {
                sqlArea.add(" OR a.area_id =  " + areaIds[i]);
            }

        }
        List<String> listSql = new ArrayList<>();
        listSql.add(" SELECT a.*,GROUP_CONCAT(a.module_id) AS module_ids,GROUP_CONCAT(a.terrace_module_name) ");
        listSql.add(" AS terrace_module_names ");
        listSql.add(" FROM (SELECT b.*,c.module_id,d.terrace_module_name,e.user_name ");
        listSql.add(" FROM sys_terrace_area a , sys_terrace b,terrace_module c,sys_terrace_module d,sys_user e ");
        listSql.add(" WHERE ( " + StringUtils.join(sqlArea, "") + " ) AND a.terrace_id=b.id AND b.id = c.terrace_id AND c.module_id = d.id ");
        listSql.add(" AND b.terrace_create = e.user_loginname) a GROUP BY a.id ORDER BY a.terrace_time DESC ");
        sql = StringUtils.join(listSql, "");
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = (JSONArray) execResult.getData();
        if (jsonArray == null) {
            jsonObject.put("total", 0);
        } else {
            jsonObject.put("total", jsonArray.size());
        }
        jsonObject.put("data", jsonArray);
        return jsonObject;
    }

    @Override
    public ExecResult updateTerrace(String terraceData, String moduleData, String terraceId) {
        List<String> sqlList = new ArrayList<>();
        String sql = SqlEasy.updateObject(terraceData, "sys_terrace", " id = " + terraceId);
        String[] moduleDataS = moduleData.split(",");
        sqlList.add(sql);
        sqlList.add("DELETE FROM terrace_module WHERE terrace_id = " + terraceId);
        int moduleDataSLen = moduleDataS.length;
        for (int i = 0; i < moduleDataSLen; i++) {
            sqlList.add("INSERT INTO terrace_module (terrace_id,module_id) VALUES(" + terraceId + "," + moduleDataS[i] + ")");
        }
        ExecResult execResult = jsonResponse.getExecResult(sqlList, "", "");
        return execResult;
    }

    @Override
    public ExecResult deleteTerrace(String terraceId) {
        List<String> list = new ArrayList<>();
        String[] terraceIdS = terraceId.split(",");
        int terraceIdSLen = terraceIdS.length;
        for (int i = 0; i < terraceIdSLen; i++) {
            String deleteTerrace = " DELETE FROM sys_terrace WHERE id = " + terraceIdS[i];
            String terraceArea = " DELETE FROM sys_terrace_area WHERE terrace_id = " + terraceIdS[i];
            String terraceModule = " DELETE FROM terrace_module WHERE terrace_id =  " + terraceIdS[i];
            list.add(deleteTerrace);
            list.add(terraceArea);
            list.add(terraceModule);
        }
        ExecResult execResult = jsonResponse.getExecResult(list, "", "");
        return execResult;
    }

    @Override
    public JSONObject getAllTerraceChoose(String areaId, String terraceChooseData) {
        String[] areaIds = areaId.split(",");
        int areaIdLen = areaIds.length;
        List<String> listAreaId = new ArrayList<>();
        List<String> listChoose = new ArrayList<>();
        for (int i = 0; i < areaIdLen; i++) {
            if (i == 0) {
                listAreaId.add(" a.area_id = " + areaIds[i]);
            } else {
                listAreaId.add(" OR a.area_id = " + areaIds[i]);
            }
        }
        JSONObject jsonObject = JSON.parseObject(terraceChooseData);
        Set<String> set = jsonObject.keySet();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String value = iterator.next();
            listChoose.add(" AND a." + value + " LIKE '%" + jsonObject.getString(value) + "%' ");
        }
        List<String> list = new ArrayList<>();
        list.add(" SELECT b.* FROM (SELECT a.id FROM (SELECT a.*,b.terrace_module_name,d.user_name,c.area_id ");
        list.add(" FROM sys_terrace a,sys_terrace_module b ,sys_terrace_area c ,sys_user d,terrace_module e ");
        list.add(" WHERE c.terrace_id=a.id AND a.id = e.terrace_id ");
        list.add(" AND e.module_id = b.id AND a.terrace_create = d.user_loginname)  a ");
        list.add(" WHERE ( " + StringUtils.join(listAreaId, "") + " ) " + StringUtils.join(listChoose, "") + " GROUP BY a.id) a, ");
        list.add(" (SELECT a.*,GROUP_CONCAT(a.module_id) AS module_ids,GROUP_CONCAT(a.terrace_module_name) AS terrace_module_names ");
        list.add(" FROM (SELECT b.*,c.module_id,d.terrace_module_name,e.user_name ");
        list.add(" FROM sys_terrace_area a , sys_terrace b,terrace_module c,sys_terrace_module d,sys_user e ");
        list.add(" WHERE  a.terrace_id=b.id AND b.id = c.terrace_id AND c.module_id = d.id ");
        list.add(" AND b.terrace_create = e.user_loginname) a GROUP BY a.id ORDER BY a.terrace_time DESC ) b ");
        list.add(" WHERE a.id = b.id ");
        String sql = StringUtils.join(list, "");
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONObject jsonObjectData = new JSONObject();
        JSONArray jsonArray = (JSONArray) execResult.getData();
        if (jsonArray == null) {
            jsonObjectData.put("total", 0);
        } else {
            jsonObjectData.put("total", jsonArray.size());
        }
        jsonObjectData.put("data", jsonArray);
        return jsonObjectData;
    }
}
