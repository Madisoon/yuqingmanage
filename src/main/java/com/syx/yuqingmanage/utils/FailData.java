package com.syx.yuqingmanage.utils;

import com.alienlab.response.JSONResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Msater Zg on 2017/3/30.
 */
public class FailData {
    JSONResponse jsonResponse = new JSONResponse();

    public void qqResend(String getNumber, String postNumber, String type, String title, String context, String link, String source) {
        //插入到一个库里面
        List list = new ArrayList();
        list.add("INSERT INTO qq_post_fail (info_title,info_content,info_link,info_source,post_number,get_number,post_type) VALUES ");
        list.add(" ('" + title + "','" + context + "','" + link + "','" + source + "','" + postNumber + "','" + getNumber + "','" + type + "') ");

        String sqlInsert = StringUtils.join(list, "");
        jsonResponse.getExecInsertId(sqlInsert, null, "", "");
    }
}
