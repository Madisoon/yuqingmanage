package com.syx.yuqingmanage.module.user.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.user.service.IFieldService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.applet.Main;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Master  Zg on 2016/11/17.
 */
@Service
public class FieldService implements IFieldService {
    @Autowired
    private JSONResponse jsonResponse;

    @Override
    public ExecResult getAllField(String dataType) {
        //拿到用户所有的字段的类型
        ExecResult execResult = getFieldName();
        JSONArray allType = (JSONArray) execResult.getData();
        //拿到用户所有的字段和字段对应的值
        List<String> list = new ArrayList<>();
        list.add("SELECT a.*,b.`custom_value`,b.id AS value_id FROM sys_custom_field a ");
        list.add(" LEFT JOIN field_value b ON a.id = b.custom_id WHERE a.field_fix <> 2");
        String sqlAllData = StringUtils.join(list, "");
        execResult = jsonResponse.getSelectResult(sqlAllData, null, "");
        JSONArray allData = (JSONArray) execResult.getData();
        JSONArray jsonArrayFix = new JSONArray();
        JSONArray jsonArrayNoFix = new JSONArray();
        for (int i = 0, allTypeLen = allType.size(); i < allTypeLen; i++) {
            //匹配字段
            JSONArray jsonArrayTemporaryFix = new JSONArray();
            JSONArray jsonArrayTemporaryNoFix = new JSONArray();
            JSONObject jsonObject = allType.getJSONObject(i);
            String id = jsonObject.getString("id");
            for (int j = 0, allDataLen = allData.size(); j < allDataLen; j++) {
                jsonObject = allData.getJSONObject(j);
                if (jsonObject.getString("id").equals(id)) {
                    //分类判断是否字段是固定


                    if ("kind".equals(dataType)) {
                        if ("1".equals(jsonObject.getString("field_fix"))) {
                            jsonArrayTemporaryFix.add(jsonObject);
                        } else {
                            jsonArrayTemporaryNoFix.add(jsonObject);
                        }
                    } else {
                        jsonArrayTemporaryFix.add(jsonObject);
                    }
                }
            }
            //在根据数据长度依次放入另一个jsonArray中（1转jsonObject，0不添加防止jsonArray中有空值，1正常添加）
            if (jsonArrayTemporaryFix.size() >= 1) {
                jsonArrayFix.add(jsonArrayTemporaryFix);
            }
            if (jsonArrayTemporaryNoFix.size() >= 1) {
                jsonArrayNoFix.add(jsonArrayTemporaryNoFix);
            }
        }
        //再依次放入jsonObject中
        JSONObject jsonObject = new JSONObject();
        if ("kind".equals(dataType)) {
            jsonObject.put("fix", jsonArrayFix);
            jsonObject.put("noFix", jsonArrayNoFix);
            execResult.setData(jsonObject);
        } else {
            execResult.setData(jsonArrayFix);
        }
        return execResult;
    }

    @Override
    public ExecResult postFieldData(String data, String fieldId) {
        JSONObject jsonObject = JSONObject.parseObject(data);
        String field_title = jsonObject.getString("field_title");
        String field_type = jsonObject.getString("field_type");
        String filed_hint = jsonObject.getString("field_hint");
        String field_name_ = String.valueOf(System.currentTimeMillis());
        String field_name = "t" + field_name_;
        String field_must = jsonObject.getString("field_must");
        String field_fix = jsonObject.getString("field_fix");
        String[] fieldValue = jsonObject.getString("field_value").split("@");
        if ("".equals(fieldId)) {
            //自定义的表名用时间戳
            List<String> list = new ArrayList<>();
            list.add("INSERT INTO sys_custom_field (field_title,field_type,field_hint,field_name,field_must,field_fix) ");
            list.add("VALUES('" + field_title + "','" + field_type + "','" + filed_hint + "','" + field_name + "','" + field_must + "','" + field_fix + "')");
            String sql = StringUtils.join(list, "");
            //给提=用户表添加字段的方法
            String createField = "ALTER TABLE yuqing_user ADD " + field_name + " VARCHAR(100)";
            List<String> listYuqingUser = new ArrayList<>();
            listYuqingUser.add(createField);
            jsonResponse.getExecResult(listYuqingUser, "", "");
            ExecResult execResult = jsonResponse.getExecInsertId(sql, null, "", "");
            if ("input".equals(jsonObject.getString("field_type"))
                    || "inputtime".equals(jsonObject.getString("field_type"))
                    || "textarea".equals(jsonObject.getString("field_type"))) {
            } else {
                String id = execResult.getMessage();
                List<String> listValue = new ArrayList<>();
                for (int i = 0, len = fieldValue.length; i < len; i++) {
                    String valueSql = "INSERT INTO field_value (custom_id,custom_value) VALUES('" + id + "','" + fieldValue[i] + "') ";
                    listValue.add(valueSql);
                }
                execResult = jsonResponse.getExecResult(listValue, "", "");
            }
            return execResult;
        } else {
            List<String> listValue = new ArrayList<>();
            /*String updateMain = "UPDATE sys_custom_filed SET field_title = '"+field_title+"',field_hint = '"+filed_hint+"',field_must = '"+field_must+"' WHERE id='"+fieldId+"'";*/
            List<String> list = new ArrayList<>();
            list.add("UPDATE sys_custom_field SET field_title = '" + field_title + "',field_hint = '" + filed_hint + "'");
            list.add(",field_must = '" + field_must + "' WHERE id='" + fieldId + "'");
            String sqlUpdate = StringUtils.join(list, "");
            listValue.add(sqlUpdate);
            if ("input".equals(field_type)
                    || "inputtime".equals(field_type)
                    || "textarea".equals(field_type)) {
            } else {
                String sql = "DELETE FROM field_value WHERE custom_id = '" + fieldId + "'";
                jsonResponse.getExecResult(sql, null);
                for (int i = 0, len = fieldValue.length; i < len; i++) {
                    String valueSql = "INSERT INTO field_value (custom_id,custom_value) VALUES('" + fieldId + "','" + fieldValue[i] + "') ";
                    listValue.add(valueSql);
                }
            }
            ExecResult execResult = jsonResponse.getExecResult(listValue, "", "");
            return execResult;
        }
    }

    @Override
    public ExecResult deleteField(String filedId) {
        String sqlXq = "SELECT * FROM sys_custom_field WHERE id='" + filedId + "'";
        ExecResult execResult = jsonResponse.getSelectResult(sqlXq, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        JSONObject jsonObject = jsonArray.getJSONObject(0);


        String sql = "DELETE FROM sys_custom_field WHERE id='" + filedId + "'";
        String sqlSensior = "DELETE  FROM field_value WHERE custom_id = '" + filedId + "'";
        String deleteField = "ALTER TABLE yuqing_user DROP COLUMN " + jsonObject.getString("field_name") + " ";
        List<String> listSql = new ArrayList<>();
        listSql.add(sql);
        listSql.add(sqlSensior);
        listSql.add(deleteField);
        execResult = jsonResponse.getExecResult(listSql, "", "");
        return execResult;
    }

    @Override
    public ExecResult getFieldName() {
        String sqlAllType = "SELECT * FROM sys_custom_field";
        ExecResult execResult = jsonResponse.getSelectResult(sqlAllType, null, "");
        return execResult;
    }

    @Override
    public ExecResult getSingleField(String id) {
        ExecResult execResult = new ExecResult();
        List<String> list = new ArrayList<>();
        list.add("SELECT  a.field_title,a.field_type,a.field_hint,a.field_name");
        list.add(",a.field_must,a.field_fix,b.id AS value_id,b.custom_value ");
        list.add("FROM sys_custom_field a ");
        list.add("LEFT JOIN field_value b ON a.id = b.custom_id ");
        list.add("WHERE a.id = '" + id + "' ");
        execResult = jsonResponse.getSelectResult(StringUtils.join(list, ""), null, "");
        return execResult;
    }
}