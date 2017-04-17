package com.alienlab.response;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alienlab.common.TypeUtils;
import com.alienlab.db.ExecResult;
import com.alienlab.db.JSONDataResult;

public class JSONResponse implements IResponse {

	public ExecResult getSelectResult(String sql, String[] params, String tableName) {
		SqlService dba = new SqlService();
		List<Map<String, Object>> result = dba.selectResult(sql, params);
		ExecResult er = new ExecResult();
		if (result == null) {
			er.setResult(false);
			er.setMessage("数据查询错误");
		} else {
			JSONDataResult jr = new JSONDataResult();

			JSONArray ja = jr.getJSONResult(result);
			if (ja.size() > 0) {
				er.setResult(true);
				er.setData(ja);
			} else {
				er.setResult(false);
				er.setMessage("数据为空");
			}
		}

		return er;
	}

	public ExecResult getExecResult(String sql, String[] params) {
		SqlService dba = new SqlService();
		boolean result = dba.executeResult(sql, params);
		ExecResult er = new ExecResult(result, null);
		return er;
	}

	public ExecResult getError(String error) {
		ExecResult er = new ExecResult(false, error);
		return er;
	}

	public ExecResult getExecResult(String sql, String[] params, String smsg, String fmsg) {
		SqlService dba = new SqlService();
		boolean result = dba.executeResult(sql, params);
		String message = result ? smsg : fmsg;
		ExecResult er = new ExecResult(result, message);
		return er;
	}

	public ExecResult getExecResult(List<String> sql, String smsg, String fmsg) {
		SqlService dba = new SqlService();
		boolean result = dba.execbatch(sql);
		String message = result ? smsg : fmsg;
		ExecResult er = new ExecResult(result, message);
		return er;
	}
	
	public ExecResult execCommand(String sql, String[] params, String smsg, String fmsg) {
		SqlService dba = new SqlService();
		boolean result = dba.execCommand(sql, params);
		System.out.println(result);
		String message = result ? smsg : fmsg;
		ExecResult er = new ExecResult(result, message);
		return er;
	}

	public ExecResult getExecInsertId(String sql, String[] params, String smsg, String fmsg) {
		SqlService dba = new SqlService();
		Object insertid = dba.getInsertId(sql, params);
		boolean result = (insertid != null);
		String message = result ? smsg : fmsg;
		ExecResult er = new ExecResult(result, message);
		if (result) {
			er.setMessage(TypeUtils.getString(insertid));
		}
		return er;
	}

}
