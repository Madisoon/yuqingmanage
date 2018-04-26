package com.syx.yuqingmanage.module.infor.service.imp;

import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.infor.service.IInForPostService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.attribute.standard.Finishings;
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
    public ExecResult getInforPost(String userLoginName, String sortType) {
        List list = new ArrayList();
        final String sortTypeStr = "0";
        if (sortTypeStr.equals(sortType)) {
            // 就是不排序
            list.add("SELECT DISTINCT a.infor_post_type, a.id,a.infor_get_people,b.qq_name AS number_name,c.get_remark, " +
                    "d.infor_site,d.infor_link,d.infor_context,d.infor_title FROM  sys_manual_post a ,sys_qq b " +
                    ",sys_customer_get c, sys_infor d WHERE a.infor_status = 0  AND a.infor_post_people = b.qq_number AND " +
                    "a.infor_get_people = c.get_number AND a.infor_id = d.id AND (a.infor_post_type = 'qq' OR a.infor_post_type = 'qqGroup') " +
                    "UNION ALL " +
                    "SELECT DISTINCT a.infor_post_type, a.id,a.infor_get_people,b.wx_name AS number_name,c.get_remark, " +
                    "d.infor_site,d.infor_link,d.infor_context,d.infor_title FROM  sys_manual_post a ,sys_weixin b " +
                    ",sys_customer_get c, sys_infor d WHERE a.infor_status = 0  AND a.infor_post_people = b.wx_number AND " +
                    "a.infor_get_people = c.get_number AND a.infor_id = d.id AND (a.infor_post_type = 'weixin' OR a.infor_post_type = 'weixinGroup') ");
        } else {
            list.add("SELECT * FROM (SELECT DISTINCT a.infor_post_type, a.id,a.infor_get_people,b.qq_name AS number_name,c.get_remark, " +
                    "d.infor_site,d.infor_link,d.infor_context,d.infor_title FROM  sys_manual_post a ,sys_qq b " +
                    ",sys_customer_get c, sys_infor d WHERE a.infor_status = 0  AND a.infor_post_people = b.qq_number AND " +
                    "a.infor_get_people = c.get_number AND a.infor_id = d.id AND (a.infor_post_type = 'qq' OR a.infor_post_type = 'qqGroup') " +
                    "UNION ALL " +
                    "SELECT DISTINCT a.infor_post_type, a.id,a.infor_get_people,b.wx_name AS number_name,c.get_remark, " +
                    "d.infor_site,d.infor_link,d.infor_context,d.infor_title FROM  sys_manual_post a ,sys_weixin b " +
                    ",sys_customer_get c, sys_infor d WHERE a.infor_status = 0  AND a.infor_post_people = b.wx_number AND " +
                    "a.infor_get_people = c.get_number AND a.infor_id = d.id AND (a.infor_post_type = 'weixin' OR a.infor_post_type = 'weixinGroup') ) a " +
                    "ORDER BY a.infor_get_people");
        }
        String getInforPost = StringUtils.join(list, "");
        ExecResult execResult = jsonResponse.getSelectResult(getInforPost, null, "");
        return execResult;
    }

    @Override
    public ExecResult updateInforPost(String id, String loginName) {
        List list = new ArrayList();
        list.add("UPDATE sys_manual_post a SET a.infor_status = 1, " +
                "a.gmt_modified = now() WHERE a.id = " + id + " ");
        String sqlUpdate = StringUtils.join(list, "");
        ExecResult execResult = jsonResponse.getExecResult(sqlUpdate, null);
        return execResult;
    }

    @Override
    public ExecResult deleteInforPost(String id) {
        String deleteInfo = "DELETE FROM sys_manual_post WHERE id = " + id;
        ExecResult execResult = jsonResponse.getExecResult(deleteInfo, null);
        return execResult;
    }
}
