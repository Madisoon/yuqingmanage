package com.syx.yuqingmanage.module.app.service.imp;

import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.app.service.AppNoteService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 描述:
 * APP日志接口实现
 *
 * @author Msater Zg
 * @create 2017-11-15 16:51
 */
public class AppNoteServiceImpl implements AppNoteService {
    @Autowired
    JSONResponse jsonResponse;

    @Override
    public void insertAppNote(String noteTitle, String noteType, String noteModule, String noteCreate) {
        String insertSql = "INSERT INTO sys_app_note (note_title, note_type, note_module, note_create) " +
                "VALUES ('" + noteTitle + "', '" + noteType + "', '" + noteModule + "', '" + noteCreate + "')";
        jsonResponse.getExecResult(insertSql, null);
    }
}
