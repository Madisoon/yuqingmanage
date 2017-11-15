package com.syx.yuqingmanage.module.app.service;

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
}
