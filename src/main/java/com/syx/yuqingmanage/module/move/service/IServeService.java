package com.syx.yuqingmanage.module.move.service;

import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;

import javax.print.DocFlavor;

/**
 * Created by Msater Zg on 2017/2/14.
 */
public interface IServeService {
    public ExecResult insertServeCustomer(String customerData, String getData, String areaId);

    public JSONObject getAllServeCustomer(String areaId);

    public ExecResult updateServeCustomer(String customerData, String getData, String schemeCustomerId);

    public ExecResult deleteServeCustomer(String serveCustomerId);

    public JSONObject getAllServeCustomerChoose(String areaId, String chooseData);
}
