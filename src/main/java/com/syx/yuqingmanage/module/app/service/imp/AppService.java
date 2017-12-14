package com.syx.yuqingmanage.module.app.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.sun.xml.internal.xsom.impl.ListSimpleTypeImpl;
import com.syx.yuqingmanage.module.app.service.IAppService;
import com.syx.yuqingmanage.module.infor.service.imp.InForService;
import com.syx.yuqingmanage.utils.DifTimeGet;
import com.syx.yuqingmanage.utils.HttpClientUtil;
import com.syx.yuqingmanage.utils.MessagePost;
import com.syx.yuqingmanage.utils.QqAsyncMessagePost;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Master  Zg on 2016/12/12.
 */
@Service
public class AppService implements IAppService {
    @Autowired
    private JSONResponse jsonResponse;
    @Autowired
    QqAsyncMessagePost qqAsyncMessagePost;
    @Autowired
    InForService inForService;

    final static String TERRACE_URL = "http://sync.yuwoyg.com:58080/yuqing-allot-main/config/yuqingmanage/dep/get";

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
        }
        long endTime = System.currentTimeMillis();
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
    }

    @Override
    public JSONObject insertInformation(String data) {
        JSONObject jsonObject = JSON.parseObject(data);
        String title = jsonObject.getString("title");
        String content = jsonObject.getString("content");
        String grade = jsonObject.getString("level_id");
        String link = jsonObject.getString("source_url");
        String creater = jsonObject.getString("user_name");
        String source = jsonObject.getString("source");
        String site = jsonObject.getString("sub_site");
        String customerId = jsonObject.getString("dep_ids");
        String insertSql = "INSERT INTO sys_terrace_infor (infor_title, infor_context, infor_grade, " +
                "infor_link, infor_creater, " +
                "infor_source, infor_site) VALUES " +
                "('" + title + "','" + content + "','" + grade + "','" + link + "','" + creater + "','" + source + "','" + site + "')";
        ExecResult execResult = jsonResponse.getExecResult(insertSql, null);
        String[] customerIdS = customerId.split(",");
        int customerIdSLen = customerIdS.length;
        List list = new ArrayList();
        list.add("SELECT * FROM sys_scheme_terrace_tag a WHERE a.terrace_customer_id = " + customerIdS[0] + "");
        for (int i = 1; i < customerIdSLen; i++) {
            list.add(" OR a.terrace_customer_id = " + customerIdS[i] + " ");
        }

        list.add(" GROUP BY a.scheme_id ");
        ExecResult execResultScheme = jsonResponse.getSelectResult(StringUtils.join(list, ""), null, "");
        JSONArray jsonArrayScheme = (JSONArray) execResultScheme.getData();
        List<String> schemeIdList = new ArrayList<>();
        if (jsonArrayScheme != null) {
            int jsonArraySchemeLen = jsonArrayScheme.size();
            for (int i = 0; i < jsonArraySchemeLen; i++) {
                JSONObject jsonObjectScheme = jsonArrayScheme.getJSONObject(i);
                schemeIdList.add(jsonObjectScheme.getString("scheme_id"));
            }
        }
        JSONObject jsonObjectReturn = new JSONObject();
        JSONArray allCustomer = inForService.getAllCustomerByScheme(schemeIdList);
        if (allCustomer == null) {
            jsonObjectReturn.put("flag", false);
        } else {
            qqAsyncMessagePost.postCustomerMessage(allCustomer, content, title, link, source, creater, site);
        }
        jsonObjectReturn.put("flag", true);
        return jsonObjectReturn;
    }

    @Override
    public JSONObject getTerraceCustomerTag() {
        Map<String, String> map = new HashMap<>(16);
        JSONObject jsonObject = HttpClientUtil.postJsonData(TERRACE_URL, map);
        return jsonObject;
    }
}
