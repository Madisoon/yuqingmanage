package com.syx.yuqingmanage.module.infor.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.infor.service.IInForHistoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Msater Zg on 2017/4/20.
 */
@Service
public class InForHistoryService implements IInForHistoryService {
    @Autowired
    private JSONResponse jsonResponse;

    @Override
    public JSONObject getAllHistory(String pageNumber, String pageSize) {
        int pageNumberInt = Integer.parseInt(pageNumber, 10);
        int pageSizeInt = Integer.parseInt(pageSize, 10);
        String sql = "SELECT * FROM sys_manual_post a WHERE a.infor_status = 1 ";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONArray dataAll = (JSONArray) execResult.getData();
        List list = new ArrayList();
        list.add("SELECT * FROM (SELECT a.*,b.qq_name AS number_name,c.get_remark FROM  sys_manual_post a ,sys_qq b ");
        list.add(",sys_customer_get c  WHERE a.infor_status = 1  AND a.infor_post_people = b.qq_number AND ");
        list.add("a.infor_get_people = c.get_number AND (a.infor_post_type = 'qq' OR a.infor_post_type = 'qqGroup')  ");
        list.add("GROUP BY a.id  ");
        list.add("UNION  ");
        list.add("SELECT a.*,b.wx_name AS number_name,c.get_remark FROM  sys_manual_post a ,sys_weixin b ,sys_customer_get c  ");
        list.add("WHERE a.infor_status = 1  AND a.infor_post_people = b.wx_number AND   ");
        list.add("a.infor_get_people = c.get_number AND (a.infor_post_type = 'weixin' OR a.infor_post_type = 'weixinGroup') ");
        list.add("GROUP BY a.id ) a ORDER BY a.infor_priority DESC ,a.infor_post_time  LIMIT " + ((pageNumberInt - 1) * pageSizeInt) + "," + pageSizeInt + " ");
        String dataSql = StringUtils.join(list, "");
        ExecResult execResult1 = jsonResponse.getSelectResult(dataSql, null, "");
        JSONArray jsonArray = (JSONArray) execResult1.getData();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", jsonArray);
        jsonObject.put("total", dataAll.size());
        return jsonObject;
    }
}