package com.syx.yuqingmanage.module.app.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.app.service.AppNoteService;
import com.syx.yuqingmanage.utils.DataExport;
import com.syx.yuqingmanage.utils.SqlEasy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.MultiKeyJedisClusterCommands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 描述:
 * APP日志接口实现
 *
 * @author Msater Zg
 * @create 2017-11-15 16:51
 */
@Service
public class AppNoteServiceImpl implements AppNoteService {
    @Autowired
    JSONResponse jsonResponse;

    private DataExport dataExport = new DataExport();


    @Override
    public void insertAppNote(String noteTitle, String noteType, String noteModule, String noteCreate) {
        String insertSql = "INSERT INTO sys_app_note (note_title, note_type, note_module, note_create) " +
                "VALUES ('" + noteTitle + "', '" + noteType + "', '" + noteModule + "', '" + noteCreate + "')";
        jsonResponse.getExecResult(insertSql, null);
    }

    @Override
    public JSONObject getAllAppNote(String pageNumber, String pageSize) {
        // 获取日志的数量
        String sqlTotal = "SELECT * FROM sys_app_note a ORDER BY a.note_date DESC ";
        ExecResult execResult = jsonResponse.getSelectResult(sqlTotal, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();

        int pageNumberInt = Integer.parseInt(pageNumber, 10);
        int pageSizeInt = Integer.parseInt(pageSize, 10);
        String sqlPage = "SELECT * FROM sys_app_note a ORDER BY a.note_date DESC  LIMIT " + ((pageNumberInt - 1) * pageSizeInt) + "," + pageSizeInt + "";
        execResult = jsonResponse.getSelectResult(sqlPage, null, "");
        JSONArray jsonArrayData = (JSONArray) execResult.getData();
        JSONObject jsonObject = new JSONObject();
        if (jsonArray == null) {
            jsonObject.put("total", 0);
        } else {
            jsonObject.put("total", jsonArray.size());
        }
        jsonObject.put("data", jsonArrayData);
        return jsonObject;
    }

    @Override
    public JSONObject getAllAppNoteChoose(String chooseData, String pageNumber, String pageSize) {
        String sql = buildAppSelectSql(chooseData);
        JSONArray jsonArray = getAllNoteChooseNotPage(sql);
        int pageNumberInt = Integer.parseInt(pageNumber, 10);
        int pageSizeInt = Integer.parseInt(pageSize, 10);
        sql += "LIMIT " + ((pageNumberInt - 1) * pageSizeInt) + ", " + pageSizeInt + "";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONArray jsonArrayData = (JSONArray) execResult.getData();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", jsonArrayData);
        if (jsonArray == null) {
            jsonObject.put("total", 0);
        } else {
            jsonObject.put("total", jsonArray.size());
        }
        return jsonObject;
    }

    @Override
    public String exportAppNoteExcel(String chooseData) {
        JSONArray jsonArray = getAllNoteChooseNotPage(buildAppSelectSql(chooseData));
        return dataExport.exportAppNoteExcel(jsonArray);
    }

    public JSONArray getAllNoteChooseNotPage(String sql) {
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        return jsonArray;
    }

    public String buildAppSelectSql(String chooseData) {
        JSONObject jsonObject = JSONObject.parseObject(chooseData);
        String chooseTime = jsonObject.getString("note_date");
        jsonObject.remove("note_date");
        Set<String> set = jsonObject.keySet();
        Iterator<String> iterator = set.iterator();
        List<String> list = new ArrayList<>();
        list.add("SELECT * FROM sys_app_note a WHERE");
        int i = 0;
        while (iterator.hasNext()) {
            String valueObject = iterator.next();
            if (i == 0) {
                list.add(" a." + valueObject + " LIKE '%" + jsonObject.getString(valueObject) + "%' ");
            } else {
                list.add(" AND  a." + valueObject + " LIKE '%" + jsonObject.getString(valueObject) + "%' ");
            }
            i++;
        }
        if (jsonObject.isEmpty()) {
            String[] chooseTimes = chooseTime.split(",");
            list.add("  a.note_date >= '" + chooseTimes[0] + "'    AND a.note_date <= '" + chooseTimes[1] + "' ");
        } else {
            if (!"".equals(chooseTime) && chooseTime != null) {
                String[] chooseTimes = chooseTime.split(",");
                list.add(" AND  a.note_date >= '" + chooseTimes[0] + "'    AND a.note_date <= '" + chooseTimes[1] + "' ");
            }
        }
        list.add(" ORDER BY a.note_date DESC ");
        return StringUtils.join(list, "");
    }

}
