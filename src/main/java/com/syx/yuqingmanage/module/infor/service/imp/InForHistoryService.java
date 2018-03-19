package com.syx.yuqingmanage.module.infor.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.infor.service.IInForHistoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
        String sql = "SELECT COUNT(a.id) AS total FROM sys_manual_post a WHERE a.infor_status = 1  ";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONArray dataAll = (JSONArray) execResult.getData();
        JSONObject jsonObject1 = dataAll.getJSONObject(0);
        List list = new ArrayList();
        list.add(" SELECT * FROM (SELECT a.*,b.qq_name AS number_name,c.get_remark,d.user_name FROM  (SELECT * FROM (SELECT * FROM  sys_manual_post ");
        list.add(" WHERE infor_status = 1  ORDER BY  infor_post_time DESC LIMIT " + ((pageNumberInt - 1) * pageSizeInt) + "," + pageSizeInt + " ) a ");
        list.add(" WHERE a.infor_post_type = 'qq' OR a.infor_post_type = 'qqGroup') a ,sys_qq b ");
        list.add(" ,sys_customer_get c,sys_user d  WHERE a.infor_post_people = b.qq_number AND ");
        list.add(" a.infor_get_people = c.get_number AND a.infor_people = d.user_loginname  GROUP BY a.id ");
        list.add(" UNION ");
        list.add(" SELECT a.*,b.wx_name AS number_name,c.get_remark,d.user_name FROM  (SELECT * FROM (SELECT * FROM  sys_manual_post ");
        list.add(" WHERE infor_status = 1  ORDER BY  infor_post_time DESC LIMIT " + ((pageNumberInt - 1) * pageSizeInt) + "," + pageSizeInt + " ) a ");
        list.add(" WHERE a.infor_post_type = 'weixin' OR a.infor_post_type = 'weixinGroup') a ,sys_weixin b ");
        list.add(" ,sys_customer_get c,sys_user d  WHERE a.infor_post_people = b.wx_number AND ");
        list.add(" a.infor_get_people = c.get_number AND a.infor_people = d.user_loginname  GROUP BY a.id ) a ORDER BY a.infor_finish_time DESC ");
        String dataSql = StringUtils.join(list, "");
        ExecResult execResult1 = jsonResponse.getSelectResult(dataSql, null, "");
        JSONArray jsonArray = (JSONArray) execResult1.getData();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", jsonArray);
        jsonObject.put("total", jsonObject1.getInteger("total"));
        return jsonObject;
    }

    @Override
    public JSONObject getChoiceHistory(String pageNumber, String pageSize, String tableChoiceData) {
        System.out.println(tableChoiceData);
        JSONObject jsonObject = JSON.parseObject(tableChoiceData);
        String finishTime = jsonObject.getString("infor_finish_time");
        jsonObject.remove("infor_finish_time");
        int pageNumberInt = Integer.parseInt(pageNumber, 10);
        int pageSizeInt = Integer.parseInt(pageSize, 10);
        JSONObject jsonObjectReturn = new JSONObject();
        ExecResult execResult = new ExecResult();
        String sqlData = "";
        if (jsonObject.isEmpty()) {
            String[] finishTimeS = finishTime.split("&");
            // 只有时间选择
            List list = new ArrayList();
            list.add(" SELECT * FROM (SELECT a.*,b.qq_name AS number_name,c.get_remark,d.user_name FROM  (SELECT * FROM (SELECT * FROM  sys_manual_post ");
            list.add(" WHERE infor_status = 1  AND  infor_finish_time>'" + finishTimeS[0] + "' AND infor_finish_time < '" + finishTimeS[1] + "' ");
            list.add(" ORDER BY  infor_post_time DESC LIMIT " + ((pageNumberInt - 1) * pageSizeInt) + "," + pageSizeInt + "  ) a ");
            list.add(" WHERE a.infor_post_type = 'qq' OR a.infor_post_type = 'qqGroup') a ,sys_qq b ");
            list.add(" ,sys_customer_get c,sys_user d  WHERE a.infor_post_people = b.qq_number AND ");
            list.add(" a.infor_get_people = c.get_number AND a.infor_people = d.user_loginname  GROUP BY a.id ");
            list.add(" UNION ");
            list.add(" SELECT a.*,b.wx_name AS number_name,c.get_remark,d.user_name FROM  (SELECT * FROM (SELECT * FROM  sys_manual_post ");
            list.add(" WHERE infor_status = 1  AND  infor_finish_time>'2017-06-01' AND infor_finish_time < '2017-06-21' ");
            list.add(" ORDER BY  infor_post_time DESC LIMIT " + ((pageNumberInt - 1) * pageSizeInt) + "," + pageSizeInt + "  ) a ");
            list.add(" WHERE a.infor_post_type = 'weixin' OR a.infor_post_type = 'weixinGroup') a ,sys_weixin b ");
            list.add(" ,sys_customer_get c,sys_user d  WHERE a.infor_post_people = b.wx_number AND ");
            list.add(" a.infor_get_people = c.get_number AND a.infor_people = d.user_loginname  GROUP BY a.id ) a ORDER BY a.infor_finish_time DESC ");
            sqlData = StringUtils.join(list, "");
            String sqlTotal = "SELECT COUNT(id) AS total FROM  sys_manual_post  WHERE infor_status = 1  AND " +
                    "infor_finish_time>'" + finishTimeS[0] + "' AND infor_finish_time < '" + finishTimeS[1] + "'";
            execResult = jsonResponse.getSelectResult(sqlTotal, null, "");
            JSONArray jsonArray = (JSONArray) execResult.getData();
            JSONObject jsonObjectLen = jsonArray.getJSONObject(0);
            jsonObjectReturn.put("total", jsonObjectLen.getInteger("total"));
        } else {
            Set set = jsonObject.keySet();
            Iterator<String> iterator = set.iterator();
            List list = new ArrayList();
            list.add("SELECT * FROM (SELECT a.*,b.qq_name AS number_name,c.get_remark,d.user_name,e.customer_name FROM  sys_manual_post a ,sys_qq b ");
            list.add(",sys_customer_get c,sys_user d,sys_post_customer e WHERE a.infor_post_people = b.qq_number AND ");
            list.add("a.infor_get_people = c.get_number AND a.infor_people = d.user_loginname AND c.post_customer_id = e.id ");
            list.add("AND (a.infor_post_type = 'qq' OR a.infor_post_type = 'qqGroup') ");
            list.add("GROUP BY a.id ");
            list.add("UNION ");
            list.add("SELECT a.*,b.wx_name AS number_name,c.get_remark,d.user_name,e.customer_name FROM  sys_manual_post a ,sys_weixin b ,sys_customer_get c, ");
            list.add("sys_user d,sys_post_customer e WHERE a.infor_post_people = b.wx_number AND ");
            list.add("a.infor_get_people = c.get_number AND a.infor_people = d.user_loginname AND c.post_customer_id = e.id ");
            list.add(" AND (a.infor_post_type = 'weixin' OR a.infor_post_type = 'weixinGroup') ");
            list.add(" GROUP BY a.id ) a WHERE a.infor_status = 1 ");
            while (iterator.hasNext()) {
                String value = iterator.next();
                list.add("AND a." + value + " LIKE '%" + jsonObject.getString(value) + "%' ");
            }
            if (!"".equals(finishTime) && finishTime != null) {
                String[] finishTimeS = finishTime.split("&");
                list.add("AND a.infor_finish_time > '" + finishTimeS[0] + "' AND a.infor_finish_time < '" + finishTimeS[1] + "' ");
            }
            list.add("ORDER BY a.infor_finish_time DESC ");
            String sqlTotal = StringUtils.join(list, "");
            execResult = jsonResponse.getSelectResult(sqlTotal, null, "");
            JSONArray jsonArray = (JSONArray) execResult.getData();
            jsonObjectReturn.put("total", jsonArray.size());
            list.add("LIMIT " + ((pageNumberInt - 1) * pageSizeInt) + "," + pageSizeInt + " ");
            sqlData = StringUtils.join(list, "");
        }
        execResult = jsonResponse.getSelectResult(sqlData, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        jsonObjectReturn.put("data", jsonArray);
        return jsonObjectReturn;
    }

    @Override
    public JSONObject exportHistoryInfor(String searchData) {
        JSONObject jsonObject = JSON.parseObject(searchData);
        String finishTime = jsonObject.getString("infor_finish_time");
        jsonObject.remove("infor_finish_time");
        JSONObject jsonObjectData = new JSONObject();
        ExecResult execResult = new ExecResult();
        String sqlData = "";
        if (jsonObject.isEmpty()) {
            String[] finishTimeS = finishTime.split("&");
            // 只有时间选择
            List list = new ArrayList();
            list.add("SELECT * FROM (SELECT a.*,b.qq_name AS number_name,c.get_remark,d.user_name FROM  sys_manual_post a ,sys_qq b ");
            list.add(",sys_customer_get c,sys_user d  WHERE a.infor_status = 1  AND a.infor_post_people = b.qq_number AND ");
            list.add("a.infor_get_people = c.get_number AND a.infor_people = d.user_loginname AND (a.infor_post_type = 'qq' ");
            list.add("OR a.infor_post_type = 'qqGroup') AND a.infor_finish_time > '" + finishTimeS[0] + "' AND a.infor_finish_time < '" + finishTimeS[1] + "' ");
            list.add("GROUP BY a.id ");
            list.add("UNION ");
            list.add("SELECT a.*,b.wx_name AS number_name,c.get_remark,d.user_name FROM  sys_manual_post a ,sys_weixin b ,sys_customer_get c, ");
            list.add("sys_user d  WHERE a.infor_status = 1  AND a.infor_post_people = b.wx_number AND ");
            list.add("a.infor_get_people = c.get_number AND a.infor_people = d.user_loginname AND (a.infor_post_type = 'weixin' ");
            list.add("OR a.infor_post_type = 'weixinGroup') AND a.infor_finish_time > '" + finishTimeS[0] + "' AND a.infor_finish_time < '" + finishTimeS[1] + "' ");
            list.add("GROUP BY a.id ) a ORDER BY a.infor_finish_time DESC ");
            sqlData = StringUtils.join(list, "");
        } else {
            Set set = jsonObject.keySet();
            Iterator<String> iterator = set.iterator();
            List list = new ArrayList();
            list.add("SELECT * FROM (SELECT a.*,b.qq_name AS number_name,c.get_remark,d.user_name,e.customer_name FROM  sys_manual_post a ,sys_qq b ");
            list.add(",sys_customer_get c,sys_user d,sys_post_customer e WHERE a.infor_post_people = b.qq_number AND ");
            list.add("a.infor_get_people = c.get_number AND a.infor_people = d.user_loginname AND c.post_customer_id = e.id ");
            list.add("AND (a.infor_post_type = 'qq' OR a.infor_post_type = 'qqGroup') ");
            list.add("GROUP BY a.id ");
            list.add("UNION ");
            list.add("SELECT a.*,b.wx_name AS number_name,c.get_remark,d.user_name,e.customer_name FROM  sys_manual_post a ,sys_weixin b ,sys_customer_get c, ");
            list.add("sys_user d,sys_post_customer e WHERE a.infor_post_people = b.wx_number AND ");
            list.add("a.infor_get_people = c.get_number AND a.infor_people = d.user_loginname AND c.post_customer_id = e.id ");
            list.add(" AND (a.infor_post_type = 'weixin' OR a.infor_post_type = 'weixinGroup') ");
            list.add(" GROUP BY a.id ) a WHERE a.infor_status = 1 ");
            while (iterator.hasNext()) {
                String value = iterator.next();
                list.add("AND a." + value + " LIKE '%" + jsonObject.getString(value) + "%' ");
            }
            if (!"".equals(finishTime) && finishTime != null) {
                String[] finishTimeS = finishTime.split("&");
                list.add("AND a.infor_finish_time > '" + finishTimeS[0] + "' AND a.infor_finish_time < '" + finishTimeS[1] + "' ");
            }
            list.add("ORDER BY a.infor_finish_time DESC ");
            sqlData = StringUtils.join(list, "");
        }
        System.out.println(sqlData);
        execResult = jsonResponse.getSelectResult(sqlData, null, "");
        JSONArray jsonArray = (JSONArray) execResult.getData();
        JSONObject jsonObjectReturn = new JSONObject();
        if (jsonArray == null) {
            jsonObjectReturn.put("result", "0");
        } else {
            // 执行导出

        }
        return jsonObjectReturn;
    }
}
