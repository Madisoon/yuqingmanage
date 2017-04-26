package com.syx.yuqingmanage.module.infor.service.imp;

import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.infor.service.IInForPostService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Msater Zg on 2017/4/20.
 */
@Service
public class InForPostService implements IInForPostService {
    @Autowired
    private JSONResponse jsonResponse;

    @Override
    public ExecResult getInforPost() {
        List list = new ArrayList();
        list.add("SELECT * FROM (SELECT a.*,b.qq_name AS number_name,c.get_remark FROM  sys_manual_post a ,sys_qq b ");
        list.add(",sys_customer_get c WHERE a.infor_status = 0  AND a.infor_post_people = b.qq_number AND ");
        list.add("a.infor_get_people = c.get_number AND (a.infor_post_type = 'qq' OR a.infor_post_type = 'qqGroup') ");
        list.add("GROUP BY a.id ");
        list.add("UNION ");
        list.add("SELECT a.*,b.wx_name AS number_name,c.get_remark FROM  sys_manual_post a ,sys_weixin b ,sys_customer_get c ");
        list.add("WHERE a.infor_status = 0  AND a.infor_post_people = b.wx_number AND ");
        list.add("a.infor_get_people = c.get_number AND (a.infor_post_type = 'weixin' OR a.infor_post_type = 'weixinGroup') ");
        list.add("GROUP BY a.id ) a  ORDER BY a.infor_priority DESC ,a.infor_post_time  ");
        String getInforPost = StringUtils.join(list, "");
        ExecResult execResult = jsonResponse.getSelectResult(getInforPost, null, "");
        return execResult;
    }

    @Override
    public ExecResult updateInforPost(String id, String loginName) {
        List list = new ArrayList();
        list.add("UPDATE sys_manual_post a SET a.infor_status = '1' , ");
        list.add("a.infor_finish_time = NOW(),a.infor_people = '" + loginName + "' WHERE a.id = '" + id + "'  ");
        String sqlUpdate = StringUtils.join(list, "");
        ExecResult execResult = jsonResponse.getExecResult(sqlUpdate, null);
        return execResult;
    }
}