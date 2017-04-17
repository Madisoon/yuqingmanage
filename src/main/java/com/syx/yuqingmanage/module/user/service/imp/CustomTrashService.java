package com.syx.yuqingmanage.module.user.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alienlab.db.ExecResult;
import com.alienlab.response.JSONResponse;
import com.syx.yuqingmanage.module.user.service.ICustomTrashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Msater Zg on 2016/12/19.
 */
@Service
public class CustomTrashService implements ICustomTrashService {
    @Autowired
    private JSONResponse jsonResponse;

    @Override
    public JSONObject getAllTrashUser() {
        String sql = "SELECT * FROM delete_custom";
        ExecResult execResult = jsonResponse.getSelectResult(sql, null, "");
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = (JSONArray) execResult.getData();
        if (jsonArray == null) {
            jsonObject.put("total", "0");
        } else {
            jsonObject.put("total", jsonArray.size());
        }
        jsonObject.put("data", jsonArray);
        return jsonObject;
    }
}
