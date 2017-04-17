package com.syx.yuqingmanage.module.user.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.user.service.ICustomService;
import com.syx.yuqingmanage.utils.SqlEasy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Master  Zg on 2016/11/21.
 */
@Service
public class CustomService implements ICustomService {
    @Autowired
    private JSONResponse jsonResponse;

    @Override
    public JSONObject getAllCustom(String pageNumber, String pageSize, String choiceSelect) {
        JSONObject jsonObject = new JSONObject();
        int pageNumberInt = Integer.parseInt(pageNumber);
        int pageSizeInt = Integer.parseInt(pageSize);
        //条件为空时，取出所有数据，无查询结果
        if ("".equals(choiceSelect)) {
            String sql = "SELECT * FROM yuqing_user  ";
            ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
            JSONArray jsonArray = (JSONArray) execResult.getData();
            String sqlPage = " SELECT a.*,b.customer_name,b.customer_status FROM (SELECT a.*,b.base_start_time,b.base_end_time,b.base_user_name FROM yuqing_user a ,base_yuqing_user b WHERE a.id = b.base_id LIMIT " + ((pageNumberInt - 1) * pageSizeInt) + "," + pageSizeInt + " ) a LEFT JOIN sys_customer b ON a.custom_name_id = b.id ";
            execResult = jsonResponse.getSelectResult(sqlPage, null, "");
            JSONArray jsonArrayPage = (JSONArray) execResult.getData();
            jsonObject.put("total", jsonArray.size());
            jsonObject.put("data", jsonArrayPage);
            return jsonObject;
        } else {
            //有查询条件
            JSONObject jsonObjectSelect = JSON.parseObject(choiceSelect);
            List<String> sql = new ArrayList<>();
            List<String> sqlPage = new ArrayList<>();
            sql.add(" SELECT * FROM (SELECT a.*,b.customer_name,b.customer_status FROM (SELECT a.*,b.base_start_time,b.base_end_time,b.base_user_name FROM yuqing_user a ,base_yuqing_user b WHERE a.id = b.base_id) a LEFT JOIN sys_customer b ON a.custom_name_id = b.id ) WHERE  ");
            sqlPage.add(" SELECT * FROM (SELECT a.*,b.customer_name,b.customer_status FROM (SELECT a.*,b.base_start_time,b.base_end_time,b.base_user_name FROM yuqing_user a ,base_yuqing_user b WHERE a.id = b.base_id) a LEFT JOIN sys_customer b ON a.custom_name_id = b.id ) WHERE  ");
            Set<String> set = jsonObjectSelect.keySet();
            Iterator<String> iterator = set.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                String choiceValue = iterator.next();
                if (i == 0) {
                    sql.add("a." + choiceValue + " like'%" + jsonObjectSelect.getString(choiceValue) + "%' ");
                    sqlPage.add("a." + choiceValue + " like'%" + jsonObjectSelect.getString(choiceValue) + "%' ");
                } else {
                    sql.add(" AND a." + choiceValue + " like'%" + jsonObjectSelect.getString(choiceValue) + "%' ");
                    sqlPage.add(" AND a." + choiceValue + " like'%" + jsonObjectSelect.getString(choiceValue) + "%' ");
                }
                i++;
            }
            sqlPage.add("  LIMIT " + ((pageNumberInt - 1) * pageSizeInt) + "," + pageSizeInt + "");
            //总的页面
            ExecResult execResult = jsonResponse.getSelectResult(StringUtils.join(sql, ""), null, "");
            if (execResult.getResult() > 0) {
                JSONArray jsonArray = (JSONArray) execResult.getData();
                jsonObject.put("total", jsonArray.size());
            } else {
                jsonObject.put("total", 0);
            }
            execResult = jsonResponse.getSelectResult(StringUtils.join(sqlPage, ""), null, "");
            if (execResult.getResult() > 0) {
                JSONArray jsonArrayPage = (JSONArray) execResult.getData();
                jsonObject.put("data", jsonArrayPage);
            } else {
                jsonObject.put("data", "");
            }
            return jsonObject;
        }
    }

    @Override
    public ExecResult deleteCustom(String idArray) {
        String[] ids = idArray.split("@");
        int idsLen = ids.length;
        List<String> sqlList = new ArrayList<>();
        for (int i = 0; i < idsLen; i++) {

            String sql = "DELETE FROM yuqing_user WHERE id = '" + ids[i] + "'";
            sqlList.add(sql);
        }
        ExecResult execResult = jsonResponse.getExecResult(sqlList, "", "");
        return execResult;
    }

    @Override
    public ExecResult postCustomData(String customData, String customId, String customerInfoId) {
        JSONObject jsonObject = JSONObject.parseObject(customData);
        jsonObject.put("custom_name_id", customerInfoId);
        ExecResult execResult = new ExecResult();
        Set<String> set = jsonObject.keySet();
        Iterator<String> iterator = set.iterator();
        //新增
        if ("0".equals(customId)) {
            String insertSql = SqlEasy.insertObject(customData, "yuqing_user");
            execResult = jsonResponse.getExecResult(insertSql, null);
            return execResult;
        } else {
            //修改UPDATE yuqing_user SET custom_name = '嘻嘻' WHERE id='28'
            List<String> sqlList = new ArrayList<>();
            sqlList.add("UPDATE yuqing_user SET ");
            int i = 0;
            while (iterator.hasNext()) {
                String value = iterator.next();
                if (i == 0) {
                    sqlList.add("" + value + " = '" + jsonObject.getString(value) + "'");
                } else {
                    sqlList.add("," + value + " = '" + jsonObject.getString(value) + "'");
                }
                i++;
            }
            sqlList.add(" WHERE id='" + customId + "'");
            execResult = jsonResponse.getExecResult(StringUtils.join(sqlList, ""), null);
            return execResult;
        }
    }

    @Override
    public ExecResult getDeparmentUser() {
        String depSql = "SELECT a.* FROM sys_deparment a,sys_user b WHERE a.dep_no = b.user_dep  GROUP BY a.id ";
        ExecResult execResult = jsonResponse.getSelectResult(depSql, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        int jsonArrayLen = jsonArray.size();
        JSONArray jsonArrayAllData = new JSONArray();
        for (int i = 0; i < jsonArrayLen; i++) {
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObjectTemporary = new JSONObject();
            jsonObjectTemporary = jsonArray.getJSONObject(i);
            jsonObject.put("id", jsonObjectTemporary.getString("id"));
            jsonObject.put("pId", "../static/img/head.jpg");
            jsonObject.put("name", jsonObjectTemporary.getString("dep_name"));
            jsonArrayAllData.add(jsonObject);
        }
        depSql = "SELECT b.*,a.id AS parentId FROM sys_deparment a,sys_user b WHERE a.dep_no = b.user_dep";
        execResult = jsonResponse.getSelectResult(depSql, null, "");
        jsonArray = (JSONArray) execResult.getData();
        jsonArrayLen = jsonArray.size();
        for (int i = 0; i < jsonArrayLen; i++) {
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObjectTemporary = jsonArray.getJSONObject(i);
            jsonObject.put("id", jsonObjectTemporary.getString("id"));
            jsonObject.put("pId", jsonObjectTemporary.getString("parentid"));
            jsonObject.put("name", jsonObjectTemporary.getString("user_name"));
            jsonObject.put("valname", jsonObjectTemporary.getString("user_name"));
            jsonArrayAllData.add(jsonObject);
        }
        execResult.setData(jsonArrayAllData);
        return execResult;
    }

    @Override
    public JSONObject getAllCustomGroup(String pageNumber, String pageSize) {
        int pageNumberInt = Integer.parseInt(pageNumber);
        int pageSizeInt = Integer.parseInt(pageSize);
        List listNumber = new ArrayList();
        listNumber.add(" SELECT a.custom_name_id, b.customer_name ,b.customer_status,c.base_end_time,c.base_start_time ");
        listNumber.add(" FROM (SELECT * FROM yuqing_user WHERE custom_name_id <> '') a, sys_customer b ,base_yuqing_user c ");
        listNumber.add(" WHERE a.custom_name_id = b.id AND a.id = c.base_id GROUP BY custom_name_id ");
        String sql = StringUtils.join(listNumber, "");
        JSONObject jsonObject = new JSONObject();
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        String sqlPage = "" + sql + "  LIMIT " + ((pageNumberInt - 1) * pageSizeInt) + "," + pageSizeInt + "";
        execResult = jsonResponse.getSelectResult(sqlPage, null, "");
        JSONArray jsonArrayPage = (JSONArray) execResult.getData();
        jsonObject.put("total", jsonArray.size());
        jsonObject.put("data", jsonArrayPage);
        return jsonObject;
    }

    @Override
    public JSONObject getAllCustomerById(String customerId) {
        System.out.println(customerId);
        List<String> list = new ArrayList<>();
        list.add(" SELECT a.*,b.customer_name,b.customer_status,c.base_user_name,c.base_start_time,c.base_end_time  ");
        list.add(" FROM yuqing_user a ,sys_customer b ,base_yuqing_user c  ");
        list.add(" WHERE a.custom_name_id = b.id AND a.id = c.base_id  AND a.custom_name_id = " + customerId);
        String sql = StringUtils.join(list, "");
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = (JSONArray) execResult.getData();
        jsonObject.put("data", jsonArray);
        jsonObject.put("total", jsonArray.size());
        return jsonObject;
    }
}
