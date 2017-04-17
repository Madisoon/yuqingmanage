package com.syx.yuqingmanage.module.app.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.app.service.IAppService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Master  Zg on 2016/12/12.
 */
@Service
public class AppService implements IAppService {
    @Autowired
    private JSONResponse jsonResponse;

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
    public ExecResult deleteCustomerInfo(String id) {
        String deleteSql = " DELETE FROM yuqing_user WHERE id = " + id;
        ExecResult execResult = jsonResponse.getExecResult(deleteSql, null);
        return execResult;
    }

    @Override
    public ExecResult insertCutomerId(String id) {
        String insertSql = " INSERT INTO  yuqing_user (id) VALUES('" + id + "') ";
        ExecResult execResult = jsonResponse.getExecResult(insertSql, null);
        return execResult;
    }

    @Override
    public void refreshData() {
        // 获取到所有的数据
    }
}
