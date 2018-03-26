package com.syx.yuqingmanage.module.app.service;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import net.sf.ehcache.transaction.xa.EhcacheXAException;
import org.dozer.loader.xml.ExpressionElementReader;

/**
 * Created by Master  Zg on 2016/12/12.
 */
public interface IAppService {

    public ExecResult addUser(String userInfo);

    public ExecResult deleteUser(String id);

    public ExecResult updateUser(String userInfo);

    public ExecResult getAllCustomer();

    public ExecResult getCustomerById(String id);

    public JSONObject deleteCustomerInfo(String id);

    public JSONObject insertCutomerId(String id);

    public void refreshData();

    public JSONObject insertInformation(String data);

    public JSONObject getTerraceCustomerTag();

    public JSONObject insertSortingTag(String tagName, String tagId);

    public JSONObject deleteSortingTag(String tagId);
}
