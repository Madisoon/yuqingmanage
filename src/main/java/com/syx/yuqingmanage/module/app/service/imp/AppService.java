package com.syx.yuqingmanage.module.app.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.sun.xml.internal.xsom.impl.ListSimpleTypeImpl;
import com.syx.yuqingmanage.module.app.service.IAppService;
import com.syx.yuqingmanage.utils.HttpClientUtil;
import com.syx.yuqingmanage.utils.QqAsyncMessagePost;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Master  Zg on 2016/12/12.
 */
@Service
public class AppService implements IAppService {
    @Autowired
    private JSONResponse jsonResponse;
    @Autowired
    QqAsyncMessagePost qqAsyncMessagePost;

    @Override
    public ExecResult addUser(String userInfo) {
        JSONObject jsonObject = JSONObject.parseObject(userInfo);
        //用uuid来确定数据的唯一性
        String custom_id = jsonObject.getString("customer_id");
        System.out.println(custom_id);
        String custom_uuid = jsonObject.getString("custom_uuid");
        String user_name = jsonObject.getString("user_name");
        String user_start_time = jsonObject.getString("user_start_time");
        String user_finish_time = jsonObject.getString("user_finish_time");
        List<String> list = new ArrayList<>();
        list.add("insert into yuqing_user (id,custom_name_id,user_name,user_start,user_end)");
        list.add("values('" + custom_uuid + "','" + custom_id + "','" + user_name + "','" + user_start_time + "','" + user_finish_time + "')");
        String sql = StringUtils.join(list, "");
        ExecResult execResult = jsonResponse.getExecResult(sql, null);
        return execResult;
    }

    @Override
    public ExecResult deleteUser(String id) {
        System.out.println(id);
        List<String> list = new ArrayList<>();
        list.add("INSERT INTO  delete_custom (id,custom_name_id,user_name,start_time,end_time,custom_status) ");
        list.add(" SELECT a.id, a.custom_name,user_name,user_start,user_end,user_status FROM  yuqing_user a WHERE a.id = '" + id + " '");
        String transiteSql = StringUtils.join(list, "");
        //这个id其实是uuid
        String deleteSql = "DELETE FROM yuqing_user WHERE id = '" + id + " '";
        //事物流，事件回滚
        List<String> listSql = new ArrayList<>();
        listSql.add(transiteSql);
        listSql.add(deleteSql);
        ExecResult execResult = jsonResponse.getExecResult(listSql, "", "");
        return execResult;
    }

    @Override
    public ExecResult updateUser(String userInfo) {
        JSONObject jsonObject = JSONObject.parseObject(userInfo);
        String customer_id = jsonObject.getString("customer_id");
        String user_name = jsonObject.getString("user_name");
        String user_start_time = jsonObject.getString("user_start_time");
        String user_finish_time = jsonObject.getString("user_finish_time");
        String custom_uuid = jsonObject.getString("custom_uuid");
        List<String> list = new ArrayList<>();
        list.add("UPDATE yuqing_user a SET a.custom_name_id = '" + customer_id + "',a.user_name = '" + user_name + "', ");
        list.add("a.user_end = '" + user_finish_time + "',a.user_start = '" + user_start_time + "' ");
        list.add(" WHERE id = '" + custom_uuid + "' ");
        String updateSqkl = StringUtils.join(list, "");
        ExecResult execResult = jsonResponse.getExecResult(updateSqkl, null);
        return execResult;
    }

    @Override
    public ExecResult getAllCustomer() {
        String allCustomer = "SELECT * FROM sys_customer";
        ExecResult execResult = jsonResponse.getSelectResult(allCustomer, null, "");
        return execResult;
    }

    @Override
    public ExecResult getCustomerById(String customerId) {
        String customerInfo = "SELECT * FROM sys_customer WHERE id = '" + customerId + "' ";
        ExecResult execResult = jsonResponse.getSelectResult(customerInfo, null, "");
        return execResult;
    }

    @Override
    public JSONObject deleteCustomerInfo(String id) {
        List list = new ArrayList();
        String[] idS = id.split(",");
        int idSLen = idS.length;
        for (int i = 0; i < idSLen; i++) {
            list.add("DELETE FROM yuqing_user WHERE id = " + idS[i]);
            list.add("DELETE FROM base_yuqing_user WHERE base_id = " + idS[i]);
        }
        ExecResult execResult = jsonResponse.getExecResult(list, "", "");
        JSONObject jsonObject = new JSONObject();
        if (execResult.getResult() == 0) {
            jsonObject.put("value", "false");
        } else {
            jsonObject.put("value", "true");
        }
        jsonObject.put("key", "delete");
        return jsonObject;
    }

    @Override
    public JSONObject insertCutomerId(String id) {
        String insertSql = "INSERT INTO  yuqing_user (id) VALUES('" + id + "') ";
        ExecResult execResult = jsonResponse.getExecResult(insertSql, null);
        JSONObject jsonObject = new JSONObject();
        if (execResult.getResult() == 0) {
            jsonObject.put("value", "false");
        } else {
            jsonObject.put("value", "true");
        }
        jsonObject.put("key", "insert");
        return jsonObject;
    }

    @Override
    public void refreshData() {
        long startTime = System.currentTimeMillis();//获取当前时间
        JSONResponse jsonResponse = new JSONResponse();
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        Map<String, String> map = new HashMap<>();
        map.put("filters", "");
        map.put("start", "0");
        map.put("limit", "100000");
        JSONObject jsonObject = httpClientUtil.sendPost("http://yq.yuwoyg.com:8080/yuqing-app-dict/dict/users", map);
        JSONArray jsonArray = (JSONArray) JSON.toJSON(jsonObject.get("value"));
        int jsonArrayLen = jsonArray.size();
        String sql = "SELECT * FROM base_yuqing_user";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONArray jsonArray1 = (JSONArray) execResult.getData();
        for (int i = 0; i < jsonArrayLen; i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            qqAsyncMessagePost.insertData(jsonObject1, jsonArray1);
            /*int j;
            for (j = 0; j < jsonArray1Len; j++) {
                JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                String ids = jsonObject2.getString("base_id");
                if (id.equals(ids)) {
                    break;
                }
            }
            if (j == jsonArray1Len) {
                list.add("INSERT INTO base_yuqing_user  (base_id,base_user_name,base_start_time,base_end_time) " +
                        "VALUES ('" + jsonObject1.getString("id") + "','" + jsonObject1.getString("name") + "'" +
                        ",'" + jsonObject1.getString("add_time") + "','" + jsonObject1.getString("end_date") + "')");
                System.out.println("插入了数据");
            }*/

            /*list.add("INSERT INTO base_yuqing_user  (base_id,base_user_name,base_start_time,base_end_time) " +
                    "VALUES ('" + jsonObject1.getString("id") + "','" + jsonObject1.getString("name") + "'" +
                    ",'" + jsonObject1.getString("add_time") + "','" + jsonObject1.getString("end_date") + "')");
            System.out.println("插入了数据");*/
        }
        long endTime = System.currentTimeMillis();
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
    }

    @Override
    public ExecResult insertInformation(String content, String postType,
                                    String postPeople, String receivePeople) {
        String insertSql = "INSERT INTO (infor_contxt, infor_post_type, " +
                "infor_post_people, infor_get_people) VALUES('','','','')";
        ExecResult execResult = jsonResponse.getExecResult(insertSql, null);
        return execResult;
    }
}
