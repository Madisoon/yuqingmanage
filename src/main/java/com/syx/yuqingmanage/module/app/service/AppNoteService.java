package com.syx.yuqingmanage.module.app.service;

import com.alibaba.fastjson.JSONObject;

/**
 * 描述:
 * APP日志API
 *
 * @author Msater Zg
 * @create 2017-11-15 16:48
 */
public interface AppNoteService {
    /**
     * 插入日志
     *
     * @param noteTitle
     * @param noteType
     * @param noteModule
     * @param noteCreate
     */
    public void insertAppNote(String noteTitle, String noteType, String noteModule, String noteCreate);

    /**
     * 得到所有的app日志（后台实现分页）
     *
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public JSONObject getAllAppNote(String pageNumber, String pageSize);

    /**
     * 得到搜索的app日志结果(后台分页)
     *
     * @param chooseData
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public JSONObject getAllAppNoteChoose(String chooseData, String pageNumber, String pageSize);

    public String exportAppNoteExcel(String chooseData);
}
