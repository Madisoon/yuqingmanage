package com.syx.yuqingmanage.utils;

import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.app.service.imp.AppService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Msater Zg on 2017/2/17.
 */
public class CustomerScheduledJob extends QuartzJobBean {
    private JSONResponse jsonResponse = new JSONResponse();

    private AppService appService = new AppService();

    @Override
    protected void executeInternal(JobExecutionContext arg0)
            throws JobExecutionException {
        /*System.out.println("I am CustomerScheduledJob");*/
        judgeCustomer();
        getDataUser();
    }

    public void judgeCustomer() {
        Date nowTime = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String updateTime = simpleDateFormat.format(nowTime);
        String sql = "UPDATE sys_post_customer a SET customer_status = '0' WHERE a.customer_end_time < '" + updateTime + "'";
        jsonResponse.getExecResult(sql, null);
    }

    public void getDataUser() {
        appService.refreshData();
    }
}
