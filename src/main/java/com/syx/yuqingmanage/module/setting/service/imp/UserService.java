package com.syx.yuqingmanage.module.setting.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.alienlab.utils.Md5Azdg;
import com.syx.yuqingmanage.module.setting.service.IDepService;
import com.syx.yuqingmanage.module.setting.service.IRoleService;
import com.syx.yuqingmanage.module.setting.service.IUserService;
import com.syx.yuqingmanage.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Master  Zg on 2016/11/24.
 */
@Service
public class UserService implements IUserService {
    @Autowired
    private JSONResponse jsonResponse;

    @Autowired
    private IDepService iDepService;

    @Autowired
    private IRoleService iRoleService;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public ExecResult getUserInfo(String userLoginName) {
        String sql = "SELECT a.*,b.role_name FROM sys_user a ,sys_role b ,sys_role_user c WHERE a.user_loginname = '" + userLoginName + "' AND a.user_loginname = c.user_id AND b.id = c.role_id";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        return execResult;
    }

    @Override
    public ExecResult getUserAllInfo(String userLoginName, String userPwd) {
        ExecResult execResult = getUserInfo(userLoginName);
        JSONObject jsonObjectData = new JSONObject();
        ExecResult dataReturn = new ExecResult();
        if (execResult.getResult() == 0) {
            dataReturn.setResult(false);
        } else {
            JSONArray jsonArray = (JSONArray) execResult.getData();
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            //密码会采用md5加密的方式
            if (jsonObject.getString("user_password").equals(Md5Azdg.md5s(userPwd))) {
                //密码匹配 得到菜单等一些其他的信息
                //放入用户的基础信息

                /*String jwtToken = jwtUtils.createJWT(userLoginName, "14", "服务管理平台", "zg", "yuqingmanage", 200, "091418wa");
                Claims claims = jwtUtils.parseJWT(jwtToken, "091418wa!");*/
                dataReturn.setResult(true);
                /*System.out.println(jwtToken);
                System.out.println(claims.toString());*/
                jsonObjectData.put("user", jsonObject);
                /*jsonObjectData.put("token", jwtToken);*/
                List<String> listModule = new ArrayList<>();
                listModule.add("SELECT f.menu_id,f.menu_pid,f.menu_name,f.menu_content,f.menu_attr ");
                listModule.add("FROM sys_user a,sys_role_user b,sys_role c,sys_role_menu d,sys_menu f  ");
                listModule.add("WHERE a.user_loginname = '" + userLoginName + "'  ");
                listModule.add("AND a.user_loginname = b.user_id AND b.role_id = c.id  ");
                listModule.add("AND b.role_id = d.role_id AND d.menu_id = f.menu_id  ");
                listModule.add("AND f.menu_pid = 0 ");

                List<String> listFunction = new ArrayList<>();
                listFunction.add("SELECT f.menu_id,f.menu_pid,f.menu_name,f.menu_content,f.menu_attr ");
                listFunction.add("FROM sys_user a,sys_role_user b,sys_role c,sys_role_menu d,sys_menu f  ");
                listFunction.add("WHERE a.user_loginname = '" + userLoginName + "'  ");
                listFunction.add("AND a.user_loginname = b.user_id AND b.role_id = c.id  ");
                listFunction.add("AND b.role_id = d.role_id AND d.menu_id = f.menu_id  ");
                listFunction.add("AND f.menu_pid <> 0 ");
                String listModuleString = StringUtils.join(listModule, "");
                String listFunctionString = StringUtils.join(listFunction, "");
                ExecResult execResultModule = jsonResponse.getSelectResult(listModuleString, null, "");
                ExecResult execResultFunction = jsonResponse.getSelectResult(listFunctionString, null, "");
                JSONArray jsonArrayModule = (JSONArray) execResultModule.getData();
                JSONArray jsonArrayFunction = (JSONArray) execResultFunction.getData();
                jsonObjectData.put("module", jsonArrayModule);

                JSONObject jsonObjectFunction = new JSONObject();

                for (int i = 0, len = jsonArrayModule.size(); i < len; i++) {
                    JSONObject jsModule = jsonArrayModule.getJSONObject(i);
                    JSONArray arryObject = new JSONArray();
                    String menu_id = jsModule.getString("menu_id");
                    for (int j = 0, lenF = jsonArrayFunction.size(); j < lenF; j++) {
                        JSONObject jsFunction = jsonArrayFunction.getJSONObject(j);
                        if (menu_id.equals(jsFunction.getString("menu_pid"))) {
                            arryObject.add(jsFunction);
                        }
                    }
                    jsonObjectFunction.put(menu_id, arryObject);

                }
                jsonObjectData.put("function", jsonObjectFunction);

            } else {
                //密码不匹配
                dataReturn.setResult(false);
            }
        }
        dataReturn.setData(jsonObjectData);
        return dataReturn;
    }

    @Override
    /*@Cacheable("AllUser")*/
    public JSONObject getAllUser() {
        System.out.println("执行这个方法");
        List<String> list = new ArrayList<>();
        list.add(" SELECT a.*,b.dep_name,d.role_name,d.id as user_role  FROM sys_user a , sys_deparment b,sys_role_user c ,sys_role d  ");
        list.add(" WHERE a.user_dep = b.dep_no AND a.user_loginname = c.user_id AND c.role_id = d.id ORDER BY a.user_createtime DESC ");
        ExecResult execResult = jsonResponse.getSelectResult(StringUtils.join(list, ""), null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", jsonArray);
        jsonObject.put("total", jsonArray.size());
        return jsonObject;
    }

    @Override
    public ExecResult getAllDepRole() {
        ExecResult execResult = new ExecResult();
        JSONArray jsonArrayDep = (JSONArray) iDepService.getAllDep().getData();
        JSONArray jsonArrayRole = (JSONArray) iRoleService.getAllRole().getData();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dep", jsonArrayDep);
        jsonObject.put("role", jsonArrayRole);
        execResult.setResult(true);
        execResult.setData(jsonObject);
        return execResult;
    }

    @Override
    public ExecResult insertSysUser(String userData) {
        JSONObject jsonObject = new JSONObject();
        jsonObject = JSONObject.parseObject(userData);
        String user_loginname = jsonObject.getString("user_loginname");
        String user_name = jsonObject.getString("user_name");
        String user_phone = jsonObject.getString("user_phone");
        String user_dep = jsonObject.getString("user_dep");
        String user_role = jsonObject.getString("user_role");
        String passWord = Md5Azdg.md5s(jsonObject.getString("user_password"));
        String sql = "INSERT INTO sys_role_user (role_id,user_id) VALUES('" + user_role + "','" + user_loginname + "')";
        List<String> list = new ArrayList<>();
        list.add("INSERT INTO sys_user (user_loginname,user_password,user_name,user_phone,user_dep) ");
        list.add("VALUES('" + user_loginname + "','" + passWord + "','" + user_name + "','" + user_phone + "','" + user_dep + "')");
        List<String> sqlList = new ArrayList<>();
        sqlList.add(StringUtils.join(list, ""));
        sqlList.add(sql);
        ExecResult execResult = jsonResponse.getExecResult(sqlList, "成功", "失败");
        return execResult;
    }

    @Override
    public ExecResult deleteUser(String id) {
        String[] ids = id.split("@");
        int idsLen = ids.length;
        List<String> listSql = new ArrayList<>();
        for (int i = 0; i < idsLen; i++) {
            listSql.add("DELETE FROM sys_user WHERE id='" + ids[i] + "'");
            String sql = "SELECT * FROM sys_user WHERE id = '" + ids[i] + "'";
            ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
            JSONArray jsonArray = (JSONArray) execResult.getData();
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            listSql.add("DELETE FROM sys_role_user WHERE user_id='" + jsonObject.getString("user_loginname") + "'");
        }
        ExecResult execResult = jsonResponse.getExecResult(listSql, "成功", "失败");
        return execResult;
    }

    @Override
    public ExecResult updateUserInfo(String userData) {
        ExecResult execResult = new ExecResult();
        JSONObject jsonObject = JSON.parseObject(userData);
        System.out.println(userData);
        if (jsonObject.getString("user_role") != null && !"".equals(jsonObject.getString("user_role"))) {
            String sql = "UPDATE sys_role_user SET role_id = '" + jsonObject.getString("user_role") + "' WHERE user_id = '" + jsonObject.getString("user_loginname") + "'";
            jsonResponse.getExecResult(sql, null);
        }
        if (jsonObject.getString("user_password") != null && !"".equals(jsonObject.getString("user_password"))) {
            String userPassword = jsonObject.getString("user_password");
            jsonObject.put("user_password", Md5Azdg.md5s(userPassword));
        }
        Set<String> set = jsonObject.keySet();
        Iterator<String> iterator = set.iterator();
        List<String> list = new ArrayList<>();
        list.add("UPDATE sys_user SET ");
        int i = 0;
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (!"user_role".equals(key)) {
                if (i == 0) {
                    list.add("" + key + " = '" + jsonObject.getString(key) + "' ");
                } else {
                    list.add("," + key + " = '" + jsonObject.getString(key) + "' ");
                }
                i++;
            }
        }
        list.add("WHERE user_loginname =  '" + jsonObject.getString("user_loginname") + "'");
        execResult = jsonResponse.getExecResult(StringUtils.join(list, ""), null);
        return execResult;
    }
}
