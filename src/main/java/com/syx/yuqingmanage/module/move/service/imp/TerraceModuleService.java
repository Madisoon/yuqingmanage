package com.syx.yuqingmanage.module.move.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.move.service.ITerraceModuleService;
import com.syx.yuqingmanage.utils.SqlEasy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Msater Zg on 2017/3/3.
 */
@Service
public class TerraceModuleService implements ITerraceModuleService {
    @Autowired
    private JSONResponse jsonResponse;

    @Override
    public ExecResult insertTerraceModule(String terraceData, String tagIds, String areaId, String baseTag) {
        String sql = SqlEasy.insertObject(terraceData, "sys_terrace_module");
        ExecResult execResult = jsonResponse.getExecInsertId(sql, null, "", "");
        int terraceModuleId = Integer.parseInt(execResult.getMessage());
        List<String> sqlList = new ArrayList<>();
        String[] ids = tagIds.split(",");
        int idsLen = ids.length;
        for (int i = 0; i < idsLen; i++) {
            sqlList.add("INSERT INTO sys_terrace_module_tag (terrace_module_id,tag_id) VALUES(" + terraceModuleId + "," + ids[i] + ")");
        }
        String[] baseTagS = baseTag.split(",");
        int baseTagSLen = baseTagS.length;
        Set<String> set = new HashSet<>();
        for (int i = 0; i < baseTagSLen; i++) {
            set.add(baseTagS[i]);
        }
        for (String str : set) {
            sqlList.add("INSERT INTO sys_terrace_module_tag_base (terrace_module_id,tag_id) VALUES(" + terraceModuleId + "," + str + ")");
        }
        sqlList.add("INSERT INTO sys_terrace_module_area (area_id,terrace_module_id) VALUES(" + areaId + "," + terraceModuleId + ")");
        execResult = jsonResponse.getExecResult(sqlList, "", "");
        return execResult;
    }

    @Override
    public JSONObject getAllTerraceModule(String areaId) {
        String[] areaIds = areaId.split(",");
        int areaIdsLen = areaIds.length;
        List<String> idList = new ArrayList<>();
        for (int i = 0; i < areaIdsLen; i++) {
            if (i == 0) {
                idList.add("" + areaIds[i]);
            } else {
                idList.add(" OR a.area_id = " + areaIds[i]);
            }
        }
        List<String> sqlList = new ArrayList<>();

        sqlList.add("SELECT a.* ,GROUP_CONCAT(a.name) AS tag_names,GROUP_CONCAT(a.tag_id) AS tag_ids ");
        sqlList.add(" FROM (SELECT c.*,d.name,d.id AS tag_id,e.user_loginname,e.user_name ,f.plan_name FROM sys_terrace_module_area a , sys_terrace_module_tag b ,sys_terrace_module c,sys_tag d,sys_user e, sys_plan f ");
        sqlList.add("WHERE a.terrace_module_id = c.id AND b.terrace_module_id = c.id  AND b.tag_id = d.id AND c.terrace_module_create=e.user_loginname AND c.terrace_module_plan = f.id  AND (a.area_id = " + StringUtils.join(idList, "") + ")) a GROUP BY a.id");
        String sql = StringUtils.join(sqlList, "");
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = (JSONArray) execResult.getData();
        jsonObject.put("data", jsonArray);
        if (jsonArray == null) {
            jsonObject.put("total", 0);
        } else {
            jsonObject.put("total", jsonArray.size());
        }
        return jsonObject;
    }

    @Override
    public ExecResult deleteTerraceModuleId(String terraceModuleId) {
        String[] idS = terraceModuleId.split(",");
        int idSLen = idS.length;
        List<String> deleteList = new ArrayList<>();
        for (int i = 0; i < idSLen; i++) {
            deleteList.add("DELETE FROM sys_terrace_module WHERE id = " + idS[i]);
            deleteList.add("DELETE FROM sys_terrace_module_area WHERE terrace_module_id=" + idS[i]);
            deleteList.add("DELETE FROM sys_terrace_module_tag WHERE terrace_module_id=" + idS[i]);
            deleteList.add("DELETE FROM sys_terrace_module_tag_base WHERE terrace_module_id=" + idS[i]);
        }
        ExecResult execResult = jsonResponse.getExecResult(deleteList, "", "");
        return execResult;
    }

    @Override
    public ExecResult updateTerraceModule(String terraceId, String tagId, String terraceData, String baseTag) {
        System.out.println("123456789");
        List<String> list = new ArrayList<>();
        String sql = SqlEasy.updateObject(terraceData, "sys_terrace_module", "id = " + terraceId);
        String[] tagIds = tagId.split(",");
        list.add(sql);
        list.add("DELETE FROM sys_terrace_module_tag WHERE terrace_module_id = " + terraceId);
        list.add("DELETE FROM sys_terrace_module_tag_base WHERE terrace_module_id = " + terraceId);
        int tagIdsLen = tagIds.length;
        for (int i = 0; i < tagIdsLen; i++) {
            list.add("INSERT INTO sys_terrace_module_tag (terrace_module_id,tag_id) VALUES(" + terraceId + "," + tagIds[i] + ")");
        }
        String[] baseTagS = baseTag.split(",");
        int baseTagSLen = baseTagS.length;
        Set<String> set = new HashSet<>();
        for (int i = 0; i < baseTagSLen; i++) {
            set.add(baseTagS[i]);
        }
        for (String str : set) {
            list.add("INSERT INTO sys_terrace_module_tag_base (terrace_module_id,tag_id) VALUES(" + terraceId + "," + str + ")");
        }
        ExecResult execResult = jsonResponse.getExecResult(list, "", "");
        return execResult;
    }

    @Override
    public JSONObject getAllTerraceModuleChoose(String areaId, String tagId, String chooseTerraceData) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObjectChoose = JSON.parseObject(chooseTerraceData);
        String[] areaIds = areaId.split(",");
        int areaIdsLen = areaIds.length;
        List<String> areaIdList = new ArrayList<>();
        for (int i = 0; i < areaIdsLen; i++) {
            if (i == 0) {
                areaIdList.add(" a.area_id = " + areaIds[i]);
            } else {
                areaIdList.add(" OR a.area_id = " + areaIds[i]);
            }
        }
        String[] tagIds = tagId.split(",");
        int tagIdsLen = tagIds.length;
        List<String> tagIdList = new ArrayList<>();
        for (int i = 0; i < tagIdsLen; i++) {
            if (i == 0) {
                tagIdList.add(" b.tag_id = " + tagIds[i]);
            } else {
                tagIdList.add(" OR b.`tag_id` = " + tagIds[i]);
            }
        }

        Set<String> set = jsonObjectChoose.keySet();
        Iterator<String> iterator = set.iterator();
        int j = 0;
        List<String> whereList = new ArrayList<>();
        while (iterator.hasNext()) {
            String iteratorValue = iterator.next();
            if (j == 0) {
                whereList.add("WHERE a." + iteratorValue + " LIKE '%" + jsonObjectChoose.getString(iteratorValue) + "%' ");
            } else {
                whereList.add("AND a." + iteratorValue + " LIKE '%" + jsonObjectChoose.getString(iteratorValue) + "%' ");
            }
            j++;
        }
        List<String> list = new ArrayList<>();
        list.add(" SELECT *,GROUP_CONCAT(a.name) AS tag_names,GROUP_CONCAT(a.tagId) AS tag_ids  ");
        list.add(" FROM(SELECT b.*, d.name, d.id AS tagId, e.area_id,f.user_name, g.plan_name  ");
        list.add(" FROM (SELECT a.id FROM sys_terrace_module a,sys_terrace_module_tag b  ");
        if ("".equals(tagId)) {
            list.add(" WHERE a.id = b.terrace_module_id ");
        } else {
            list.add(" WHERE a.id = b.terrace_module_id AND (" + StringUtils.join(tagIdList, "") + ")  ");
        }
        list.add(" GROUP BY a.id) a,sys_terrace_module b,sys_terrace_module_tag c,sys_tag d,sys_terrace_module_area e,sys_user f, sys_plan g ");
        list.add(" WHERE a.id = b.id AND b.id = c.terrace_module_id AND c.tag_id = d.id AND a.id = e.terrace_module_id ");
        list.add(" AND b.terrace_module_create = f.user_loginname AND b.terrace_module_plan = g.id ) a  ");
        list.add(" " + StringUtils.join(whereList, ""));
        System.out.println();
        if (chooseTerraceData == null || "{}".equals(chooseTerraceData)) {
            list.add(" WHERE " + StringUtils.join(areaIdList, "") + " ");
        } else {
            list.add("  AND (" + StringUtils.join(areaIdList, "") + ") ");
        }
        list.add(" GROUP BY a.id  ");
        ExecResult execResult = jsonResponse.getSelectResult(StringUtils.join(list, ""), null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        jsonObject.put("data", jsonArray);
        jsonObject.put("total", jsonArray.size());
        return jsonObject;
    }

    @Override
    public JSONArray getAppModule() {
        String sql = "SELECT id AS tag_id,terrace_module_name AS tag_name FROM sys_terrace_module ";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        return jsonArray;
    }

    @Override
    public JSONArray getAppModuleById(String idS) {
        String[] tagIds = idS.split(",");
        int tagIdLen = tagIds.length;
        List<String> list = new ArrayList<>();
        list.add("SELECT id AS tag_id,terrace_module_name AS tag_name FROM sys_terrace_module a WHERE a.id = " + tagIds[0] + " ");
        for (int i = 1; i < tagIdLen; i++) {
            list.add(" OR a.id = " + tagIds[i] + " ");
        }
        ExecResult execResult = jsonResponse.getSelectResult(StringUtils.join(list, ""), null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        return jsonArray;
    }
}
