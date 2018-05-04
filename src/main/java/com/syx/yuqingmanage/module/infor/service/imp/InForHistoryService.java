package com.syx.yuqingmanage.module.infor.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.infor.service.IInForHistoryService;
import com.syx.yuqingmanage.utils.DataExport;
import com.syx.yuqingmanage.utils.SqlEasy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Msater Zg on 2017/4/20.
 */
@Service
public class InForHistoryService implements IInForHistoryService {
    @Autowired
    private JSONResponse jsonResponse;
    private DataExport dataExport = new DataExport();

    @Override
    public JSONObject getAllHistory(String pageNumber, String pageSize) {
        int pageNumberInt = Integer.parseInt(pageNumber, 10);
        int pageSizeInt = Integer.parseInt(pageSize, 10);
        String sql = "SELECT COUNT(a.id) AS total FROM sys_manual_post a WHERE a.infor_status = 1  ";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONArray dataAll = (JSONArray) execResult.getData();
        JSONObject jsonObject1 = dataAll.getJSONObject(0);
        StringBuilder dataSql = new StringBuilder();
        dataSql.append(" SELECT a.infor_consumer,a.gmt_create,a.gmt_modified,b.infor_title,b.infor_context,c.user_name FROM sys_manual_post a, sys_infor b," +
                "sys_user c WHERE a.infor_status = 1 AND a.infor_id = b.id AND a.infor_people = c.user_loginname ORDER BY  a.gmt_modified DESC LIMIT  " + ((pageNumberInt - 1) * pageSizeInt) + "," + pageSizeInt + "");
        ExecResult execResult1 = jsonResponse.getSelectResult(dataSql.toString(), null, "");
        JSONArray jsonArray = (JSONArray) execResult1.getData();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", jsonArray);
        jsonObject.put("total", jsonObject1.getInteger("total"));
        return jsonObject;
    }

    @Override
    public JSONObject getChoiceHistory(String pageNumber, String pageSize, String tableChoiceData, String timeOrderType) {
        String timeOrderTypeStr = "1";
        JSONObject jsonObject = JSON.parseObject(tableChoiceData);
        String finishTime = jsonObject.getString("infor_finish_time");
        jsonObject.remove("infor_finish_time");
        JSONObject jsonObjectReturn = new JSONObject();
        String sqlData = "";
        String sqlTotal = "";
        if (jsonObject.isEmpty()) {
            String[] finishTimeS = finishTime.split("&");
            // 只有时间选择
            sqlTotal = "SELECT a.infor_consumer,a.gmt_create,a.gmt_modified,b.infor_title,b.infor_context,b.infor_link,b.infor_site, b.infor_source,c.user_name " +
                    "FROM sys_manual_post a, sys_infor b, " +
                    "sys_user c WHERE a.infor_status = 1 AND a.infor_id = b.id AND a.infor_people = c.user_loginname AND " +
                    "a.gmt_modified>'" + finishTimeS[0] + "' AND a.gmt_modified < '" + finishTimeS[1] + "' ORDER BY a.gmt_modified DESC ";
            sqlData = sqlTotal + SqlEasy.limitPage(pageSize, pageNumber);
        } else {
            Set set = jsonObject.keySet();
            Iterator<String> iterator = set.iterator();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT a.infor_consumer,a.gmt_create,a.gmt_modified,b.infor_title,b.infor_context,b.infor_link,b.infor_site, b.infor_source,c.user_name " +
                    "FROM sys_manual_post a, sys_infor b, " +
                    "sys_user c WHERE a.infor_status = 1 AND a.infor_id = b.id AND a.infor_people = c.user_loginname " +
                    "AND ");
            int i = 0;
            while (iterator.hasNext()) {
                String value = iterator.next();
                if (i == 0) {
                    stringBuilder.append(" a." + value + " LIKE '%" + jsonObject.getString(value) + "%' ");
                } else {
                    stringBuilder.append("AND a." + value + " LIKE '%" + jsonObject.getString(value) + "%' ");
                }
                i++;
            }
            if (!"".equals(finishTime) && finishTime != null) {
                String[] finishTimeS = finishTime.split("&");
                stringBuilder.append("AND a.gmt_modified > '" + finishTimeS[0] + "' AND a.gmt_modified < '" + finishTimeS[1] + "' ");
            }
            if (timeOrderTypeStr.equals(timeOrderType)) {
                stringBuilder.append("ORDER BY a.gmt_modified DESC ");
            } else {
                stringBuilder.append("ORDER BY a.gmt_modified ");
            }
            sqlTotal = stringBuilder.toString();
            System.out.println(sqlTotal);
            sqlData = sqlTotal + SqlEasy.limitPage(pageSize, pageNumber);
        }
        ExecResult execResult = jsonResponse.getSelectResult(sqlData, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        execResult = jsonResponse.getSelectResult(sqlTotal, null, "");
        JSONArray total = (JSONArray) execResult.getData();
        if (total == null) {
            jsonObjectReturn.put("total", 0);
        } else {
            jsonObjectReturn.put("total", total.size());
        }
        jsonObjectReturn.put("data", jsonArray);
        return jsonObjectReturn;
    }

    @Override
    public JSONObject exportHistoryInfor(String searchData, String exportType) {
        JSONObject returnJsonObject = new JSONObject();
        JSONObject jsonObjectData = JSON.parseObject(searchData);
        String customerName = jsonObjectData.getString("infor_consumer");
        JSONObject jsonObject = getChoiceHistory("1", "2000", searchData, "0");
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        if (jsonArray == null) {
            returnJsonObject.put("result", "");
        } else {
            String url = dataExport.exeportHistoryInfor(jsonArray, customerName, exportType);
            returnJsonObject.put("result", url);
        }
        return returnJsonObject;
    }
}
