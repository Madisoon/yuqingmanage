package com.syx.yuqingmanage.module.move.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.sun.xml.internal.xsom.impl.ListSimpleTypeImpl;
import com.syx.yuqingmanage.module.move.service.IServeService;
import com.syx.yuqingmanage.utils.SqlEasy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Msater Zg on 2017/2/14.
 */
@Service
public class ServeService implements IServeService {
    @Autowired
    private JSONResponse jsonResponse;

    @Override
    public ExecResult insertServeCustomer(String customerData, String getData, String areaId) {
        String sql = SqlEasy.insertObject(customerData, "sys_post_customer");
        ExecResult execResult = jsonResponse.getExecInsertId(sql, null, "", "");
        int id = Integer.parseInt(execResult.getMessage());
        JSONArray getArray = JSON.parseArray(getData);
        int getArrayLen = getArray.size();
        List<String> sqlList = new ArrayList<>();
        for (int i = 0; i < getArrayLen; i++) {
            JSONObject jsonObject = getArray.getJSONObject(i);
            sqlList.add("INSERT INTO sys_customer_get (post_customer_id,get_number,get_remark,get_type) " +
                    "VALUES(" + id + ",'" + jsonObject.getString("get_number") + "','" + jsonObject.getString("get_remark") + "','" + jsonObject.getString("get_type") + "')");
        }
        sqlList.add("INSERT INTO post_customer_area VALUES('" + areaId + "','" + id + "')");
        execResult = jsonResponse.getExecResult(sqlList, "", "");
        return execResult;
    }

    @Override
    public JSONObject getAllServeCustomer(String areaId) {
        String[] areaIds = areaId.split(",");
        int areaIdsLen = areaIds.length;
        List<String> idList = new ArrayList<>();
        for (int i = 0; i < areaIdsLen; i++) {
            if (i == 0) {
                idList.add(" a.area_id = " + areaIds[i]);
            } else {
                idList.add(" OR a.area_id = " + areaIds[i]);
            }
        }
        List<String> listSql = new ArrayList<>();


        listSql.add(" SELECT a.*,GROUP_CONCAT(a.get_number) AS get_numbers, ");
        listSql.add(" GROUP_CONCAT(a.get_remark) AS get_remarks,GROUP_CONCAT(a.get_type) AS get_types ");
        listSql.add(" FROM (SELECT a.*,b.scheme_name FROM (SELECT b.*,d.user_loginname,d.user_name,e.get_number,e.get_remark,e.get_type ");
        listSql.add(" FROM post_customer_area a,sys_post_customer b ,sys_user d, ");
        listSql.add(" sys_customer_get e    WHERE (" + StringUtils.join(idList, "") + ") ");
        listSql.add(" AND a.post_customer_id = b.id  AND b.customer_creater = d.user_loginname AND b.id = e.post_customer_id)a ");
        listSql.add(" LEFT JOIN sys_scheme b ON a.customer_scheme = b.id ) ");
        listSql.add(" a GROUP BY a.id ");
        ExecResult execResult = jsonResponse.getSelectResult(StringUtils.join(listSql, ""), null, "");
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = (JSONArray) execResult.getData();
        jsonObject.put("data", jsonArray);
        if (jsonArray == null) {
            jsonObject.put("total", "0");
        } else {
            jsonObject.put("total", jsonArray.size());
        }
        return jsonObject;
    }

    @Override
    public ExecResult updateServeCustomer(String customerData, String getData, String schemeCustomerId) {
        String sql = SqlEasy.updateObject(customerData, "sys_post_customer", "id = " + schemeCustomerId);
        List<String> sqlList = new ArrayList<>();
        sqlList.add(sql);
        sqlList.add("DELETE FROM sys_customer_get WHERE post_customer_id = " + schemeCustomerId);
        JSONArray getArray = JSON.parseArray(getData);
        int getArrayLen = getArray.size();
        for (int i = 0; i < getArrayLen; i++) {
            JSONObject jsonObject = getArray.getJSONObject(i);
            sqlList.add("INSERT INTO sys_customer_get (post_customer_id,get_number,get_remark,get_type) " +
                    "VALUES(" + schemeCustomerId + ",'" + jsonObject.getString("get_number") + "','" + jsonObject.getString("get_remark") + "','" + jsonObject.getString("get_type") + "')");
        }
        ExecResult execResult = jsonResponse.getExecResult(sqlList, "", "");
        return execResult;
    }

    @Override
    public ExecResult deleteServeCustomer(String serveCustomerId) {
        String[] idS = serveCustomerId.split(",");
        int idSLen = idS.length;
        List<String> deleteList = new ArrayList<>();
        for (int i = 0; i < idSLen; i++) {
            deleteList.add("DELETE FROM sys_post_customer WHERE id = " + idS[i]);
            deleteList.add("DELETE FROM post_customer_area WHERE post_customer_id = " + idS[i]);
            deleteList.add("DELETE FROM sys_customer_get WHERE post_customer_id = " + idS[i]);
        }
        ExecResult execResult = jsonResponse.getExecResult(deleteList, "", "");
        return execResult;
    }

    @Override
    public JSONObject getAllServeCustomerChoose(String areaId, String chooseData) {
        String[] areaIds = areaId.split(",");
        int areaIdsLen = areaIds.length;
        List<String> idList = new ArrayList<>();
        for (int i = 0; i < areaIdsLen; i++) {
            if (i == 0) {
                idList.add(" a.area_id = " + areaIds[i]);
            } else {
                idList.add(" OR a.area_id = " + areaIds[i]);
            }
        }

        JSONObject jsonObject = JSON.parseObject(chooseData);
        Set<String> set = jsonObject.keySet();
        Iterator<String> iterator = set.iterator();
        List<String> chooseChoice = new ArrayList<>();
        int m = 0;
        while (iterator.hasNext()) {
            String keyValue = iterator.next();
            if (m == 0) {
                chooseChoice.add(" a." + keyValue + " LIKE '%" + jsonObject.getString(keyValue) + "%' ");
            } else {
                chooseChoice.add(" AND a." + keyValue + " LIKE '%" + jsonObject.getString(keyValue) + "%' ");
            }
            m++;
        }
        List<String> list = new ArrayList<>();
        list.add(" SELECT b.* FROM (SELECT a.id FROM (SELECT  b.*,d.user_name,e.get_number,e.get_remark,e.get_type, ");
        list.add(" f.scheme_name FROM post_customer_area a,sys_post_customer b,sys_user d,sys_customer_get e, ");
        list.add(" sys_scheme f  WHERE ( " + StringUtils.join(idList, "") + " ) AND a.post_customer_id = b.id ");
        list.add(" AND b.customer_creater = d.user_loginname  AND b.id = e.post_customer_id ");
        list.add(" AND b.customer_scheme = f.id) a  ");
        list.add(" WHERE " + StringUtils.join(chooseChoice, "") + " GROUP BY a.id) a , ");
        list.add(" (SELECT a.*,GROUP_CONCAT(a.get_number) AS get_numbers, ");
        list.add(" GROUP_CONCAT(a.get_remark) AS get_remarks, ");
        list.add(" GROUP_CONCAT(a.get_type) AS get_types ");
        list.add(" FROM (SELECT b.*,d.user_loginname,d.user_name,e.get_number,e.get_remark,e.get_type,f.scheme_name ");
        list.add(" FROM sys_post_customer b ,sys_user d,  sys_customer_get e,sys_scheme f ");
        list.add(" WHERE b.customer_creater = d.user_loginname ");
        list.add(" AND b.id = e.post_customer_id ");
        list.add(" AND b.customer_scheme = f.id) a ");
        list.add(" GROUP BY a.id) b  WHERE a.id = b.id ");
        String sql = StringUtils.join(list, "");
        System.out.println(sql);
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONObject object = new JSONObject();
        JSONArray jsonArray = (JSONArray) execResult.getData();
        object.put("data", jsonArray);
        if (jsonArray == null) {
            object.put("total", "0");
        } else {
            object.put("total", jsonArray.size());
        }
        return object;
    }
}
