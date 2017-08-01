package com.syx.yuqingmanage.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by Msater Zg on 2017/7/25.
 */
@Component
public class SysCookie {
    // 根据cookie的token获取到用户
    @Autowired
    JSONResponse jsonResponse;

    // 如果返回空就说明cookie过期了
    public String getUser(HttpServletRequest req) {
        // 获取到cookie
        String token = getToken(req);
        String loginName = "";
        if (token != null) {
            String sqlUser = "SELECT * FROM app_user a WHERE a.app_user_token = '" + token + "'";
            ExecResult execResult = jsonResponse.getSelectResult(sqlUser, null, "");
            JSONArray jsonArray = (JSONArray) execResult.getData();
            if (jsonArray != null) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                loginName = jsonObject.getString("app_user_loginname");
            }
        }
        return loginName;
    }

    public static String getToken(final HttpServletRequest req) {
        return getCookie(req, "token");
    }

    public static String getCookie(HttpServletRequest req, String field) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) {
            return "";
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(field)) {
                return cookie.getValue();
            }
        }
        return "";
    }
}
