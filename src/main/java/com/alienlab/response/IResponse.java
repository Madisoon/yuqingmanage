package com.alienlab.response;

import java.util.List;

import com.alienlab.db.ExecResult;

public interface IResponse {
	/**
	 * 获得sql查询结果并转换成xml
	 * @param sql  查询的sql语句，支持带参数形式，参数使�?''{0}'',''{1}''形式，详见MessageFormat.format()用法
	 * @param params 取代sql语句中参数占位符的�?�，为字符串数组�?
	 * @param tableName 查询结果表名，可自定义�??
	 * @return 返回结果xml
	 */
	public  ExecResult getSelectResult(String sql, String[] params, String tableName);
	/**
	 * 获得sql执行结果
	 * @param sql �?要执行的sql语句
	 * @param params 执行sql时传入的参数
	 * @return 结果集的xml
	 */
	public  ExecResult getExecResult(String sql, String[] params);
	


	
	/**
	 * 获得SQL执行结果
	 * @param sql �?执行的语�?
	 * @param params 传入参数
	 * @param message 返回信息
	 * @return
	 */
	public  ExecResult getExecResult(String sql, String[] params, String smsg, String fmsg);
	
	/**
	 * 获得执行错误�?
	 * @param error 错误内容
	 * @return
	 */
	public ExecResult getError(String error);
	
	public ExecResult getExecResult(List<String> sql, String smsg, String fmsg);
	
	public ExecResult getExecInsertId(String sql, String[] params, String smsg, String fmsg);
}
