package com.syx.yuqingmanage.module.appmodule.service;

        import com.alienlab.db.ExecResult;

/**
 * Created by Master  Zg on 2016/11/9.
 */
public interface ISysAppUserService {

    public ExecResult insertAppUser(String appUserInfo, String appUserProgram);

    public ExecResult deleteAppUser(String userLoginName);

    public ExecResult updateAppUser(String userLoginName, String appUserInfo, String appUserProgram);

    public ExecResult getAllAppUser(String pageSize, String pageNumber);
}
