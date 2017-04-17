package com.syx.yuqingmanage.module.setting.service;

import com.alienlab.db.ExecResult;
import org.springframework.stereotype.Service;

/**
 * Created by Master  Zg on 2016/11/8.
 */
public interface IDepService {
    /**
     * 获取所有部门信息
     * @return ExecResult
     */
    public ExecResult getAllDep();

    /**
     * 添加一个新的部门
     * @return ExecResult
     */
    public ExecResult postDepData(String dep_name, String dep_no);

    /**
     * 删除部门
     * @return ExecResult
     */
    public ExecResult deleteById(String dep_id);

    /**
     * 根据部门获取所有的人
     * @return ExecResult
     */
    public ExecResult getUserByDepNo(String dep_no);

    /**
     * 删除部门
     * @param depName
     * @param depNo
     * @param depId
     * @return
     */
    public ExecResult updateDep(String depName, String depNo, String depId);
}
