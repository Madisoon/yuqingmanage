package com.syx.yuqingmanage.module.user.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.user.service.ICustomerService;
import com.syx.yuqingmanage.utils.SqlEasy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Msater Zg on 2016/12/22.
 */
@Service
public class CustomerService implements ICustomerService {
    @Autowired
    private JSONResponse jsonResponse;

    @Override
    public JSONObject getAllCustomer() {
        String sql = "SELECT *,COUNT(b.custom_name_id) AS yuqinguser_number FROM sys_customer a LEFT JOIN yuqing_user b ON  a.id = b.custom_name_id GROUP BY a.customer_name ";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = (JSONArray) execResult.getData();
        jsonObject.put("data", jsonArray);
        jsonObject.put("total", jsonArray.size());
        return jsonObject;
    }

    @Override
    public ExecResult deleteCustomer(String ids) {
        String[] idArray = ids.split("@");
        int idArrayLen = idArray.length;
        List<String> deleteSql = new ArrayList<>();
        for (int i = 0; i < idArrayLen; i++) {
            deleteSql.add(" DELETE FROM sys_customer WHERE id='" + idArray[i] + "' ");
            //同时需要修改接口
            deleteSql.add(" UPDATE yuqing_user a SET a.custom_name_id = '' WHERE a.custom_name_id = '" + idArray[i] + "' ");
        }
        ExecResult execResult = jsonResponse.getExecResult(deleteSql, "", "");
        return execResult;
    }

    @Override
    public ExecResult insertCustomer(String customerInfo) {
        JSONObject jsonObject = JSONObject.parseObject(customerInfo);
        Set<String> set = jsonObject.keySet();
        Iterator<String> iterator = set.iterator();
        List<String> sqlListInsert = new ArrayList<>();
        sqlListInsert.add("INSERT INTO sys_customer (");
        List<String> sqlListValues = new ArrayList<>();
        sqlListValues.add(" VALUES(");
        int i = 0;
        while (iterator.hasNext()) {
            String valueText = iterator.next();
            if (i == 0) {
                sqlListInsert.add("" + valueText + "");
                sqlListValues.add("'" + jsonObject.getString(valueText) + "'");
            } else {
                sqlListInsert.add("," + valueText + "");
                sqlListValues.add(",'" + jsonObject.getString(valueText) + "'");
            }
            i++;
        }
        sqlListInsert.add(")");
        sqlListValues.add(")");
        List<String> endSql = new ArrayList<>();
        endSql.add(StringUtils.join(sqlListInsert, ""));
        endSql.add(StringUtils.join(sqlListValues, ""));
        String sql = StringUtils.join(endSql, "");
        System.out.println(

        );
        ExecResult execResult = jsonResponse.getExecResult(sql, null);
        return execResult;
    }

    @Override
    public ExecResult changeCustomer(String customerInfo, String customerId) {
        String changeSql = SqlEasy.updateObject(customerInfo, "sys_customer", " id =  " + customerId);
        ExecResult execResult = jsonResponse.getExecResult(changeSql, null);
        return execResult;
    }

}
