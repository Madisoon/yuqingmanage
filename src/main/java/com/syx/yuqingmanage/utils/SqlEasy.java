package com.syx.yuqingmanage.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Msater Zg on 2017/2/7.
 */
public class SqlEasy {
    public static String insertObject(String objectData, String tableName) {
        JSONObject jsonObject = JSONObject.parseObject(objectData);
        Set<String> set = jsonObject.keySet();
        Iterator<String> iterator = set.iterator();
        //新增
        List<String> sqlList = new ArrayList<>();
        List<String> sqlListValues = new ArrayList<>();
        sqlList.add("INSERT INTO " + tableName + " (");
        sqlListValues.add("VALUES(");
        int i = 0;
        while (iterator.hasNext()) {
            String value = iterator.next();
            if (i == 0) {
                sqlList.add(value);
                sqlListValues.add("'" + jsonObject.getString(value) + "'");
            } else {
                sqlList.add("," + value + "");
                sqlListValues.add(",'" + jsonObject.getString(value) + "'");
            }
            i++;
        }
        sqlListValues.add(")");
        sqlList.add(")   ");
        List<String> list = new ArrayList<>();
        list.add(StringUtils.join(sqlList, ""));
        list.add(StringUtils.join(sqlListValues, ""));
        return StringUtils.join(list, "");
    }

    public static String updateObject(String objectData, String tableName, String chooseData) {
        JSONObject jsonObject = JSON.parseObject(objectData);
        Set<String> set = jsonObject.keySet();
        Iterator<String> iterator = set.iterator();
        List<String> list = new ArrayList<>();
        list.add("UPDATE " + tableName + " SET ");
        int i = 0;
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (i == 0) {
                list.add("" + key + " = '" + jsonObject.getString(key) + "' ");
            } else {
                list.add("," + key + " = '" + jsonObject.getString(key) + "' ");
            }
            i++;
        }
        list.add("WHERE  " + chooseData + "");
        return StringUtils.join(list, "");
    }
}
